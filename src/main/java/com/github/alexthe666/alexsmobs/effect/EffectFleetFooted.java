package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class EffectFleetFooted extends MobEffect {

    private static final net.minecraft.resources.Identifier SPRINT_JUMP_SPEED_MODIFIER_ID = net.minecraft.resources.Identifier.parse("alexsmobs:fleet_footed");
    private static final AttributeModifier SPRINT_JUMP_SPEED_BONUS = new AttributeModifier(SPRINT_JUMP_SPEED_MODIFIER_ID, 0.2F, AttributeModifier.Operation.ADD_VALUE);
    private int lastDuration = -1;
    private int removeEffectAfter = 0;

    public EffectFleetFooted() {
        super(MobEffectCategory.BENEFICIAL, 0X685441);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        AttributeInstance modifiableattributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        boolean applyEffect = entity.isSprinting() && !entity.onGround() && lastDuration > 2;
        if(removeEffectAfter > 0){
            removeEffectAfter--;
        }
        if (applyEffect) {
            if(!modifiableattributeinstance.hasModifier(SPRINT_JUMP_SPEED_MODIFIER_ID)){
                modifiableattributeinstance.addPermanentModifier(SPRINT_JUMP_SPEED_BONUS);
            }
            removeEffectAfter = 5;
        }
        if (removeEffectAfter <= 0 || lastDuration < 2) {
            modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_MODIFIER_ID);
        }
    }

    public void removeAttributeModifiers(AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.fleet_footed";
    }

}
