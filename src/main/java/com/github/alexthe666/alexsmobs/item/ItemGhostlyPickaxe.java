package com.github.alexthe666.alexsmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemGhostlyPickaxe extends Item {

    public ItemGhostlyPickaxe(Properties props) {
        super(props);
    }

    public static boolean shouldStoreInGhost(LivingEntity player, ItemStack stack) {
        return player instanceof Player && ((Player) player).getInventory().getFreeSlot() == -1;
    }

    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        return blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? 20.0F : 1.0F;
    }

    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity user) {
        if (shouldStoreInGhost(user, stack)) {
            if (user instanceof Player player) {
                player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
                player.causeFoodExhaustion(0.005F);
            }
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                Block.getDrops(state, serverLevel, pos, blockentity, user, stack).forEach((item) -> {
                    putItemInGhostInventoryOrDrop(user, stack, item);
                });
                state.spawnAfterBreak(serverLevel, pos, stack, true);
            }
        }
        return true;
    }

    private static void putItemInGhostInventoryOrDrop(LivingEntity user, ItemStack pickaxe, ItemStack item) {
        if (user instanceof Player player) {
            if (player.getInventory().add(item)) {
                return;
            }
        }
        if (!item.isEmpty()) {
            user.spawnAtLocation((ServerLevel) user.level(), item);
        }
    }

    public boolean isValidRepairItem(ItemStack pickaxe, ItemStack stack) {
        return stack.is(Items.PHANTOM_MEMBRANE);
    }

    private void dropAllContents(Level level, Vec3 vec3, ItemStack pickaxe) {
        CustomData customData = pickaxe.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            pickaxe.remove(DataComponents.CUSTOM_DATA);
        }
    }

    public void onDestroyed(ItemEntity itemEntity) {
        dropAllContents(itemEntity.level(), itemEntity.position(), itemEntity.getItem());
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, java.util.function.Consumer<Item> onBroken) {
        if (stack.getDamageValue() + amount >= stack.getMaxDamage() && entity != null) {
            dropAllContents(entity.level(), entity.position(), stack);
        }
        return amount;
    }
}



