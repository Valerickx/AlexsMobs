package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.util.GsonHelper;
import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Type;

public class CapsidRecipe {
    private final NonNullList<Ingredient> ingredients;
    private ItemStack result = ItemStack.EMPTY;
    private int time = 0;

    public CapsidRecipe(NonNullList<Ingredient> ingredients, ItemStack result, int time) {
        this.result = result;
        this.ingredients = ingredients;
        this.time = time;
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for(int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, ingredientArray.get(i)).getOrThrow();
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getTime() {
        return time;
    }

    public boolean matches(ItemStack... items) {
        IntList taken = new IntArrayList();
        ItemStack[] copy = new ItemStack[items.length];
        for(int i = 0; i < items.length; i++){
            copy[i] = items[i].copy();
        }
        for(int i = 0; i < ingredients.size(); i++){
            for(int j = 0; j < copy.length; j++){
                if (ingredients.get(i).test(copy[j])) {
                    taken.add(j);
                    copy[j].shrink(1);
                }
            }
        }
        return taken.size() >= ingredients.size();
    }

    public static class Deserializer implements JsonDeserializer<CapsidRecipe> {

        @Override
        public CapsidRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            int time = GsonHelper.getAsInt(jsonobject, "time", 0);
            ItemStack result = ItemStack.EMPTY;
            if (jsonobject.has("result")) {
                result = ItemStack.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, GsonHelper.getAsJsonObject(jsonobject, "result")).getOrThrow();
            }
            NonNullList<Ingredient> nonnulllist = readIngredients(GsonHelper.getAsJsonArray(jsonobject, "ingredients"));
            return new CapsidRecipe(nonnulllist, result, time);
        }

    }
}
