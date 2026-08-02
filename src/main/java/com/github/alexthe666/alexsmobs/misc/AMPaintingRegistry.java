package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class AMPaintingRegistry {
    public static final DeferredRegister<PaintingVariant> DEF_REG = DeferredRegister.create(Registries.PAINTING_VARIANT, AlexsMobs.MODID);

    public static final DeferredHolder<PaintingVariant, PaintingVariant> NFT = DEF_REG.register("nft", () -> new PaintingVariant(32, 32, Identifier.fromNamespaceAndPath("alexsmobs", "nft"), Optional.empty(), Optional.empty()));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> DOG_POKER = DEF_REG.register("dog_poker", () -> new PaintingVariant(32, 16, Identifier.fromNamespaceAndPath("alexsmobs", "dog_poker"), Optional.empty(), Optional.empty()));
}



