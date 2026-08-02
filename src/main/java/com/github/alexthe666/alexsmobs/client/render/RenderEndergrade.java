package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerEndergradeSaddle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class RenderEndergrade extends MobRenderer<EntityEndergrade, ModelEndergrade> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/endergrade.png");

    public RenderEndergrade(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelEndergrade(), 0.6F);
        this.addLayer(new LayerEndergradeSaddle(this));
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EntityEndergrade p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        Identifier Identifier = this.getTextureLocation(p_230496_1_);
        if (p_230496_3_) {
            return RenderType.itemEntityTranslucentCull(Identifier);
        } else if (p_230496_2_) {
            return RenderType.entityTranslucent(Identifier);
        } else {
            return p_230496_4_ ? RenderType.outline(Identifier) : null;
        }
    }

    protected void scale(EntityEndergrade entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public Identifier getTextureLocation(EntityEndergrade entity) {
        return TEXTURE;
    }
}

