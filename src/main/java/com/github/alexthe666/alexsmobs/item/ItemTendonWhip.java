package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

public class ItemTendonWhip extends Item implements ILeftClick {

    public ItemTendonWhip(Item.Properties props) {
        super(props);
    }

    public static boolean isActive(ItemStack stack, LivingEntity holder) {
        if (holder != null && (holder.getMainHandItem() == stack || holder.getOffhandItem() == stack)) {
            return !TendonWhipUtil.canLaunchTendons(holder.level(), holder);
        }
        return false;
    }

    public boolean onLeftClick(ItemStack stack, LivingEntity playerIn){
        if(stack.is(AMItemRegistry.TENDON_WHIP.get()) && (!(playerIn instanceof Player) || isCharged((Player)playerIn, stack))){
            Level worldIn = playerIn.level();
            Entity closestValid = null;
            Vec3 playerEyes = playerIn.getEyePosition(1.0F);
            HitResult hitresult = worldIn.clip(new ClipContext(playerEyes, playerEyes.add(playerIn.getLookAngle().scale(12.0D)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, playerIn));
            if (hitresult instanceof EntityHitResult) {
                Entity entity = ((EntityHitResult) hitresult).getEntity();
                if (!entity.equals(playerIn) && !playerIn.isAlliedTo(entity) && !entity.isAlliedTo(playerIn) && entity instanceof Mob && playerIn.hasLineOfSight(entity)) {
                    closestValid = entity;
                }
            } else {
                for (Entity entity : worldIn.getEntitiesOfClass(LivingEntity.class, playerIn.getBoundingBox().inflate(12.0D))) {
                    if (!entity.equals(playerIn) && !playerIn.isAlliedTo(entity) && !entity.isAlliedTo(playerIn) && entity instanceof Mob && playerIn.hasLineOfSight(entity)) {
                        if (closestValid == null || playerIn.distanceTo(entity) < playerIn.distanceTo(closestValid)) {
                            closestValid = entity;
                        }
                    }
                }
            }
            if(closestValid != null){
                stack.hurtAndBreak(1, playerIn, playerIn.getUsedItemHand());
            }
            return launchTendonsAt(stack, playerIn, closestValid);
        }
        return false;
    }

    private boolean isCharged(Player player, ItemStack stack){
        return player.getAttackStrengthScale(0.5F) > 0.9F;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity player) {
        launchTendonsAt(stack, player, entity);
        super.hurtEnemy(stack, entity, player);
    }

    public boolean launchTendonsAt(ItemStack stack, LivingEntity playerIn, Entity closestValid) {
        Level worldIn = playerIn.level();
        if (TendonWhipUtil.canLaunchTendons(worldIn, playerIn)) {
            TendonWhipUtil.retractFarTendons(worldIn, playerIn);
            if (!worldIn.isClientSide()) {
                if (closestValid != null) {
                    EntityTendonSegment segment = AMEntityRegistry.TENDON_SEGMENT.get().create(worldIn, EntitySpawnReason.MOB_SUMMONED);
                    segment.copyPosition(playerIn);
                    worldIn.addFreshEntity(segment);
                    segment.setCreatorEntityUUID(playerIn.getUUID());
                    segment.setFromEntityID(playerIn.getId());
                    segment.setToEntityID(closestValid.getId());
                    segment.copyPosition(playerIn);
                    segment.setProgress(0.0F);
                    segment.setHasGlint(stack.hasFoil());
                    TendonWhipUtil.setLastTendon(playerIn, segment);
                    return true;
                }
            }
        }
        return false;
    }
}



