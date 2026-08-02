package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSoulVultureGlow;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderSoulVulture extends MobRenderer<EntitySoulVulture, LivingEntityRenderState, ModelSoulVulture> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/soul_vulture/soul_vulture.png");

    public RenderSoulVulture(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSoulVulture(), 0.3F);
        this.addLayer(new LayerSoulVultureGlow(this));
    }

    protected void scale(EntitySoulVulture entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public Identifier getTextureLocation(EntitySoulVulture entity) {
        return TEXTURE;
    }
}


