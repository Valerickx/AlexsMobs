package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AMBlockItem extends BlockItem implements CustomTabBehavior {

    private final DeferredHolder<Block, Block> blockSupplier;

    public AMBlockItem(DeferredHolder<Block, Block> blockSupplier, Item.Properties props) {
        super((Block) null, props);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public Block getBlock() {
        return blockSupplier.get();
    }

    public boolean canFitInsideCraftingRemainingItems() {
        return !(blockSupplier.get() instanceof ShulkerBoxBlock);
    }

    public void onDestroyed(ItemEntity entity) {
        if (this.blockSupplier.get() instanceof ShulkerBoxBlock) {
            ItemStack itemstack = entity.getItem();
            CustomData customData = itemstack.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.copyTag().contains("Items")) {
                CompoundTag compoundtag = customData.copyTag();
                ListTag listtag = compoundtag.getListOrEmpty("Items");
            }
        }
    }

    public boolean canBeHurtBy(ItemStack stack, DamageSource damage) {
        return (this != AMBlockRegistry.TRANSMUTATION_TABLE.get().asItem() || !damage.is(DamageTypeTags.IS_EXPLOSION));
    }

    @Override
    public void fillItemCategory(CreativeModeTab.Output contents) {
        if (blockSupplier.equals(AMBlockRegistry.SAND_CIRCLE) || blockSupplier.equals(AMBlockRegistry.RED_SAND_CIRCLE)) {

        } else {
            contents.accept(this);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS) ? InteractionResult.PASS : super.useOn(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS)) {
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockHitResult blockhitresult1 = blockhitresult.withPosition(blockhitresult.getBlockPos().above());
            return super.useOn(new UseOnContext(player, hand, blockhitresult1));
        } else {
            return super.use(level, player, hand);
        }
    }
}



