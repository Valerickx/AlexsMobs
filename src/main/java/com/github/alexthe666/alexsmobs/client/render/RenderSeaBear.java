package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelSeaBear;
import com.github.alexthe666.alexsmobs.entity.EntitySeaBear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderSeaBear extends MobRenderer<EntitySeaBear, LivingEntityRenderState, ModelSeaBear> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/sea_bear.png");

    public RenderSeaBear(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSeaBear(), 1.2F);
    }

    protected void scale(EntitySeaBear entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public Identifier getTextureLocation(EntitySeaBear entity) {
        return TEXTURE;
    }
}
