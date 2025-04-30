package net.createoverprocessed.forge.registry;

import net.createoverprocessed.forge.CreateOverprocessed;
import net.createoverprocessed.forge.content.blocks.GrillBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateOverprocessed.MOD_ID);

    public static final RegistryObject<BlockEntityType<GrillBlockEntity>> GRILL =
            BLOCK_ENTITIES.register("grill", () ->
                    BlockEntityType.Builder.of(GrillBlockEntity::new, ModBlocks.GRILL_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
