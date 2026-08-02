package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelOrca;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderOrca extends MobRenderer<EntityOrca, LivingEntityRenderState, ModelOrca> {
    private static final Identifier TEXTURE_NE = Identifier.parse("alexsmobs:textures/entity/orca_ne.png");
    private static final Identifier TEXTURE_NW = Identifier.parse("alexsmobs:textures/entity/orca_nw.png");
    private static final Identifier TEXTURE_SE = Identifier.parse("alexsmobs:textures/entity/orca_se.png");
    private static final Identifier TEXTURE_SW = Identifier.parse("alexsmobs:textures/entity/orca_sw.png");

    public RenderOrca(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelOrca(), 1.0F);
    }

    protected void scale(EntityOrca entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }


    public Identifier getTextureLocation(EntityOrca entity) {
        return switch (entity.getVariant()) {
            case 0 -> TEXTURE_NE;
            case 1 -> TEXTURE_NW;
            case 2 -> TEXTURE_SE;
            default -> TEXTURE_SW;
        };
    }
}





