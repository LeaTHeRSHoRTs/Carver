package com.leathershorts.carver;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CarverRecipeProvider extends FabricRecipeProvider {
    public CarverRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        new CarverRecipeUtil(recipeExporter)
            .createWood()
            .createStone()
            .createMisc(Items.GLOWSTONE, Items.GLOWSTONE_DUST, 4)
            .createMisc(Items.QUARTZ_BLOCK, Items.QUARTZ, 4)
            .createMisc(Items.WHITE_WOOL, Items.STRING, 4)
            .createMisc(Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD, 4)
            .createMisc(Items.AMETHYST_BLOCK, Items.AMETHYST_CLUSTER, 1)
            .createMisc(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP, 1)
            .complete();
    }
}
