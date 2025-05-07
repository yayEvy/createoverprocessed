package net.createoverprocessed.forge.registry;

import net.createoverprocessed.forge.content.blocks.GrillBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static net.createoverprocessed.CreateOverprocessed.MOD_ID;
import static net.minecraft.world.item.Items.registerBlock;

public class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static final RegistryObject<Block> GRILL_BLOCK =
            BLOCKS.register("grill", () -> new GrillBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion()
            ));

    public static final RegistryObject<Block> PINK_SALT_BLOCK = registerBlock("pink_salt_block",
                    () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));



    private static <T extends  Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
RegistryObject<T> toReturn = BLOCKS.register(name, block);
registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return  ModItems.ITEMS.register(name, () ->new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
