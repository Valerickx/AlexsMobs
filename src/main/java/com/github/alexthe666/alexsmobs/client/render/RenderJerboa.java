package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderJerboa extends MobRenderer<EntityJerboa, LivingEntityRenderState, ModelJerboa> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/jerboa.png");
    private static final Identifier TEXTURE_SLEEPING = Identifier.parse("alexsmobs:textures/entity/jerboa_sleeping.png");

    public RenderJerboa(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelJerboa(), 0.1F);
    }

    protected void scale(EntityJerboa entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public Identifier getTextureLocation(EntityJerboa entity) {
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}





