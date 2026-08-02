package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BananaLootModifier extends LootModifier {

    public static final Supplier<MapCodec<BananaLootModifier>> CODEC = () ->
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst).apply(inst, BananaLootModifier::new));

    public BananaLootModifier(LootItemCondition[] conditionsIn, int priority) {
        super(conditionsIn, priority);
    }

    public BananaLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn, 0);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.bananasDropFromLeaves) {
            Object toolObj = context.getOptionalParameter(LootContextParams.TOOL);
            ItemStack ctxTool = toolObj instanceof ItemStack s ? s : null;
            RandomSource random = context.getRandom();
            if (ctxTool != null) {
                Holder<Enchantment> silkTouch = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
                int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, ctxTool);
                if (silkTouchLevel > 0 || ctxTool.getItem() instanceof ShearsItem) {
                    return generatedLoot;
                }
            }
            Holder<Enchantment> fortune = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            int bonusLevel = ctxTool != null ? EnchantmentHelper.getItemEnchantmentLevel(fortune, ctxTool) : 0;
            int bananaStep = (int) Math.floor(AMConfig.bananaChance * 0.1F);
            int bananaRarity = AMConfig.bananaChance - (bonusLevel * bananaStep);
            if (bananaRarity < 1 || random.nextInt(bananaRarity) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.BANANA.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}



