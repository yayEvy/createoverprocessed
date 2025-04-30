package net.createoverprocessed.forge.registry;

import net.createoverprocessed.forge.content.blocks.GrillBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.createoverprocessed.CreateOverprocessed.MOD_ID;

public class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static final RegistryObject<Block> GRILL_BLOCK =
            BLOCKS.register("grill", () -> new GrillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
