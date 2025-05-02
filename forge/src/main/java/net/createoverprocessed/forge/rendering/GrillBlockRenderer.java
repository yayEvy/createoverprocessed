package net.createoverprocessed.forge.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createoverprocessed.forge.content.blocks.GrillBlock;
import net.createoverprocessed.forge.content.blocks.GrillBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GrillBlockRenderer implements BlockEntityRenderer<GrillBlockEntity> {
    public GrillBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GrillBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        boolean isHeated = blockState.getValue(GrillBlock.HEATED);
        Level level = blockEntity.getLevel();
        if (level == null || !isHeated) return;

        float horizontalAngle = getHorizontalAngleTowardsPlayer(blockEntity, partialTicks);
        float animation = blockEntity.headAnimation.getValue(partialTicks) * .175f;
        float time = AnimationTickHolder.getRenderTime(level);
        float renderTick = time + (blockEntity.hashCode() % 13) * 16f;
        float offset = Mth.sin((float) ((renderTick / 16f) % (2 * Math.PI))) / 32f;
        float offset1 = Mth.sin((float) ((renderTick / 16f + Math.PI) % (2 * Math.PI))) / 32f;
        float headY = offset - (animation * .75f);

        poseStack.pushPose();

        // these two lines drove me insane :3
        poseStack.translate(0.1, -0.1, 0.1);


        poseStack.scale(0.8f, 0.8f, 0.8f);
        // oki :3

        VertexConsumer solid = bufferSource.getBuffer(RenderType.solid());
        SuperByteBuffer blazeBuffer = CachedBuffers.partial(GrillBlockPartialModels.GRILL_MODEL, blockState);


        blazeBuffer.translate(0, headY, 0);


        blazeBuffer.rotateCentered(horizontalAngle, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(poseStack, solid);


        VertexConsumer cutout = bufferSource.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer rodsBuffer = CachedBuffers.partial(GrillBlockPartialModels.GRILL_HEAT, blockState);


        rodsBuffer.translate(0, offset1 + animation + .125f, 0)
                .rotateCentered(horizontalAngle, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(poseStack, cutout);

        poseStack.popPose();
    }

    private float getHorizontalAngleTowardsPlayer(GrillBlockEntity blockEntity, float partialTicks) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;


        Vec3 blockPos = Vec3.atCenterOf(blockEntity.getBlockPos());


        Vec3 playerPos = player.getEyePosition(partialTicks);


        double dx = playerPos.x - blockPos.x;
        double dz = playerPos.z - blockPos.z;


        float angle = (float) Math.atan2(dz, dx);

        return angle + (float) (Math.PI / 2);
    }

    @Override
    public boolean shouldRenderOffScreen(GrillBlockEntity blockEntity) {
        return true;
    }
}
