package com.github.alexthe666.alexsmobs.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.ArmorType;

public class AMArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durability;
    private final int[] damageReduction;
    private final int encantability;
    private final SoundEvent sound;
    private final float toughness;
    private Ingredient ingredient = null;
    public float knockbackResistance = 0.0F;

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.encantability = encantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = 0;
    }

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResist) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.encantability = encantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResist;
    }

    public int getDurabilityForType(ArmorType type) {
        return MAX_DAMAGE_ARRAY[type.ordinal()] * this.durability;
    }

    public int getDefenseForType(ArmorType type) {
        return this.damageReduction[type.ordinal()];
    }

    public int getEnchantmentValue() {
        return this.encantability;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.ingredient == null ? Ingredient.of() : this.ingredient;
    }

    public void setRepairMaterial(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getName() {
        return name;
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
