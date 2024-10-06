/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.data.provider;

import moriyashiine.enchancement.common.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.util.Identifier.tryParse;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public ModItemTagProvider(FabricDataOutput output) {
		super(output, CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		getOrCreateTagBuilder(ModItemTags.CANNOT_ASSIMILATE)
				.addOptionalTag(ConventionalItemTags.RAW_FISH_FOODS)
				.addOptionalTag(ConventionalItemTags.RAW_MEAT_FOODS)
				.addOptionalTag(ConventionalItemTags.FOOD_POISONING_FOODS)
				.add(Items.CHORUS_FRUIT)
				.add(Items.ENCHANTED_GOLDEN_APPLE)
				.add(Items.GOLDEN_APPLE)
				.addOptionalTag(tryParse("c:foods/doughs"))
				.addOptionalTag(tryParse("c:foods/pastas"))
				.addOptional(tryParse("farmersdelight:dog_food"))
				.addOptional(tryParse("farmersdelight:pie_crust"));
		getOrCreateTagBuilder(ModItemTags.NO_LOYALTY)
				.addOptional(tryParse("impaled:pitchfork"));
		getOrCreateTagBuilder(ModItemTags.RETAINS_DURABILITY)
				.add(Items.WOLF_ARMOR)
				.addOptionalTag(tryParse("create:sandpaper"))
				.addOptional(tryParse("create:super_glue"));
		getOrCreateTagBuilder(ModItemTags.WEAKLY_ENCHANTED)
				.addOptional(tryParse("impaled:pitchfork"));
	}
}
