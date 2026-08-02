package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemModFishBucket extends MobBucketItem {

    private final Supplier<? extends EntityType<?>> fishTypeSupplier;

    public ItemModFishBucket(Supplier<? extends EntityType<?>> fishTypeIn, Fluid fluid, Item.Properties builder) {
        super((EntityType<? extends Mob>) fishTypeIn.get(), fluid, SoundEvents.BUCKET_EMPTY_FISH, builder.stacksTo(1));
        this.fishTypeSupplier = fishTypeIn;
    }

    public EntityType<?> getFishType() {
        return this.fishTypeSupplier.get();
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        EntityType<?> fishType = getFishType();
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag compoundnbt = customData.copyTag();
            if (fishType == AMEntityRegistry.LOBSTER.get()) {
                if (compoundnbt.contains("BucketVariantTag")) {
                    int i = compoundnbt.getIntOr("BucketVariantTag", 0);
                    String s = "entity.alexsmobs.lobster.variant_" + EntityLobster.getVariantName(i);
                    tooltip.add((Component.translatable(s)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                }
            }
            if (fishType == AMEntityRegistry.TERRAPIN.get()) {
                if (compoundnbt.contains("TerrapinData")) {
                    int i = compoundnbt.getCompoundOrEmpty("TerrapinData").getIntOr("TurtleType", 0);
                    tooltip.add((Component.translatable(TerrapinTypes.values()[Mth.clamp(i, 0, TerrapinTypes.values().length - 1)].getTranslationName())).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                }
            }
            if (fishType == AMEntityRegistry.COMB_JELLY.get()) {
                if (compoundnbt.contains("BucketVariantTag")) {
                    int i = compoundnbt.getIntOr("BucketVariantTag", 0);
                    String s = "entity.alexsmobs.comb_jelly.variant_" + i;
                    tooltip.add((Component.translatable(s)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                }
            }
        }
    }

    @Override
    public void checkExtraContent(@Nullable LivingEntity user, Level level, ItemStack stack, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            this.spawnFish(serverLevel, stack, pos);
            level.gameEvent(user, GameEvent.ENTITY_PLACE, pos);
        }
    }

    private void spawnFish(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
        Entity entity = getFishType().spawn(serverLevel, stack, (Player) null, pos, EntitySpawnReason.BUCKET, true, false);
        if (entity instanceof Bucketable bucketable) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                bucketable.loadFromBucketTag(customData.copyTag());
            }
            bucketable.setFromBucket(true);
        }
        addExtraAttributes(entity, stack);
    }

    private void addExtraAttributes(Entity entity, ItemStack stack) {
        if (entity instanceof EntityCatfish catfish) {
            if (stack.is(AMItemRegistry.SMALL_CATFISH_BUCKET.get())) {
                catfish.setCatfishSize(0);
            } else if (stack.is(AMItemRegistry.MEDIUM_CATFISH_BUCKET.get())) {
                catfish.setCatfishSize(1);
            } else if (stack.is(AMItemRegistry.LARGE_CATFISH_BUCKET.get())) {
                catfish.setCatfishSize(2);
            }
        }
    }
}



