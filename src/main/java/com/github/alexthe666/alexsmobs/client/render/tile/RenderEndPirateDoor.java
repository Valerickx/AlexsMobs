package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateDoor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateDoor<T extends TileEntityEndPirateDoor> implements BlockEntityRenderer<T, RenderEndPirateDoor.DoorRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/door.png");
    private static final ModelEndPirateDoor DOOR_MODEL = new ModelEndPirateDoor();

    public static class DoorRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public boolean isLeftHinge;
        public float openProgress;
        public float wiggleProgress;
        public float ticks;
    }

    public RenderEndPirateDoor(Context rendererDispatcherIn) {
    }

    @Override
    public DoorRenderState createRenderState() {
        return new DoorRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, DoorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.facing = blockEntity.getBlockState().getValue(BlockEndPirateDoor.HORIZONTAL_FACING);
        state.isLeftHinge = blockEntity.getBlockState().getValue(BlockEndPirateDoor.HINGE) == DoorHingeSide.LEFT;
        state.openProgress = blockEntity.getOpenProgress(partialTick);
        state.wiggleProgress = blockEntity.getWiggleProgress(partialTick);
        state.ticks = blockEntity.ticksExisted + partialTick;
    }

    @Override
    public void submit(DoorRenderState state, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        matrixStackIn.pushPose();
        Direction dir = state.facing;
        switch (dir) {
            case NORTH -> matrixStackIn.translate(0.5, 0.5F, -0.5F);
            case EAST -> matrixStackIn.translate(1.5F, 0.5F, 0.5F);
            case SOUTH -> matrixStackIn.translate(0.5, 0.5F, 1.5F);
            case WEST -> matrixStackIn.translate(-0.5F, 0.5F, 0.5F);
        }
        matrixStackIn.mulPose(dir.getOpposite().getRotation());
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 1, -1);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
        matrixStackIn.scale(0.999F, 0.999F, 0.999F);

        DOOR_MODEL.renderDoor(state.openProgress, state.wiggleProgress, state.ticks, state.isLeftHinge);

        submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE), (pose, buffer) -> {
            DOOR_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    public int getViewDistance() {
        return 128;
    }
}

