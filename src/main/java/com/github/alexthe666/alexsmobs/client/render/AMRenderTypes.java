package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class AMRenderTypes {

    public static final Identifier STATIC_TEXTURE = Identifier.parse("alexsmobs:textures/static.png");

    public static RenderType getTransparentMimicube(Identifier texture) {
        return RenderType.entityTranslucent(texture);
    }

    public static RenderType getEyesFlickering(Identifier location, float lightLevel) {
        return RenderType.entityEyes(location);
    }

    public static RenderType getFullBright(Identifier location) {
        return RenderType.entityCutoutNoCull(location);
    }

    public static RenderType getFreddy(Identifier location) {
        return RenderType.entityCutoutNoCull(location);
    }

    public static RenderType getFrilledSharkTeeth(Identifier location) {
        return RenderType.entityCutoutNoCull(location);
    }

    public static RenderType getEyesNoCull(Identifier location) {
        return RenderType.entityEyes(location);
    }

    public static RenderType getSpectreBones(Identifier location) {
        return RenderType.entityTranslucent(location);
    }

    public static RenderType getGhost(Identifier location) {
        return RenderType.entityTranslucent(location);
    }

    public static RenderType getEyesAlphaEnabled(Identifier location) {
        return RenderType.entityEyes(location);
    }

    public static RenderType getEyesNoFog(Identifier location) {
        return RenderType.entityEyes(location);
    }

    public static RenderType getSunbirdShine() {
        return RenderType.entityTranslucent(Identifier.parse("alexsmobs:textures/entity/sunbird_shine.png"));
    }

    public static RenderType getSkulkBoom() {
        return RenderType.entityTranslucent(Identifier.parse("alexsmobs:textures/particle/skulk_boom.png"));
    }

    public static RenderType getVoidWormPortal(Identifier location) {
        return RenderType.entityTranslucent(location);
    }

    public static RenderType getRainbow(Identifier location) {
        return RenderType.entityTranslucent(location);
    }

    public static RenderType getCombJelly(Identifier location) {
        return RenderType.entityTranslucent(location);
    }

    public static RenderType getStatic(Identifier location) {
        return RenderType.entityTranslucent(location);
    }
}
