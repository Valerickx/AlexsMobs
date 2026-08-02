package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public class MessageUpdateTransmutablesToDisplay {

    private int playerId;
    public ItemStack stack1;
    public ItemStack stack2;
    public ItemStack stack3;

    public MessageUpdateTransmutablesToDisplay(int playerId, ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        this.stack1 = stack1;
        this.stack2 = stack2;
        this.stack3 = stack3;
        this.playerId = playerId;
    }

    public MessageUpdateTransmutablesToDisplay() {
    }

    public static MessageUpdateTransmutablesToDisplay read(RegistryFriendlyByteBuf buf) {
        return new MessageUpdateTransmutablesToDisplay(buf.readInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static void write(MessageUpdateTransmutablesToDisplay message, RegistryFriendlyByteBuf buf) {
        buf.writeInt(message.playerId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.stack1);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.stack2);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.stack3);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateTransmutablesToDisplay message, IPayloadContext context) {
            
            context.enqueueWork(() -> {
                Player player = context.player();
                if (context.flow().isClientbound()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player.getId() == message.playerId) {
                    AlexsMobs.PROXY.setDisplayTransmuteResult(0, message.stack1);
                    AlexsMobs.PROXY.setDisplayTransmuteResult(1, message.stack2);
                    AlexsMobs.PROXY.setDisplayTransmuteResult(2, message.stack3);
                }
            });
        }
    }
}



