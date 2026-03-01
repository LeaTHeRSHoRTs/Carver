package com.leathershorts.carver;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

public class CarverDataGenerator implements DataGeneratorEntrypoint {
import java.util.concurrent.CompletableFuture;

final class CarverDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(CarverRecipeProvider::new);

        var blockTags = pack.addProvider(BlockTagGenerator::new);
        pack.addProvider((out, reg) -> new ItemTagGenerator(out, reg, blockTags));
        pack.addProvider(CarverRecipeProvider::new);
	}

    private static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
        public BlockTagGenerator(FabricDataOutput o, CompletableFuture<RegistryWrapper.WrapperLookup> r) { super(o, r); }
        @Override protected void configure(RegistryWrapper.WrapperLookup arg) {}
    }

    private static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
        public ItemTagGenerator(FabricDataOutput o, CompletableFuture<RegistryWrapper.WrapperLookup> r, BlockTagProvider b) { super(o, r, b); }
        @Override protected void configure(RegistryWrapper.WrapperLookup arg) {}
    }
}
