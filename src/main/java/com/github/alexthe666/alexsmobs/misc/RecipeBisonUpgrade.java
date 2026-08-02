package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeBisonUpgrade extends CustomRecipe {
    public static final RecipeSerializer<RecipeBisonUpgrade> SERIALIZER = new RecipeSerializer<>(
        com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(i -> i.point(new RecipeBisonUpgrade())),
        net.minecraft.network.codec.StreamCodec.unit(new RecipeBisonUpgrade())
    );

    public RecipeBisonUpgrade() {
    }

    private ItemStack createBoots(CraftingInput container){
        ItemStack boots = ItemStack.EMPTY;
        int fur = 0;
        for (int j = 0; j < container.size(); ++j) {
            ItemStack itemstack1 = container.getItem(j);
            if (itemstack1.is(AMBlockRegistry.BISON_FUR_BLOCK.get().asItem())) {
                fur++;
            }
        }
        if(fur == 1){
            for (int j = 0; j < container.size(); ++j) {
                ItemStack itemstack1 = container.getItem(j);
                CustomData customData = itemstack1.get(DataComponents.CUSTOM_DATA);
                CompoundTag tag = customData != null ? customData.copyTag() : null;
                boolean notFurred = tag == null || !tag.getBooleanOr("BisonFur", false);
                boolean isBoots = itemstack1.getEquipmentSlot() == EquipmentSlot.FEET;
                if (!itemstack1.isEmpty() && notFurred && isBoots) {
                    boots = itemstack1;
                }
            }
            if(!boots.isEmpty()){
                ItemStack stack = boots.copy();
                CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> t.putBoolean("BisonFur", true));
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        return !createBoots(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput container) {
        return createBoots(container);
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<RecipeBisonUpgrade> getSerializer() {
        return AMRecipeRegistry.BISON_UPGRADE.get();
    }
}



