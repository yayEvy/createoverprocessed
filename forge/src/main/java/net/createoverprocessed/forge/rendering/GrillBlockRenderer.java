package net.createoverprocessed.forge.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.mojang.blaze3d.vertex.BufferUploader.draw;

public class GrillBlockRenderer {

    public GrillBlockRenderer(BlockEntityRendererProvider.Context context) {}



    public static void renderBlazeGrill(PoseStack ms, @Nullable PoseStack modelTransform, MultiBufferSource bufferSource,
                                        Level level, BlockState blockState, float animation, float horizontalAngle,
                                        boolean isHeated) {
        boolean blockAbove = animation > 0.125f;
        float time = AnimationTickHolder.getRenderTime(level);



        ms.pushPose();

    }

}
