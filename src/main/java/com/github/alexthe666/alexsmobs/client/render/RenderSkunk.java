package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelSkunk;
import com.github.alexthe666.alexsmobs.entity.EntitySkunk;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderSkunk extends MobRenderer<EntitySkunk, LivingEntityRenderState, ModelSkunk> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/skunk.png");

    public RenderSkunk(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSkunk(), 0.45F);
    }

    public Identifier getTextureLocation(EntitySkunk entity) {
        return TEXTURE;
    }
}
