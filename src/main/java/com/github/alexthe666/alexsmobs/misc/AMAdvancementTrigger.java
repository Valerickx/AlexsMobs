package com.github.alexthe666.alexsmobs.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.TriggerInstance> {
    public final Identifier identifier;

    public AMAdvancementTrigger(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Codec<AMAdvancementTrigger.TriggerInstance> codec() {
        return AMAdvancementTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, t -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<AMAdvancementTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(AMAdvancementTrigger.TriggerInstance::player)
                )
                .apply(i, AMAdvancementTrigger.TriggerInstance::new)
        );
    }
}
