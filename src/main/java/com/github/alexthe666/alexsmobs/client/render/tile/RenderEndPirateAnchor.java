package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateAnchor<T extends TileEntityEndPirateAnchor> implements BlockEntityRenderer<T, RenderEndPirateAnchor.AnchorRenderState> {

    protected static final Identifier TEXTURE_ANCHOR = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor.png");
    protected static final Identifier TEXTURE_ANCHOR_GLOW = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_glow.png");
    protected static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();

    public static class AnchorRenderState extends BlockEntityRenderState {
        public boolean east;
    }

    public RenderEndPirateAnchor(Context rendererDispatcherIn) {
    }

    @Override
    public AnchorRenderState createRenderState() {
        return new AnchorRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, AnchorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.east = blockEntity.getBlockState().getValue(BlockEndPirateAnchor.EASTORWEST);
    }

    @Override
    public void submit(AnchorRenderState state, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (state.east) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        ANCHOR_MODEL.renderAnchor(state.east);

        submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(TEXTURE_ANCHOR), (pose, buffer) -> {
            ANCHOR_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE_ANCHOR_GLOW), (pose, buffer) -> {
            ANCHOR_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });

        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    public boolean shouldRenderOffScreen(T p_112306_) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }
}

