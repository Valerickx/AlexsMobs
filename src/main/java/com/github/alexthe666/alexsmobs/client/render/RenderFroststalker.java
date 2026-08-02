package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelFroststalker;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderFroststalker extends MobRenderer<EntityFroststalker, LivingEntityRenderState, ModelFroststalker> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/froststalker.png");
    private static final Identifier TEXTURE_NOSPIKES = Identifier.parse("alexsmobs:textures/entity/froststalker_nospikes.png");

    public RenderFroststalker(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFroststalker(), 0.4F);
    }

    protected void scale(EntityFroststalker entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public Identifier getTextureLocation(EntityFroststalker entity) {
        return entity.hasSpikes() ? TEXTURE : TEXTURE_NOSPIKES;
    }

    protected boolean isShaking(EntityFroststalker entity) {
        return entity.isInWaterOrRain() && !entity.hasSpikes();
    }

}


