package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapsidRecipeManager extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(CapsidRecipe.class, new CapsidRecipe.Deserializer()).create();
    private static final RandomSource RANDOM = RandomSource.create();

    private final List<CapsidRecipe> capsidRecipes = Lists.newArrayList();

    public CapsidRecipeManager() {
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> map = new HashMap<>();
        FileToIdConverter converter = FileToIdConverter.json("capsid_recipes");
        for (Map.Entry<Identifier, Resource> entry : converter.listMatchingResources(manager).entrySet()) {
            Identifier id = converter.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                map.put(id, json);
            } catch (Exception e) {
                AlexsMobs.LOGGER.error("Error loading capsid recipe {}", entry.getKey(), e);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profile) {
        this.capsidRecipes.clear();
        ImmutableMap.Builder<Identifier, CapsidRecipe> builder = ImmutableMap.builder();
        AlexsMobs.LOGGER.log(Level.ALL, "Loading in capsid_recipes jsons...");
        jsonMap.forEach((identifier, jsonElement) -> {
            try {
                CapsidRecipe capsidRecipe = GSON.fromJson(jsonElement, CapsidRecipe.class);
                builder.put(identifier, capsidRecipe);
            } catch (Exception exception) {
                AlexsMobs.LOGGER.error("Couldn't parse capsid recipe {}", identifier, exception);
            }
        });
        ImmutableMap<Identifier, CapsidRecipe> immutablemap = builder.build();
        immutablemap.forEach((identifier, capsidRecipe) -> {
            capsidRecipes.add(capsidRecipe);
        });
    }

    public CapsidRecipe getRecipeFor(ItemStack stack){
        for(CapsidRecipe recipe : capsidRecipes){
            if(recipe.matches(stack)){
                return recipe;
            }
        }

        return null;
    }

    public List<CapsidRecipe> getCapsidRecipes() {
        return capsidRecipes;
    }
}
