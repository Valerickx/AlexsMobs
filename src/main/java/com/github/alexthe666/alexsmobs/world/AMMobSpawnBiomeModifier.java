package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AMMobSpawnBiomeModifier implements BiomeModifier {
    private static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AMMobSpawnBiomeModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "am_mob_spawns"));

    public AMMobSpawnBiomeModifier() {
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addBiomeSpawns(biome, builder);
        }
    }

    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static MapCodec<AMMobSpawnBiomeModifier> makeCodec() {
        return MapCodec.unit(AMMobSpawnBiomeModifier::new);
    }
}



