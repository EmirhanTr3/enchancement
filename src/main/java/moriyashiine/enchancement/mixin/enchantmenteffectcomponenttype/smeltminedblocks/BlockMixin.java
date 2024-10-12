/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.mixin.enchantmenteffectcomponenttype.smeltminedblocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.enchancement.client.payload.AddMoltenParticlesPayload;
import moriyashiine.enchancement.common.init.ModEnchantmentEffectComponentTypes;
import moriyashiine.enchancement.common.init.ModSoundEvents;
import moriyashiine.enchancement.common.tag.ModBlockTags;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
	@ModifyReturnValue(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"))
	private static List<ItemStack> enchancement$smeltMinedBlocks(List<ItemStack> original, BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack) {
		if (entity != null && entity.isSneaking()) {
			return original;
		}
		if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, ModEnchantmentEffectComponentTypes.SMELT_MINED_BLOCKS) && !original.isEmpty()) {
			original = new ArrayList<>(original);
			boolean smeltsSelf = state.isIn(ModBlockTags.SMELTS_SELF);
			for (int i = 0; i < original.size(); i++) {
				Pair<ItemStack, Float> smelted = getSmeltedStack(world, smeltsSelf ? new ItemStack(state.getBlock()) : original.get(i));
				if (smelted != null) {
					PlayerLookup.tracking(world, pos).forEach(foundPlayer -> AddMoltenParticlesPayload.send(foundPlayer, pos));
					world.playSound(null, pos, ModSoundEvents.BLOCK_GENERIC_SMELT, SoundCategory.BLOCKS, 1, 1);
					original.set(i, smelted.getLeft());
					FurnaceBlockEntity.dropExperience(world, entity != null && EnchantmentHelper.hasAnyEnchantmentsWith(stack, ModEnchantmentEffectComponentTypes.MINE_ORE_VEINS) ? entity.getPos() : Vec3d.of(pos), 1, smelted.getRight());
				}
			}
		}
		return original;
	}

	@Unique
	private static Pair<ItemStack, Float> getSmeltedStack(ServerWorld world, ItemStack stack) {
		for (RecipeEntry<SmeltingRecipe> recipe : world.getRecipeManager().listAllOfType(RecipeType.SMELTING)) {
			for (Ingredient ingredient : recipe.value().getIngredients()) {
				if (ingredient.test(stack)) {
					return new Pair<>(new ItemStack(recipe.value().getResult(world.getRegistryManager()).getItem(), recipe.value().getResult(world.getRegistryManager()).getCount() * stack.getCount()), recipe.value().getExperience());
				}
			}
		}
		return null;
	}
}
