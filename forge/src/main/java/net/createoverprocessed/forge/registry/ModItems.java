package net.createoverprocessed.forge.registry;

import net.createoverprocessed.forge.registry.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.createoverprocessed.CreateOverprocessed.MOD_ID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> GRILL_BLOCK_ITEM =
            ITEMS.register("grill", () ->
                    new BlockItem(ModBlocks.GRILL_BLOCK.get(), new Item.Properties())
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
