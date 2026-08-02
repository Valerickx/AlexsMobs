package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockTransmutationTable;
import com.github.alexthe666.alexsmobs.client.model.ModelTransmutationTable;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.minecraft.client.renderer.rendertype.RenderTypes;

public class RenderTransmutationTable<T extends TileEntityTransmutationTable> implements BlockEntityRenderer<T, RenderTransmutationTable.TransmutationTableRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table.png");
    private static final Identifier OVERLAY = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
    private static final Identifier GLOW_TEXTURE = Identifier.parse("alexsmobs:textures/entity/farseer/transmutation_table_glow.png");
    private static final ModelTransmutationTable MODEL = new ModelTransmutationTable(0F);
    private static final ModelTransmutationTable OVERLAY_MODEL = new ModelTransmutationTable(0.01F);

    public static class TransmutationTableRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public float ageInTicks;
    }

    public RenderTransmutationTable(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public TransmutationTableRenderState createRenderState() {
        return new TransmutationTableRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, TransmutationTableRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.facing = blockEntity.getBlockState().getValue(BlockTransmutationTable.FACING);
        state.ageInTicks = blockEntity.ticksExisted + partialTick;
    }

    @Override
    public void submit(TransmutationTableRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        Direction dir = state.facing;
        switch (dir) {
            case NORTH -> poseStack.translate(0.5, 1.5F, 0.5F);
            case EAST -> poseStack.translate(0.5F, 1.5F, 0.5F);
            case SOUTH -> poseStack.translate(0.5, 1.5F, 0.5F);
            case WEST -> poseStack.translate(0.5F, 1.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.pushPose();

        MODEL.animate(state.ageInTicks);
        OVERLAY_MODEL.animate(state.ageInTicks);

        float glowAlpha = 0.5F + (float) Math.sin(state.ageInTicks * 0.05F) * 0.25F;

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TEXTURE), (pose, buffer) -> {
            MODEL.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        submitNodeCollector.submitCustomGeometry(poseStack, AMRenderTypes.getEyesAlphaEnabled(GLOW_TEXTURE), (pose, buffer) -> {
            MODEL.renderToBuffer(poseStack, buffer, 240, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color((int) (glowAlpha * 255F), 255, 255, 255));
        });
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(OVERLAY), (pose, buffer) -> {
            OVERLAY_MODEL.renderToBuffer(poseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        poseStack.popPose();
        poseStack.popPose();
    }
}
