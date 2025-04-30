package net.createoverprocessed.forge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.createoverprocessed.CreateOverprocessed.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> OVERPROCESSED_TAB =
            TABS.register("overprocessed", () -> CreativeModeTab.builder()
                    .title(Component.literal("Create: Overprocessed"))
                    .icon(() -> new ItemStack(ModBlocks.GRILL_BLOCK.get()))
                    .displayItems((ItemDisplayParameters parameters, Output output) -> {
                        output.accept(ModBlocks.GRILL_BLOCK.get());
                        // adding more stuff here later, blocks, items etc idk
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
