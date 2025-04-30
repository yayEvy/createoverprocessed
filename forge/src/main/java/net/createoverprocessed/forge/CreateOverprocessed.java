package net.createoverprocessed.forge;

import net.createoverprocessed.forge.registry.ModBlockEntities;
import net.createoverprocessed.forge.registry.ModBlocks;
import net.createoverprocessed.forge.registry.ModCreativeTabs;
import net.createoverprocessed.forge.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateOverprocessed.MOD_ID)
public class CreateOverprocessed {
    public static final String MOD_ID = "createoverprocessed";

    public CreateOverprocessed() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);


    }
}
