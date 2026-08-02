package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public class MessageKangarooInventorySync {

    public int kangaroo;
    public int slotId;
    public ItemStack stack;

    public MessageKangarooInventorySync(int kangaroo, int slotId, ItemStack stack) {
        this.kangaroo = kangaroo;
        this.slotId = slotId;
        this.stack = stack;
    }

    public MessageKangarooInventorySync() {
    }

    public static MessageKangarooInventorySync read(RegistryFriendlyByteBuf buf) {
        return new MessageKangarooInventorySync(buf.readInt(), buf.readInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static void write(MessageKangarooInventorySync message, RegistryFriendlyByteBuf buf) {
        buf.writeInt(message.kangaroo);
        buf.writeInt(message.slotId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.stack);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageKangarooInventorySync message, IPayloadContext context) {
            
            context.enqueueWork(() -> {
                Player player = context.player();
                if (context.flow().isClientbound()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }

                if (player != null) {
                    if (player.level() != null) {
                        Entity entity = player.level().getEntity(message.kangaroo);
                        if (entity instanceof EntityKangaroo && ((EntityKangaroo) entity).kangarooInventory != null) {
                            if (message.slotId < 0) {

                            } else {
                                ((EntityKangaroo) entity).kangarooInventory.setItem(message.slotId, message.stack);
                            }
                        }
                    }
                }
            });
        }
    }
}
