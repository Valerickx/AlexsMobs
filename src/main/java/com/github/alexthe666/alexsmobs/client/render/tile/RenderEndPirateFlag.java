package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateFlag;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateFlag;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RenderEndPirateFlag<T extends TileEntityEndPirateFlag> implements BlockEntityRenderer<T, RenderEndPirateFlag.FlagRenderState> {

    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/end_pirate/flag.png");
    private static final ModelEndPirateFlag FLAG_MODEL = new ModelEndPirateFlag();

    public static class FlagRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public float ticks;
    }

    public RenderEndPirateFlag(Context rendererDispatcherIn) {
    }

    @Override
    public FlagRenderState createRenderState() {
        return new FlagRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, FlagRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.facing = blockEntity.getBlockState().getValue(BlockEndPirateFlag.FACING);
        state.ticks = blockEntity.ticksExisted + partialTick;
    }

    @Override
    public void submit(FlagRenderState state, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        matrixStackIn.pushPose();
        Direction dir = state.facing;
        switch (dir) {
            case NORTH -> matrixStackIn.translate(0.5, 1.5F, 0.5F);
            case EAST -> matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            case SOUTH -> matrixStackIn.translate(0.5, 1.5F, 0.5F);
            case WEST -> matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        }
        matrixStackIn.mulPose(dir.getOpposite().getRotation());
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(dir.getAxis() == Direction.Axis.Y ? -90.0F : 90.0F));
        matrixStackIn.pushPose();
        FLAG_MODEL.renderFlag(state.ticks);

        submitNodeCollector.submitCustomGeometry(matrixStackIn, net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            FLAG_MODEL.renderToBuffer(matrixStackIn, buffer, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, net.minecraft.util.ARGB.color(255, 255, 255, 255));
        });
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}




