package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateShipWheel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

public class RenderEndPirateShipWheel<T extends TileEntityEndPirateShipWheel> implements BlockEntityRenderer<T, RenderEndPirateShipWheel.ShipWheelRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/ship_wheel.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/end_pirate/ship_wheel_glow.png");
    private static final ModelEndPirateShipWheel WHEEL_MODEL = new ModelEndPirateShipWheel();

    public static class ShipWheelRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public float wheelRot;
    }

    public RenderEndPirateShipWheel(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public ShipWheelRenderState createRenderState() {
        return new ShipWheelRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, ShipWheelRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.facing = blockEntity.getBlockState().getValue(BlockEndPirateShipWheel.FACING);
        state.wheelRot = blockEntity.getWheelRot(partialTick);
    }

    @Override
    public void submit(ShipWheelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        Direction dir = state.facing;
        switch (dir) {
            case NORTH -> poseStack.translate(0.5F, 0.5F, 0.9F);
            case SOUTH -> poseStack.translate(0.5F, 0.5F, 0.1F);
            case EAST -> poseStack.translate(0.1F, 0.5F, 0.5F);
            case WEST -> poseStack.translate(0.9F, 0.5F, 0.5F);
            case UP -> poseStack.translate(0.5F, 0.1F, 0.5F);
            case DOWN -> poseStack.translate(0.5F, 0.9F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        WHEEL_MODEL.renderWheel(state.wheelRot);

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            WHEEL_MODEL.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.eyes(TEXTURE_GLOW), (pose, buffer) -> {
            WHEEL_MODEL.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        poseStack.popPose();
        poseStack.popPose();
    }
}



