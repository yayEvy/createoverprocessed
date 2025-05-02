package net.createoverprocessed.forge.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createoverprocessed.forge.content.blocks.GrillBlock;
import net.createoverprocessed.forge.content.blocks.GrillBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GrillBlockRenderer implements BlockEntityRenderer<GrillBlockEntity> {

    private static final float HEAD_ROTATION_DEGREES = 180.0f;

    public GrillBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GrillBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        boolean isHeated = blockState.getValue(GrillBlock.HEATED);
        Level level = blockEntity.getLevel();
        if (level == null || !isHeated) return;


        float animation = blockEntity.headAnimation.getValue(partialTicks) * 0.175f;


        float time = AnimationTickHolder.getRenderTime(level);


        int seed = blockEntity.getBlockPos().hashCode();
        float phase = (seed % 628) / 100f;


        float bobFrequency = 0.03f;
        float bobAmplitude = 0.01f;
        float offset = Mth.sin((time * bobFrequency) + phase) * bobAmplitude;


        float rodSpinSpeed = 0.4f;
        float rodSpin = (time * rodSpinSpeed + phase) % (2 * Mth.PI);


        float headY = offset - (animation * 0.75f);


        float headRotationRadians = (float) (HEAD_ROTATION_DEGREES * Math.PI / 180.0f);

        poseStack.pushPose();

        poseStack.translate(0.1, -0.1, 0.1);
        poseStack.scale(0.8f, 0.8f, 0.8f);


        VertexConsumer solid = bufferSource.getBuffer(RenderType.solid());
        SuperByteBuffer blazeBuffer = CachedBuffers.partial(GrillBlockPartialModels.GRILL_MODEL, blockState);

        blazeBuffer.translate(0, headY, 0);
        blazeBuffer.rotateCentered(headRotationRadians, Direction.UP);
        blazeBuffer.light(LightTexture.FULL_BRIGHT);
        blazeBuffer.renderInto(poseStack, solid);


        VertexConsumer cutout = bufferSource.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer rodsBuffer = CachedBuffers.partial(GrillBlockPartialModels.GRILL_HEAT, blockState);


        rodsBuffer.translate(0, animation + 0.125f, 0);
        rodsBuffer.rotateCentered(rodSpin, Direction.UP);
        rodsBuffer.light(LightTexture.FULL_BRIGHT);
        rodsBuffer.renderInto(poseStack, cutout);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(GrillBlockEntity blockEntity) {
        return true;
    }
}
