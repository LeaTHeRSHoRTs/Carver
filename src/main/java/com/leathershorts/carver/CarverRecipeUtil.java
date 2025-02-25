package com.leathershorts.carver;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

import static com.leathershorts.carver.Carver.LOGGER;

public class CarverRecipeUtil {
    private final RecipeExporter exporter;

    public CarverRecipeUtil(RecipeExporter exporter) {
        this.exporter = exporter;
    }

    // Dynamically fetch all wood types tagged as PLANKS
    public final Set<Identifier> WOODS = Registries.BLOCK.stream()
        .filter(block -> Registries.BLOCK.getId(block).getPath().contains("planks"))  // Filter for the "planks" tag
        .map(Registries.BLOCK::getId)                      // Convert the blocks to identifiers
        .collect(Collectors.toSet());                      // Collect them into a set

    // Dynamically configurable wood item suffixes
    public final Map<String, Integer> WOOD_ITEM_SUFFIXES = Map.of(
        "_stairs", 1,
        "_fence", 1,
        "_fence_gate", 1,
        "_pressure_plate", 4,
        "_sign", 2,
        "_slab", 2,
        "_trapdoor", 3
    );

    // Unique items that can be crafted from wood, with dynamic amounts
    public final Map<String, Integer> UNIQUE_ITEMS = Map.of(
        "crafting_table", 1,
        "chiseled_bookshelf", 1,
        "ladder", 3,
        "composter", 1,
        "barrel", 1,
        "stick", 4
    );

    // Dynamic ore-to-product mappings
    public final Map<Identifier, Identifier> ORES = Registries.BLOCK.stream()
        .filter(block -> Registries.BLOCK.getId(block).getPath().endsWith("_ore"))
        .map(Registries.BLOCK::getId)
        .collect(Collectors.toMap(
            ore -> ore,
            ore -> {
                String path = ore.getPath();
                String productName = path
                    .replace("ore", "")
                    .replace("deepslate", "")
                    .replace("nether", "")
                    .replaceAll("_", "");
                if (path.contains("lapis")) {
                    return Identifier.of("minecraft", "lapis_lazuli");
                } else if (path.contains("gold") || path.contains("iron") || path.contains("copper")) {
                    return Identifier.of("minecraft", "raw_" + productName); // For deepslate raw ores
                } else {
                    return Identifier.of("minecraft", productName); // For non-raw deepslate ores
                }
            }
        ));

    //RECIPES-----------------------------------------------------------------------------------------------------------

    // Register dynamic wood recipes
    public CarverRecipeUtil createWood() {
        for (Identifier wood : WOODS) {
            String baseName = wood.getPath().replaceAll("_planks$", "");

            // Register items with standard suffixes
            WOOD_ITEM_SUFFIXES.forEach((suffix, count) -> registerStonecutterRecipe(
                wood,
                Identifier.of(wood.getNamespace(), baseName + suffix),
                count
            ));

            // Register unique items
            UNIQUE_ITEMS.forEach((uniqueItem, count) -> registerStonecutterRecipe(
                wood,
                Identifier.of("minecraft", uniqueItem),
                count
            ));
        }

        LOGGER.info("Registered Wood Recipes");
        return this;
    }

    // Register dynamic stonecutting recipes
    public CarverRecipeUtil createStone() {
        ORES.forEach((ore, product) -> registerStonecutterRecipe(
            ore,
            product,
            2
        ));

        LOGGER.info("Registered Stone Recipes");
        return this;
    }

    private static final Map<Identifier, Map<Identifier, Integer>> misc_table = new HashMap<>();

    public CarverRecipeUtil createMisc(Item input, Item output, int outputAmount) {
        Identifier inputId = Registries.ITEM.getId(input);
        Identifier outputId = Registries.ITEM.getId(output);

        misc_table.computeIfAbsent(inputId, k -> new HashMap<>()).put(outputId, outputAmount);
        return this;
    }

    public void complete() {
        misc_table.forEach((input, outputs) -> outputs.forEach((output, amount) ->
            registerStonecutterRecipe(input, output, amount)
        ));
    }

    // Utility method for stonecutter recipe registration
    private void registerStonecutterRecipe(Identifier input, Identifier output, int amount) {
        try {
            FabricRecipeProvider.offerStonecuttingRecipe(
                this.exporter,
                RecipeCategory.MISC,
                Registries.ITEM.get(output),
                Registries.ITEM.get(input),
                amount
            );
        } catch (Exception e) {
            LOGGER.warn("Skipping invalid stonecutter recipe: {} -> {}", input, output);
        }
    }
}
