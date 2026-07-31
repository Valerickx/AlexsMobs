package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModArmor extends Item {
    private final AMArmorMaterial material;
    private final ArmorType type;
    private final float knockbackResistance;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapCroc;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapMoose;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapFlyingFish;
    private Multimap<Holder<Attribute>, AttributeModifier> attributeMapKimono;

    public ItemModArmor(AMArmorMaterial armorMaterial, ArmorType slot) {
        super(new Item.Properties().durability(armorMaterial.getDurabilityForType(slot)));
        this.material = armorMaterial;
        this.type = slot;
        this.knockbackResistance = armorMaterial.knockbackResistance;
    }

    public AMArmorMaterial getMaterial() {
        return this.material;
    }

    public ArmorType getType() {
        return this.type;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getArmorRenderProperties());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.material == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.centipede_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.material == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.emu_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (this.material == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.roadrunner_boots.desc").withStyle(ChatFormatting.BLUE));
        }
        if (this.material == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.frontier_cap.desc").withStyle(ChatFormatting.BLUE));
        }
        if (this.material == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.froststalker_helmet.desc").withStyle(ChatFormatting.AQUA));
        }
        if (this.material == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.rocky_chestplate.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.material == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL && AlexsMobs.isAprilFools()) {
            tooltip.add(Component.translatable("item.alexsmobs.sombrero.special_desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.material == AMItemRegistry.FLYING_FISH_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.flying_fish_boots.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.material == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.novelty_hat.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.material == AMItemRegistry.KIMONO_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.unsettling_kimono.desc").withStyle(ChatFormatting.GRAY));
        }
    }

    private void buildCrocAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_modifier"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "swim_speed"), 1, AttributeModifier.Operation.ADD_VALUE));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_knockback_resistance"), this.knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        attributeMapCroc = builder.build();
    }

    private void buildFlyingFishAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_modifier"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "swim_speed"), 0.5, AttributeModifier.Operation.ADD_VALUE));
        attributeMapFlyingFish = builder.build();
    }

    private void buildMooseAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_modifier"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "knockback"), 2, AttributeModifier.Operation.ADD_VALUE));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_knockback_resistance"), this.knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        attributeMapMoose = builder.build();
    }

    private void buildKimonoAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_modifier"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "armor_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "block_reach_distance"), 2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Identifier.fromNamespaceAndPath("alexsmobs", "entity_reach_distance"), 2, AttributeModifier.Operation.ADD_VALUE));
        attributeMapKimono = builder.build();
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getCustomAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (getMaterial() == AMItemRegistry.CROCODILE_ARMOR_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapCroc == null) {
                buildCrocAttributes(AMItemRegistry.CROCODILE_ARMOR_MATERIAL);
            }
            return attributeMapCroc;
        }
        if (getMaterial() == AMItemRegistry.MOOSE_ARMOR_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapMoose == null) {
                buildMooseAttributes(AMItemRegistry.MOOSE_ARMOR_MATERIAL);
            }
            return attributeMapMoose;
        }
        if (getMaterial() == AMItemRegistry.FLYING_FISH_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapFlyingFish == null) {
                buildFlyingFishAttributes(AMItemRegistry.FLYING_FISH_MATERIAL);
            }
            return attributeMapFlyingFish;
        }
        if (getMaterial() == AMItemRegistry.KIMONO_MATERIAL && equipmentSlot == this.type.getSlot()) {
            if (attributeMapKimono == null) {
                buildKimonoAttributes(AMItemRegistry.KIMONO_MATERIAL);
            }
            return attributeMapKimono;
        }
        return ImmutableMultimap.of();
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.material == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/crocodile_chestplate.png";
        } else if (this.material == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/roadrunner_boots.png";
        } else if (this.material == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/centipede_leggings.png";
        } else if (this.material == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/moose_headgear.png";
        } else if (this.material == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/frontier_cap.png";
        } else if (this.material == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/sombrero.png";
        } else if (this.material == AMItemRegistry.SPIKED_TURTLE_SHELL_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/spiked_turtle_shell.png";
        } else if (this.material == AMItemRegistry.FEDORA_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/fedora.png";
        } else if (this.material == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/emu_leggings.png";
        } else if (this.material == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/froststalker_helmet.png";
        } else if (this.material == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/rocky_chestplate.png";
        } else if (this.material == AMItemRegistry.FLYING_FISH_MATERIAL) {
            return "alexsmobs:textures/armor/flying_fish_boots.png";
        } else if (this.material == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
            return "alexsmobs:textures/armor/novelty_hat.png";
        } else if (this.material == AMItemRegistry.KIMONO_MATERIAL) {
            return "alexsmobs:textures/armor/unsettling_kimono.png";
        }
        return null;
    }
}
