package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMEnchantmentRegistry {

    public static final DeferredRegister<Enchantment> DEF_REG = DeferredRegister.create(Registries.ENCHANTMENT, AlexsMobs.MODID);

    public static final ResourceKey<Enchantment> STRADDLE_JUMP = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "straddle_jump"));
    public static final ResourceKey<Enchantment> STRADDLE_LAVAWAX = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "lavawax"));
    public static final ResourceKey<Enchantment> STRADDLE_SERPENTFRIEND = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "serpentfriend"));
    public static final ResourceKey<Enchantment> STRADDLE_BOARDRETURN = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "board_return"));
}
