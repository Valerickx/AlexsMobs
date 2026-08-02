package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorChain;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchorWinch;
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

public class RenderEndPirateAnchorWinch<T extends TileEntityEndPirateAnchorWinch> implements BlockEntityRenderer<T, RenderEndPirateAnchorWinch.AnchorWinchRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
    private static final Identifier TEXTURE_CHAIN = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_chain.png");
    private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
    private static final ModelEndPirateAnchorChain CHAIN_MODEL = new ModelEndPirateAnchorChain();

    public static class AnchorWinchRenderState extends BlockEntityRenderState {
        public boolean east;
        public boolean isAnchorEW;
        public float bottomOfChain;
        public float chainLengthForRender;
        public boolean hasAnchor;
        public float windCounter;
        public float windProgress;
        public boolean isWindingUp;
        public boolean isWinching;
        public float clientRoll;
        public float partialTick;
    }

    public RenderEndPirateAnchorWinch(Context rendererDispatcherIn) {
    }

    @Override
    public AnchorWinchRenderState createRenderState() {
        return new AnchorWinchRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, AnchorWinchRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.east = blockEntity.getBlockState().getValue(BlockEndPirateAnchorWinch.EASTORWEST);
        state.isAnchorEW = blockEntity.isAnchorEW();
        state.bottomOfChain = blockEntity.getChainLength(partialTick);
        state.chainLengthForRender = blockEntity.getChainLengthForRender();
        state.hasAnchor = blockEntity.hasAnchor();
        state.windCounter = blockEntity.windCounter;
        state.windProgress = blockEntity.getWindProgress(partialTick);
        state.isWindingUp = blockEntity.isWindingUp();
        state.isWinching = blockEntity.isWinching();
        state.clientRoll = blockEntity.clientRoll;
        state.partialTick = partialTick;
    }

    @Override
    public void submit(AnchorWinchRenderState state, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (state.east) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        boolean flag = false;
        matrixStackIn.pushPose();
        if (!state.isAnchorEW) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        float bottomOfChain = state.bottomOfChain;
        for (float i = 0; i < state.chainLengthForRender; i += 0.5F) {
            matrixStackIn.pushPose();
            float moveDown = Math.max(bottomOfChain - i, 0);
            matrixStackIn.translate(0, 0.1F + moveDown, 0);
            if (flag) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            if (moveDown <= 1F) {
                float modulatedScale = 0.5F + moveDown * 0.5F;
                matrixStackIn.translate(0, (1F - moveDown) * 0.5F, 0);
                matrixStackIn.scale(modulatedScale, modulatedScale, modulatedScale);
            }
            CHAIN_MODEL.resetToDefaultPose();
            submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(TEXTURE_CHAIN), (pose, buffer) -> {
                CHAIN_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
            });
            submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE_CHAIN), (pose, buffer) -> {
                CHAIN_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
            });
            matrixStackIn.popPose();
            flag = !flag;
        }
        matrixStackIn.popPose();

        WINCH_MODEL.renderAnchor(state.windCounter, state.windProgress, state.isWindingUp, state.isWinching, state.clientRoll, state.partialTick, state.east);
        submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            WINCH_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE), (pose, buffer) -> {
            WINCH_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        matrixStackIn.popPose();
        matrixStackIn.popPose();

        if (state.hasAnchor) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, -1.5F - bottomOfChain, 0.5F);
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            if (state.isAnchorEW) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            RenderEndPirateAnchor.ANCHOR_MODEL.resetToDefaultPose();
            submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(RenderEndPirateAnchor.TEXTURE_ANCHOR), (pose, buffer) -> {
                RenderEndPirateAnchor.ANCHOR_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
            });
            submitNodeCollector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(RenderEndPirateAnchor.TEXTURE_ANCHOR_GLOW), (pose, buffer) -> {
                RenderEndPirateAnchor.ANCHOR_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
            });

            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }
}




