package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;

public class TransmutationData {
    private final Object2DoubleMap<ItemStack> itemstackData = new Object2DoubleOpenHashMap<>();

    public void onTransmuteItem(ItemStack beingTransmuted, ItemStack turnedInto){
        double fromWeight = getWeight(beingTransmuted);
        double toWeight = getWeight(turnedInto);
        putWeight(beingTransmuted, fromWeight + calculateAddWeight(beingTransmuted.getCount()));
        putWeight(turnedInto, toWeight + calculateRemoveWeight(turnedInto.getCount()));
    }

    public double getWeight(ItemStack stack){
        for(Object2DoubleMap.Entry<ItemStack> entry : itemstackData.object2DoubleEntrySet()){
            if(ItemStack.areEqual(stack, entry.getKey())){
                return entry.getDoubleValue();
            }
        }
        return 0.0;
    }

    private static double calculateAddWeight(int count){
        return Math.log(Math.pow(count, AMConfig.transmutingWeightAddStep));
    }

    private static double calculateRemoveWeight(int count){
        return -Math.log(Math.pow(count, AMConfig.transmutingWeightRemoveStep));
    }

    public void putWeight(ItemStack stack, double newWeight){
        ItemStack replace = stack;
        for(ItemStack entry : itemstackData.keySet()){
            if(ItemStack.areEqual(stack, entry)){
                replace = entry;
                break;
            }
        }
        itemstackData.put(replace, Math.max(newWeight, 0.0F));
    }

    @Nullable
    public ItemStack getRandomItem(Random random) {
        ItemStack result = null;
        double bestValue = Double.MAX_VALUE;
        for(Object2DoubleMap.Entry<ItemStack> entry : itemstackData.object2DoubleEntrySet()){
            if(entry.getDoubleValue() <= 0.0){
                continue;
            }else{
                final double value = -Math.log(random.nextDouble()) / entry.getDoubleValue();
                if (value < bestValue) {
                    bestValue = value;
                    result = entry.getKey().copy();
                }
            }
        }
        return result;
    }

    public void saveToValueOutput(ValueOutput output){
        int i = 0;
        for(Object2DoubleMap.Entry<ItemStack> entry : itemstackData.object2DoubleEntrySet()) {
            ValueOutput itemEntry = output.write("Entry" + i);
            ItemStack.CODEC.encode(entry.getKey(), itemEntry.write("Item"));
            itemEntry.writeDouble("Weight", entry.getDoubleValue());
            i++;
        }
        output.writeInt("Size", i);
    }

    public static TransmutationData fromValueInput(ValueInput input){
        TransmutationData data = new TransmutationData();
        int size = input.getIntOr("Size", 0);
        for (int i = 0; i < size; ++i) {
            java.util.Optional<ValueInput> entryOpt = input.read("Entry" + i);
            if (entryOpt.isPresent()) {
                ValueInput itemEntry = entryOpt.get();
                try {
                    java.util.Optional<ValueInput> itemIn = itemEntry.read("Item");
                    double weight = itemEntry.getDoubleOr("Weight", 0.0);
                    if (itemIn.isPresent()) {
                        ItemStack from = ItemStack.CODEC.decode(itemIn.get()).result()
                                .map(p -> p.getFirst()).orElse(ItemStack.EMPTY);
                        if (!from.isEmpty() && weight > 0.0) {
                            data.putWeight(from, weight);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public double getTotalWeight() {
        return itemstackData.values().doubleStream().sum();
    }
}
