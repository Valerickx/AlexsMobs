package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderCaiman extends MobRenderer<EntityCaiman, LivingEntityRenderState, ModelCaiman> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/caiman.png");

    public RenderCaiman(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCaiman(), 0.4F);
    }

    public Identifier getTextureLocation(EntityCaiman entity) {
        return TEXTURE;
    }
}
