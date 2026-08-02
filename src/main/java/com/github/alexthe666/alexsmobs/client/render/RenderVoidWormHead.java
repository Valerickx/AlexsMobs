package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerVoidWormGlow;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class RenderVoidWormHead extends MobRenderer<EntityVoidWorm, ModelVoidWorm> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_head.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/void_worm/void_worm_head_glow.png");

    public RenderVoidWormHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelVoidWorm(0.0f), 1F);
        this.addLayer(new LayerVoidWormGlow(this, renderManagerIn.getResourceManager(), new ModelVoidWorm(1.001F)){
            public Identifier getGlowTexture(LivingEntity worm){
                return TEXTURE_GLOW;
            }
            public boolean isGlowing(LivingEntity worm){
                return true;
            }
            public float getAlpha(LivingEntity livingEntity){
                return 1.0F;
            }
        });
    }

    @Nullable
    protected RenderType getRenderType(EntityVoidWorm jelly, boolean normal, boolean invis, boolean outline) {
        Identifier Identifier = this.getTextureLocation(jelly);
        if (invis) {
            return RenderType.itemEntityTranslucentCull(Identifier);
        } else if (normal) {
            return RenderType.entityTranslucent(Identifier);
        } else {
            return outline ? RenderType.outline(Identifier) : null;
        }
    }

    public boolean shouldRender(EntityVoidWorm worm, Frustum camera, double camX, double camY, double camZ) {
        return worm.getPortalTicks() <= 0 && super.shouldRender(worm, camera, camX, camY, camZ);
    }

    public Identifier getTextureLocation(EntityVoidWorm entity) {
        return TEXTURE;
    }
}

