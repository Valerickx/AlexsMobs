package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityCockroachEgg extends ThrowableItemProjectile {

    public EntityCockroachEgg(EntityType<? extends EntityCockroachEgg> p_i50154_1_, Level p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public EntityCockroachEgg(Level worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.COCKROACH_EGG.get(), throwerIn, worldIn, new ItemStack(AMItemRegistry.COCKROACH_OOTHECA.get()));
    }

    public EntityCockroachEgg(Level worldIn, double x, double y, double z) {
        super(AMEntityRegistry.COCKROACH_EGG.get(), x, y, z, worldIn, new ItemStack(AMItemRegistry.COCKROACH_OOTHECA.get()));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(net.minecraft.server.level.ServerEntity serverEntity) {
        return super.getAddEntityPacket(serverEntity);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, (this.getItem().getItem())), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            int i = random.nextInt(3);
            for (int j = 0; j < i; ++j) {
                final EntityCockroach croc = AMEntityRegistry.COCKROACH.get().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                if (croc != null) {
                    croc.setAge(-24000);
                    croc.setPos(this.getX(), this.getY(), this.getZ());
                    croc.setYRot(this.getYRot());
                    croc.setXRot(0.0F);
                    if (level() instanceof ServerLevel serverLevel) {
                        croc.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.TRIGGERED, null);
                    }
                    this.level().addFreshEntity(croc);
                }
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected Item getDefaultItem() {
        return AMItemRegistry.COCKROACH_OOTHECA.get();
    }
}
