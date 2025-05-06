package net.createoverprocessed.forge.content.blocks;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.animation.LerpedFloat;
import net.createoverprocessed.forge.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GrillBlockEntity extends FluidTankBlockEntity implements IHaveGoggleInformation {
    public boolean blaze; // for the lil blaze guy :3
    public final LerpedFloat headAnimation = LerpedFloat.linear();
    public final LerpedFloat headAngle = LerpedFloat.linear();
    private boolean wasHeatedLastTick;
    private int soundDelay = 0;
    private final Random random = new Random();


    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> itemCapability;
    private TransportedItemStack heldItem;


    private long itemPlacedTime = 0L;
    private boolean itemAnimationActive = false;
    public float itemAnimationProgress = 0f;
    public float prevItemAnimationProgress = 0f;
    private boolean isAnimating = false;
    private static final float ANIMATION_SPEED = 0.05f;

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRILL.get(), pos, state);
        tankInventory = createInventory();
        fluidCapability = LazyOptional.of(() -> tankInventory);
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        blaze = false;
        wasHeatedLastTick = false;


        inventory = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (level != null)
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            }
        };
        itemCapability = LazyOptional.of(() -> inventory);
        heldItem = null;
    }

    public void startItemPlacementAnimation() {
        this.itemAnimationProgress = 0.0f;
        this.prevItemAnimationProgress = 0.0f;
        this.isAnimating = true;
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

        if (getHeldItemStack().isEmpty()) {
            tooltip.add(Component.literal("No Item"));
        } else {
            tooltip.add(Component.literal("Item: " + getHeldItemStack().getDisplayName().getString()));
        }
        return true;
    }

    @Override
    public void tick() {
        prevItemAnimationProgress = itemAnimationProgress;
        if (isAnimating) {
            itemAnimationProgress += ANIMATION_SPEED;
            if (itemAnimationProgress >= 1.0f) {
                itemAnimationProgress = 1.0f;
                isAnimating = false;
            }
        }

        if (level.isClientSide) {
            headAnimation.chase(blaze ? 1 : 0, 0.25f, LerpedFloat.Chaser.EXP);
            headAnimation.tickChaser();
            return;
        }

        boolean wasHeated = blaze;
        if (tankInventory.getFluidAmount() >= 1 && tankInventory.getFluid().getFluid().defaultFluidState().is(FluidTags.LAVA)) {
            tankInventory.drain(1, IFluidHandler.FluidAction.EXECUTE);
            blaze = true;
            level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, true), 2);
        } else {
            blaze = false;
            level.setBlock(getBlockPos(), getBlockState().setValue(GrillBlock.HEATED, false), 2);
        }

        if (blaze && !wasHeatedLastTick) {
            level.playSound(null, worldPosition, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 0.1F, 1.0F);
            soundDelay = 20 + random.nextInt(20);
        } else if (blaze) {
            if (--soundDelay <= 0) {
                float pitch = 0.9F + random.nextFloat() * 0.2F;
                level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS, 0.05F, pitch);
                level.playSound(null, worldPosition, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.05F, 0.8F + pitch * 0.2F);
                soundDelay = 60 + random.nextInt(60);
            }
        } else if (!blaze && wasHeatedLastTick) {
            level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 1.5F);
        }

        wasHeatedLastTick = blaze;


        if (!isRemoved() && level != null) {
            collectItemsFromWorld();
        }

        setChanged();
    }


    public float getItemAnimationProgress(float partialTicks) {
        return prevItemAnimationProgress + (itemAnimationProgress - prevItemAnimationProgress) * partialTicks;
    }


    private void collectItemsFromWorld() {
        if (!getHeldItemStack().isEmpty())
            return;
        AABB bb = new AABB(worldPosition).expandTowards(0, 0.5, 0);
        List<ItemEntity> itemsInRange = level.getEntitiesOfClass(ItemEntity.class, bb);
        for (ItemEntity itemEntity : itemsInRange) {
            if (!itemEntity.isAlive())
                continue;
            if (itemEntity.getItem().isEmpty())
                continue;
            ItemStack remainder = insertItemStack(itemEntity.getItem());
            if (remainder.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(remainder);
            }
        }
    }

    public ItemStack insertItemStack(ItemStack stack) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        if (getHeldItemStack().isEmpty()) {
            ItemStack toInsert = stack.copy();
            toInsert.setCount(1);
            inventory.setStackInSlot(0, toInsert);


            startItemPlacementAnimation();

            ItemStack remainder = stack.copy();
            remainder.shrink(1);
            return remainder;
        }
        return stack;
    }

    public ItemStack getHeldItemStack() {
        return inventory.getStackInSlot(0);
    }

    public ItemStack removeHeldItem() {
        if (getHeldItemStack().isEmpty())
            return ItemStack.EMPTY;
        ItemStack held = getHeldItemStack().copy();
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        itemAnimationProgress = 0f;
        prevItemAnimationProgress = 0f;
        isAnimating = false;
        setChanged();
        return held;
    }

    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
        itemCapability.invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        itemCapability.invalidate();
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Blaze", blaze);
        tag.putBoolean("Heated", getBlockState().getValue(GrillBlock.HEATED));
        tag.put("Inventory", inventory.serializeNBT());


        tag.putFloat("ItemAnimProgress", itemAnimationProgress);
        tag.putFloat("PrevItemAnimProgress", prevItemAnimationProgress);
        tag.putBoolean("IsAnimating", isAnimating);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        blaze = tag.getBoolean("Blaze");
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().setValue(GrillBlock.HEATED,
                    tag.getBoolean("Heated")), 2);
        }
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (clientPacket) {
            headAnimation.setValue(tag.getFloat("HeadAnim"));
        }


        if (tag.contains("ItemAnimProgress")) {
            itemAnimationProgress = tag.getFloat("ItemAnimProgress");
        }
        if (tag.contains("PrevItemAnimProgress")) {
            prevItemAnimationProgress = tag.getFloat("PrevItemAnimProgress");
        }
        if (tag.contains("IsAnimating")) {
            isAnimating = tag.getBoolean("IsAnimating");
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();
        return super.getCapability(cap, side);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity be) {
        be.tick();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity be) {
        be.tick();
    }


    public void setHeldItem(TransportedItemStack transportedStack) {
        if (transportedStack == null) {
            heldItem = null;
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            return;
        }
        heldItem = transportedStack;
        inventory.setStackInSlot(0, transportedStack.stack);
        startItemPlacementAnimation();
        setChanged();
    }

    public TransportedItemStack getHeldItem() {
        if (heldItem == null && !inventory.getStackInSlot(0).isEmpty()) {
            heldItem = new TransportedItemStack(inventory.getStackInSlot(0));
        }
        return heldItem;
    }

    public void removeHeldItem(boolean inWorld) {
        if (inWorld && !getHeldItemStack().isEmpty())
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), getHeldItemStack());
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        heldItem = null;
        setChanged();
    }

    public boolean isOutputEmpty() {
        return getHeldItemStack().isEmpty();
    }

    public boolean canAcceptItem(ItemStack stack) {
        return getHeldItemStack().isEmpty() && isItemValid(stack);
    }


    public boolean canBePlacedOnBelt() {
        return true;
    }


    public void handleEntityCollision(Entity entity) {
        if (blaze && entity instanceof ItemEntity itemEntity) {

        }
    }
}
