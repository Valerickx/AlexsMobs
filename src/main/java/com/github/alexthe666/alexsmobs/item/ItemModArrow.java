package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class ItemModArrow extends ArrowItem {
    public ItemModArrow(Item.Properties group) {
        super(group);
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon) {
        if (this == AMItemRegistry.SHARK_TOOTH_ARROW.get()) {
            return new EntitySharkToothArrow(worldIn, shooter);
        } else {
            return super.createArrow(worldIn, stack, shooter, weapon);
        }
    }
}
