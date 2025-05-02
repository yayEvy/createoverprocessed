package net.createoverprocessed.forge.content.blocks;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.createoverprocessed.forge.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class GrillBlockEntity extends FluidTankBlockEntity implements IHaveGoggleInformation {


    public boolean blaze; // for the lil blaze guy :3
    public final LerpedFloat headAnimation = LerpedFloat.linear();
    public final LerpedFloat headAngle = LerpedFloat.linear();


    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRILL.get(), pos, state);
        tankInventory = createInventory();
        fluidCapability = LazyOptional.of(() -> tankInventory);
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        blaze = false; //BRUHHH
    }

    @Override
    public void initialize() {
        super.initialize();
        tankInventory.setCapacity(2000);
        updateConnectivity = false;
    }


    @Override
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(2000, this::onFluidStackChanged) {
            @Override
            public int getCapacity() {
                return 2000;
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().defaultFluidState().is(FluidTags.LAVA);
            }
        };
    }




    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        if (tankInventory != null) {
            if (newFluidStack.getAmount() > 1990) {
                newFluidStack.setAmount(2000);
            }

            if (newFluidStack.isEmpty()) {
                blaze = false;
            } else {
                boolean isLava = newFluidStack.getFluid().defaultFluidState().is(FluidTags.LAVA);
                blaze = isLava;
            }

            setChanged();
            assert level != null;
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, blaze), 2);
        }
    }



    public static int getCapacityMultiplier() {
        return 1000; // i think this is useless now
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        tooltip.add(Component.literal("Lava: " + tankInventory.getFluidAmount() + "mb"));

        if (blaze) {
            tooltip.add(Component.literal("Status: Heated"));
        } else {
            tooltip.add(Component.literal("Status: Not Heated"));
        }
        return true;
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            headAnimation.chase(blaze ? 1 : 0, 0.25f, LerpedFloat.Chaser.EXP);
            headAnimation.tickChaser();
            return;
        }

        if (tankInventory.getFluidAmount() >= 1 && tankInventory.getFluid().getFluid().defaultFluidState().is(FluidTags.LAVA)) {
            tankInventory.drain(1, IFluidHandler.FluidAction.EXECUTE);
            blaze = true;
            level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, true), 2);
        } else {
            blaze = false;
            level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, false), 2);
        }
        setChanged();
    }


    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Blaze", blaze);
        tag.putBoolean("Heated", getBlockState().getValue(GrillBlock.HEATED));
        if (clientPacket) {
            tag.putFloat("HeadAngle", headAngle.getValue());
            tag.putFloat("HeadAnim", headAnimation.getValue());
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        blaze = tag.getBoolean("Blaze");
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().setValue(GrillBlock.HEATED,
                    tag.getBoolean("Heated")), 2);
        }
        if (clientPacket) {
            headAngle.setValue(tag.getFloat("HeadAngle"));
            headAnimation.setValue(tag.getFloat("HeadAnim"));
        }
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity be) {
        be.tick();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity be) {
        be.tick();
    }

}
