package net.createoverprocessed;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOverprocessed {
    public static final String MOD_ID = "createoverprocessed";
    public static final String NAME = "createoverprocessed";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);


    public static void init() {
        LOGGER.info("{} initializing! Create version: {} on platform: {}", NAME, Create.ID, ExampleExpectPlatform.platformName());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
