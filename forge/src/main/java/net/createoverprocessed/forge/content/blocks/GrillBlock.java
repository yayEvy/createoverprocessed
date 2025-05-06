package net.createoverprocessed.forge.content.blocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.createoverprocessed.forge.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GrillBlock extends Block implements IWrenchable, IBE<GrillBlockEntity> {
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty HEATED = BooleanProperty.create("heated");
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    public GrillBlock(Properties arg) {
        super(arg);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HEATED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HEATED);
    }

    @Override
    public Class<GrillBlockEntity> getBlockEntityClass() {
        return GrillBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GrillBlockEntity> getBlockEntityType() {
        return ModBlockEntities.GRILL.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        ItemStack heldItem = player.getItemInHand(hand);
        GrillBlockEntity be = getBlockEntity(level, pos);
        if (be == null)
            return InteractionResult.PASS;


        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, heldItem, be))
            return InteractionResult.SUCCESS;
        if (FluidHelper.tryFillItemFromBE(level, player, hand, heldItem, be))
            return InteractionResult.SUCCESS;

        if (!be.getHeldItemStack().isEmpty()) {

            if (!player.getInventory().add(be.removeHeldItem())) {
                player.drop(be.removeHeldItem(), false);
            }
            return InteractionResult.SUCCESS;
        } else if (!heldItem.isEmpty()) {

            ItemStack toInsert = heldItem.copy();
            toInsert.setCount(1);
            if (be.isItemValid(toInsert)) {
                be.insertItemStack(toInsert);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            withBlockEntityDo(level, pos, GrillBlockEntity::destroy);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.GRILL.get(), level.isClientSide ?
                GrillBlockEntity::clientTick : GrillBlockEntity::serverTick);
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) {
        return typeB == typeA ? (BlockEntityTicker<A>) ticker : null;
    }
}
