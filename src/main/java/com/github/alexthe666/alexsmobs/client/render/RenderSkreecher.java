package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelSkreecher;
import com.github.alexthe666.alexsmobs.entity.EntitySkreecher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;

public class RenderSkreecher extends MobRenderer<EntitySkreecher, ModelSkreecher> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/skreecher.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/skreecher_glow.png");

    public RenderSkreecher(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSkreecher(), 0.35F);
        this.addLayer(new LayerScorch(this));
    }

    protected void scale(EntitySkreecher entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public Identifier getTextureLocation(EntitySkreecher entity) {
        return TEXTURE;
    }

    static class LayerScorch extends RenderLayer<EntitySkreecher, ModelSkreecher> {

        public LayerScorch(RenderSkreecher render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, OrderedSubmitNodeCollector bufferIn, int packedLightIn, EntitySkreecher entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer scorch = bufferIn.getBuffer(AMRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            float alpha = (float)Math.sin((entitylivingbaseIn.tickCount + partialTicks) * 0.1F) * 0.35F + 0.5F;
            this.getParentModel().renderToBuffer(matrixStackIn, scorch, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}

