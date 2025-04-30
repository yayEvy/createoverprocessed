package net.createoverprocessed.forge.content.blocks;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
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

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRILL.get(), pos, state);
        tankInventory = createInventory();
        fluidCapability = LazyOptional.of(() -> tankInventory);
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        blaze = true;
    }


    protected SmartFluidTank createInventory() {
        return new SmartFluidTank((getCapacityMultiplier()), this::onFluidStackChanged);
    }


    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;
            if (tankInventory != null) {
                boolean isLava = newFluidStack.getFluid().defaultFluidState().is(FluidTags.LAVA);

                blaze = !newFluidStack.isEmpty() && isLava;

                setChanged();
                assert level != null;
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);

                level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, blaze), 2); // wont be here for a bit id lmdokjsansodkf


            }

    }

    public static int getCapacityMultiplier() {
        return 4000; // idk we cna change this later lmao
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return isPlayerSneaking;
    }

    public void tick() {
        if (level.isClientSide)
            return;

        if (tankInventory.getFluidAmount() > 0 && tankInventory.getFluid().getFluid().defaultFluidState().is(FluidTags.LAVA)) {
            tankInventory.drain(1, IFluidHandler.FluidAction.EXECUTE); // we'll adjust the drain drain later once more of this block is finalized
        } else {
            level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, false), 2);
        }
    }
    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Blaze", blaze);
        tag.putBoolean("Heated", getBlockState().getValue(GrillBlock.HEATED));
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        blaze = tag.getBoolean("Blaze");
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().setValue(GrillBlock.HEATED,
                    tag.getBoolean("Heated")), 2);
        }
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity be) {
        be.tick();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity be) {
        be.tick();
    }

}
