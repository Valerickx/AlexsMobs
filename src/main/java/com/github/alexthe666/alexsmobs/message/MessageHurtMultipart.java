package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public class MessageHurtMultipart {

    public int part;
    public int parent;
    public float damage;
    public String damageType;

    public MessageHurtMultipart(int part, int parent, float damage) {
        this.part = part;
        this.parent = parent;
        this.damage = damage;
        this.damageType = "";
    }

    public MessageHurtMultipart(int part, int parent, float damage, String damageType) {
        this.part = part;
        this.parent = parent;
        this.damage = damage;
        this.damageType = damageType;
    }

    public MessageHurtMultipart() {
    }

    public static MessageHurtMultipart read(FriendlyByteBuf buf) {
        return new MessageHurtMultipart(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readUtf());
    }

    public static void write(MessageHurtMultipart message, FriendlyByteBuf buf) {
        buf.writeInt(message.part);
        buf.writeInt(message.parent);
        buf.writeFloat(message.damage);
        buf.writeUtf(message.damageType);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageHurtMultipart message, IPayloadContext context) {
            
            context.enqueueWork(() -> {
                Player player = context.player();
                if (context.flow().isClientbound()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }

                if (player != null) {
                    if (player.level() != null) {
                        Entity part = player.level().getEntity(message.part);
                        Entity parent = player.level().getEntity(message.parent);
                        var registry = player.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE);
                        var holder = registry.get(net.minecraft.resources.ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse(message.damageType))).orElse(null);
                        if (holder != null) {
                            DamageSource source = new DamageSource(holder);
                            if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                                ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity) parent, message.damage, source);
                            }
                            if (part == null && parent != null && parent.isMultipartEntity()) {
                                parent.hurt(source, message.damage);
                            }
                        }
                    }
                }
            });
        }
    }
}



