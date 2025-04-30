package net.createoverprocessed.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.createoverprocessed.ExampleBlocks;
import net.createoverprocessed.CreateOverprocessed;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateOverprocessed.init();
        CreateOverprocessed.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
                ), CreateOverprocessed.NAME);
        // on fabric, Registrates must be explicitly finalized and registered.
        ExampleBlocks.REGISTRATE.register();
    }
}
