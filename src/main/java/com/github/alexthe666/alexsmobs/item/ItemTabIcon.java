package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class ItemTabIcon extends ItemInventoryOnly {
    public ItemTabIcon(Properties properties) {
        super(properties);
    }

    public static boolean hasCustomEntityDisplay(ItemStack stack) {
        net.minecraft.world.item.component.CustomData customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().contains("DisplayEntityType");
    }

    public static String getCustomDisplayEntityString(ItemStack stack) {
        net.minecraft.world.item.component.CustomData customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag().getStringOr("DisplayEntityType", "") : "";
    }

    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }

    @Nullable
    public static EntityType getEntityType(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains("DisplayEntityType")) {
            String entityType = tag.getStringOr("DisplayEntityType", "");
            return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.tryParse(entityType));
        }
        return null;
    }
}
