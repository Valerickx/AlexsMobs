package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ItemFlutterPot extends Item implements DispensibleContainerItem {

    public ItemFlutterPot(Properties builder) {
        super(builder.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if (!world.isClientSide()) {
            if (this.placeFish((ServerLevel) world, context.getItemInHand(), blockpos) && (context.getPlayer() == null || !context.getPlayer().isCreative())) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    private boolean placeFish(ServerLevel worldIn, ItemStack stack, BlockPos pos) {
        Entity entity = AMEntityRegistry.FLUTTER.get().spawn(worldIn, stack, (Player) null, pos, EntitySpawnReason.BUCKET, true, false);
        if (entity != null && entity instanceof EntityFlutter) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.copyTag().contains("FlutterData")) {
                ((EntityFlutter) entity).readAdditionalSaveData(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, worldIn.registryAccess(), customData.copyTag().getCompoundOrEmpty("FlutterData")));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean emptyContents(@Nullable LivingEntity entity, Level level, BlockPos pos, @Nullable BlockHitResult result) {
        return false;
    }

    @Override
    public void checkExtraContent(@Nullable LivingEntity entity, Level level, ItemStack stack, BlockPos pos) {

    }
}
