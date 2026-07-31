package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Small compatibility helpers for API that moved between Minecraft versions.
 */
public class AMPortUtil {

    private AMPortUtil() {
    }

    /**
     * Step height is an attribute now, so there is no direct setter on the entity anymore.
     */
    public static void setStepHeight(LivingEntity entity, double height) {
        AttributeInstance instance = entity.getAttribute(Attributes.STEP_HEIGHT);
        if (instance != null) {
            instance.setBaseValue(height);
        }
    }
}
