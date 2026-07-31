package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class RenderEndPirateAnchor<T extends TileEntityEndPirateAnchor> implements BlockEntityRenderer<T, BlockEntityRenderState> {

    protected static final Identifier TEXTURE_ANCHOR = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor.png");
    protected static final Identifier TEXTURE_ANCHOR_GLOW = Identifier.parse("alexsmobs:textures/entity/end_pirate/anchor_glow.png");
    protected static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();

    public RenderEndPirateAnchor(Context rendererDispatcherIn) {
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, OrderedSubmitNodeCollector bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        boolean east = tileEntityIn.getBlockState().getValue(BlockEndPirateAnchor.EASTORWEST);
        //boolean isChain = tileEntityIn.getBlockState().getValue(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
        matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        if(east){
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
        ANCHOR_MODEL.renderAnchor(tileEntityIn, partialTicks, east);
        ANCHOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(TEXTURE_ANCHOR)), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);
        ANCHOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.eyes(TEXTURE_ANCHOR_GLOW)), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);

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
