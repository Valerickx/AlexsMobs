package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class VineLassoUtil {

    private static final String LASSO_PACKET = "LassoSentPacketAlexsMobs";
    private static final String LASSO_REMOVED = "LassoRemovedAlexsMobs";
    private static final String LASSOED_TO_TAG = "LassoOwnerAlexsMobs";
    private static final String LASSOED_TO_ENTITY_ID_TAG = "LassoOwnerIDAlexsMobs";

    public static void lassoTo(@Nullable LivingEntity lassoer, LivingEntity lassoed) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
        if (lassoer == null) {
            lassoedTag.store(LASSOED_TO_TAG, net.minecraft.core.UUIDUtil.CODEC, UUID.randomUUID());
            lassoedTag.putInt(LASSOED_TO_ENTITY_ID_TAG, -1);
            lassoedTag.putBoolean(LASSO_REMOVED, true);
        } else {
            if (!lassoedTag.contains(LASSOED_TO_ENTITY_ID_TAG) || lassoedTag.getIntOr(LASSOED_TO_ENTITY_ID_TAG, 0) == -1) {
                lassoedTag.store(LASSOED_TO_TAG, net.minecraft.core.UUIDUtil.CODEC, lassoer.getUUID());
                lassoedTag.putInt(LASSOED_TO_ENTITY_ID_TAG, lassoer.getId());
                lassoedTag.putBoolean(LASSO_REMOVED, false);
            }
        }
        lassoedTag.putBoolean(LASSO_PACKET, true);
        CitadelEntityData.setCitadelTag(lassoed, lassoedTag);
        if(!lassoed.level().isClientSide()){
            Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, lassoed.getId()));
        }
    }

    public static boolean hasLassoData(LivingEntity lasso) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lasso);
        return lassoedTag.contains(LASSOED_TO_ENTITY_ID_TAG) && !lassoedTag.getBooleanOr(LASSO_REMOVED, false) && lassoedTag.getIntOr(LASSOED_TO_ENTITY_ID_TAG, 0) != -1;
    }

    public static Entity getLassoedTo(LivingEntity lassoed) {
        CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
        if(lassoedTag.getBooleanOr(LASSO_REMOVED, false)){
            return null;
        }
        if (hasLassoData(lassoed)) {
            if (lassoed.level().isClientSide() && lassoedTag.contains(LASSOED_TO_ENTITY_ID_TAG)) {
                int i = lassoedTag.getIntOr(LASSOED_TO_ENTITY_ID_TAG, 0);
                if (i != -1) {
                    Entity found = lassoed.level().getEntity(i);
                    if (found != null) {
                        return found;
                    } else {
                        UUID uuid = lassoedTag.read(LASSOED_TO_TAG, net.minecraft.core.UUIDUtil.CODEC).orElse(null);
                        if (uuid != null) {
                            return lassoed.level().getPlayerByUUID(uuid);
                        }
                    }
                }
            } else if (lassoed.level() instanceof ServerLevel) {
                UUID uuid = lassoedTag.read(LASSOED_TO_TAG, net.minecraft.core.UUIDUtil.CODEC).orElse(null);
                if (uuid != null) {
                    Entity found = ((ServerLevel) lassoed.level()).getEntity(uuid);
                    if (found != null) {
                        lassoedTag.putInt(LASSOED_TO_ENTITY_ID_TAG, found.getId());
                        return found;
                    }
                }
            }
        }
        return null;
    }

    public static void tickLasso(LivingEntity lassoed) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
        if (!lassoed.level().isClientSide()) {
            if (tag.contains(LASSO_PACKET) || tag.getBooleanOr(LASSO_REMOVED, false)) {
                tag.putBoolean(LASSO_PACKET, false);
                CitadelEntityData.setCitadelTag(lassoed, tag);
                Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", tag, lassoed.getId()));
            }
        }
        Entity lassoedOwner = VineLassoUtil.getLassoedTo(lassoed);
        if (lassoedOwner != null) {
            double distance = lassoed.distanceTo(lassoedOwner);

            if (lassoed instanceof Mob) {
                Mob mob = (Mob) lassoed;
                if (distance > 3.0F) {
                    mob.getNavigation().moveTo(lassoedOwner, 1.0F);
                } else {
                    mob.getNavigation().stop();
                }
            }
            if (distance > 10) {
                double d0 = (lassoedOwner.getX() - lassoed.getX()) / (double)distance;
                double d1 = (lassoedOwner.getY() - lassoed.getY()) / (double)distance;
                double d2 = (lassoedOwner.getZ() - lassoed.getZ()) / (double)distance;
                double yd = Math.copySign(d1 * d1 * 0.4D, d1);
                if(lassoed instanceof Player){
                    yd = 0;
                }
                lassoed.setDeltaMovement(lassoed.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), yd, Math.copySign(d2 * d2 * 0.4D, d2)));
            }
        }
    }
}
