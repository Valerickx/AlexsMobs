package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AMMobSpawnStructureModifier implements StructureModifier {

    private static final DeferredHolder<MapCodec<? extends StructureModifier>, MapCodec<AMMobSpawnStructureModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "am_structure_spawns"));

    public AMMobSpawnStructureModifier() {
    }

    @Override
    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == StructureModifier.Phase.ADD) {
            AMWorldRegistry.modifyStructure(structure, builder);

        }
    }

    public MapCodec<? extends StructureModifier> codec() {
        return SERIALIZER.get();
    }

    public static MapCodec<AMMobSpawnStructureModifier> makeCodec() {
        return MapCodec.unit(AMMobSpawnStructureModifier::new);
    }
}
