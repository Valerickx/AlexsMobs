package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class AMRenderTypes {

    public static final Identifier STATIC_TEXTURE = Identifier.parse("alexsmobs:textures/static.png");
    public static final RenderType STATIC_PORTAL = RenderTypes.entityTranslucent(STATIC_TEXTURE);

    public static RenderType getTransparentMimicube(Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    public static RenderType getEyesFlickering(Identifier location, float lightLevel) {
        return RenderTypes.eyes(location);
    }

    public static RenderType getFullBright(Identifier location) {
        return RenderTypes.entityCutout(location);
    }

    public static RenderType getFreddy(Identifier location) {
        return RenderTypes.entityCutout(location);
    }

    public static RenderType getFrilledSharkTeeth(Identifier location) {
        return RenderTypes.entityCutout(location);
    }

    public static RenderType getEyesNoCull(Identifier location) {
        return RenderTypes.eyes(location);
    }

    public static RenderType getSpectreBones(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    public static RenderType getGhost(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    public static RenderType getEyesAlphaEnabled(Identifier location) {
        return RenderTypes.eyes(location);
    }

    public static RenderType getEyesNoFog(Identifier location) {
        return RenderTypes.eyes(location);
    }

    public static RenderType getSunbirdShine() {
        return RenderTypes.entityTranslucent(Identifier.parse("alexsmobs:textures/entity/sunbird_shine.png"));
    }

    public static RenderType getSkulkBoom() {
        return RenderTypes.entityTranslucent(Identifier.parse("alexsmobs:textures/particle/skulk_boom.png"));
    }

    public static RenderType getVoidWormPortal(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    public static RenderType getRainbow(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    public static RenderType getCombJelly(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    public static RenderType getStatic(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    public static RenderType entityCutoutNoCull(Identifier location) {
        return RenderTypes.entityCutout(location);
    }

    public static RenderType entityCutout(Identifier location) {
        return RenderTypes.entityCutout(location);
    }

    public static RenderType entityTranslucent(Identifier location) {
        return RenderTypes.entityTranslucent(location);
    }

    // Compatibility helper for ItemRenderer.getArmorFoilBuffer which moved/changed in mappings.
    public static VertexConsumer getArmorFoilBuffer(net.minecraft.client.renderer.MultiBufferSource bufferIn, RenderType type, boolean p1, boolean p2) {
        // The original method handled enchanted glint and layering. For now, return the buffer directly.
        return bufferIn.getBuffer(type);
    }
}



