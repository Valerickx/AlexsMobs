package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBeak;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

public class RenderVoidWormBeak<T extends TileEntityVoidWormBeak> implements BlockEntityRenderer<T, RenderVoidWormBeak.VoidWormBeakRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_beak.png");
    private static final ModelVoidWormBeak HEAD_MODEL = new ModelVoidWormBeak();

    public static class VoidWormBeakRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public float chompProgress;
        public float ageInTicks;
    }

    public RenderVoidWormBeak(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public VoidWormBeakRenderState createRenderState() {
        return new VoidWormBeakRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, VoidWormBeakRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.facing = blockEntity.getBlockState().getValue(BlockVoidWormBeak.FACING);
        state.chompProgress = blockEntity.getChompProgress(partialTick);
        state.ageInTicks = blockEntity.ticksExisted + partialTick;
    }

    @Override
    public void submit(VoidWormBeakRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        Direction dir = state.facing;
        switch (dir) {
            case NORTH -> poseStack.translate(0.5, 0.5F, 0.5F);
            case EAST -> poseStack.translate(0.5F, 0.5F, 0.5F);
            case SOUTH -> poseStack.translate(0.5, 0.5F, 0.5F);
            case WEST -> poseStack.translate(0.5F, 0.5F, 0.5F);
            case UP -> poseStack.translate(0.5F, 0.5F, 0.5F);
            case DOWN -> poseStack.translate(0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.pushPose();
        poseStack.translate(0, -0.01F, 0.0F);
        HEAD_MODEL.renderBeak(state.chompProgress, state.ageInTicks);

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            HEAD_MODEL.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        poseStack.popPose();
        poseStack.popPose();
    }
}
