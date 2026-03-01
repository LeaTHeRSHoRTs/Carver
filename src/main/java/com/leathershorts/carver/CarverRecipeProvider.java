package com.leathershorts.carver;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeGenerator;
import net.minecraft.data.server.recipe.StonecuttingRecipeJsonBuilder;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CarverRecipeProvider extends FabricRecipeProvider {
    public static final Logger LOGGER = LoggerFactory.getLogger("carver");
    private final Set<String> shouldTest = new HashSet<>();

    public CarverRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(registryLookup, recipeExporter) {
            private static int recipes = 0;

            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = this.registries.getOrThrow(RegistryKeys.ITEM);
                List<Item> planks = getRegistryItems(itemLookup, ".*planks$");
                List<Item> wood = getRegistryItems(itemLookup, ".*wood$");
                List<Item> logs = getRegistryItems(itemLookup, ".*log$");
                List<Item> glass = getRegistryItems(itemLookup, "^(.*_)?glass$");

                // Stone & Ores
                List.of(
                    Items.STONE,
                    Items.SMOOTH_STONE,
                    Items.STONE_BRICKS,
                    Items.MOSSY_STONE_BRICKS,
                    Items.CRACKED_STONE_BRICKS,
                    Items.CHISELED_STONE_BRICKS
                ).forEach(stone -> this.offerRecipe(RecipeCategory.BUILDING_BLOCKS, stone, Items.COBBLESTONE, 1));

                List.of(
                    Items.DEEPSLATE,
                    Items.DEEPSLATE_BRICKS,
                    Items.DEEPSLATE_TILES,
                    Items.CHISELED_DEEPSLATE,
                    Items.POLISHED_DEEPSLATE,
                    Items.CRACKED_DEEPSLATE_BRICKS,
                    Items.CRACKED_DEEPSLATE_TILES
                ).forEach(stone -> this.offerRecipe(RecipeCategory.BUILDING_BLOCKS, stone, Items.COBBLED_DEEPSLATE, 1));

                // Ores
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.COAL_ORES), Items.COAL, 2);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.IRON_ORES), Items.RAW_IRON, 2);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.GOLD_ORES), Items.RAW_GOLD, 2);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.EMERALD_ORES), Items.EMERALD, 2);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.COPPER_ORES), Items.RAW_COPPER, 2);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.DIAMOND_ORES), Items.DIAMOND, 2);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.LAPIS_ORES), Items.LAPIS_LAZULI, 4);
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.REDSTONE_ORES), Items.REDSTONE, 4);
                this.offerRecipe(RecipeCategory.MISC, Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP, 1);

                // Wood
                var plankList = itemLookup.getOrThrow(ItemTags.PLANKS);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.STICK, 4);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.CRAFTING_TABLE, 1);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.CHISELED_BOOKSHELF, 1);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.LADDER, 3);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.COMPOSTER, 1);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.BARREL, 1);
                this.offerRecipe(RecipeCategory.MISC, plankList, Items.BOWL, 2);
                this.offerRecipe(RecipeCategory.MISC, Items.OAK_LOG, Items.PETRIFIED_OAK_SLAB, 1);

                for (Item plank : planks) {
                    String plankName = Registries.ITEM.getId(plank).getPath().replace("_planks", "");
                    Function<String, Item> get = (suffix) ->
                        Registries.ITEM.get(Identifier.of("minecraft", plankName + suffix));

                    this.offerRecipes(RecipeCategory.BUILDING_BLOCKS, Ingredient.ofItem(plank), Map.of(
                        get.apply("_slab"), 2,
                        get.apply("_stairs"), 1,
                        get.apply("_fence"), 1,
                        get.apply("_fence_gate"), 1,
                        get.apply("_button"), 2,
                        get.apply("_pressure_plate"), 4,
                        get.apply("_door"), 1,
                        get.apply("_trapdoor"), 3,
                        get.apply("_sign"), 3
                    ));
                }

                for (Item logItem : logs) {
                    Identifier normalLog = Registries.ITEM.getId(logItem);
                    Item strippedLog = Registries.ITEM.get(Identifier.of("stripped_" + normalLog.getPath()));

                    if (strippedLog != Items.AIR) {
                        this.offerRecipe(RecipeCategory.DECORATIONS, logItem, strippedLog, 1);
                    }
                }

                for (Item woodItem : wood) {
                    Identifier normalWood = Registries.ITEM.getId(woodItem);
                    Item strippedWood = Registries.ITEM.get(Identifier.of("stripped_" + normalWood.getPath()));

                    if (strippedWood != Items.AIR) {
                        this.offerRecipe(RecipeCategory.DECORATIONS, woodItem, strippedWood, 1);
                    }
                }

                // Glass
                for (Item glassItem : glass) {
                    String name = Registries.ITEM.getId(glassItem).getPath();
                    Item pane = Registries.ITEM.get(Identifier.of("minecraft", name + "_pane"));

                    if (pane != Items.AIR) {
                        this.offerRecipe(RecipeCategory.DECORATIONS, glassItem, pane, 3);
                    }
                }

                // Others
                this.offerRecipe(RecipeCategory.MISC, itemLookup.getOrThrow(ItemTags.WOOL), Items.STRING, 4);
                this.offerRecipe(RecipeCategory.MISC, Items.GLOWSTONE, Items.GLOWSTONE_DUST, 4);
                this.offerRecipe(RecipeCategory.MISC, Items.QUARTZ_BLOCK, Items.QUARTZ, 4);
                this.offerRecipe(RecipeCategory.MISC, Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD, 4);
                this.offerRecipe(RecipeCategory.MISC, Items.AMETHYST_BLOCK, Items.AMETHYST_CLUSTER, 1);
                this.offerRecipe(RecipeCategory.MISC, Items.AMETHYST_CLUSTER, Items.AMETHYST_SHARD, 4);

                LOGGER.info("Generated {} recipes!", recipes);
                for (String item : shouldTest) {
                    LOGGER.info("Should test recipes for {}", item);
                }
            }

            @Contract("_, _ -> new")
            private @NotNull PopulatedList<Item> getRegistryItems(RegistryWrapper.@NotNull Impl<Item> lookup, @NotNull String pattern) {
                return new PopulatedList<>(lookup.streamEntries()
                    .filter(entry -> entry.getKey().orElseThrow().getValue().getPath().matches(pattern))
                    .map(RegistryEntry::value)
                    .toList());
            }

            public void offerRecipe(RecipeCategory category, Ingredient item, Item product, int count) {
                StonecuttingRecipeJsonBuilder recipe = StonecuttingRecipeJsonBuilder
                    .createStonecutting(item, category, product, count);

                item.getMatchingItems().forEach(itemRegistryEntry ->
                    recipe.criterion(
                        hasItem(itemRegistryEntry.value()),
                        this.conditionsFromItem(itemRegistryEntry.value())
                    )
                );

                String inputName = Registries.ITEM.getId(item.getMatchingItems().getFirst().value()).getPath();
                String productName = Registries.ITEM.getId(product).getPath();
                String path = String.format("%s_to_%s", inputName, productName);
                RegistryKey<Recipe<?>> recipeKey = RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("carver", path));

                recipe.offerTo(this.exporter, recipeKey);
                recipes++;
                for (RegistryEntry<Item> entry : item.getMatchingItems()) {
                    Item value = entry.value();
                    String itemName = value.toString();
                    shouldTest.add(itemName);
                }
            }

            public void offerRecipe(RecipeCategory category, Item item, Item product, int count) {
                this.offerRecipe(category, Ingredient.ofItem(item), product, count);
            }

            public void offerRecipe(RecipeCategory category, RegistryEntryList.Named<Item> list, Item product, int count) {
                Ingredient input = Ingredient.fromTag(list);

                StonecuttingRecipeJsonBuilder.createStonecutting(input, category, product, count)
                    .criterion("has_item", conditionsFromItem(product))
                    .offerTo(this.exporter, RegistryKey.of(RegistryKeys.RECIPE,
                        Identifier.of("carver", "stonecutting/" + Registries.ITEM.getId(product).getPath())));

                LOGGER.info("Created recipe {} to {}", input, product);
                recipes++;
            }

            public void offerRecipes(RecipeCategory category, Ingredient input, Map<Item, Integer> outputs) {
                outputs.forEach((output, count) -> {
                    this.offerRecipe(category, input, output, count);
                });
            }

            public void offerRecipes(RecipeCategory category, Item input, Map<Item, Integer> outputs) {
                this.offerRecipes(category, Ingredient.ofItem(input), outputs);
            }
        };
    }

    @Override
    public String getName() {
        return "CarverRecipeProvider";
    }
}
