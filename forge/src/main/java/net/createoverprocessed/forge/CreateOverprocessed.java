package net.createoverprocessed.forge;

import net.createoverprocessed.forge.registry.ModBlockEntities;
import net.createoverprocessed.forge.registry.ModBlocks;
import net.createoverprocessed.forge.registry.ModCreativeTabs;
import net.createoverprocessed.forge.registry.ModItems;
import net.createoverprocessed.forge.rendering.GrillBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateOverprocessed.MOD_ID)
public class CreateOverprocessed {
    public static final String MOD_ID = "createoverprocessed";

    public CreateOverprocessed() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        IEventBus clientBus = FMLJavaModLoadingContext.get().getModEventBus();
        clientBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.GRILL.get(), GrillBlockRenderer::new);
    }
}
