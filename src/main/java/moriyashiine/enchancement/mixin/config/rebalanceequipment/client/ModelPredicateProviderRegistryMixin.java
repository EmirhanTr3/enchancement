/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.mixin.config.rebalanceequipment.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.enchancement.common.ModConfig;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelPredicateProviderRegistry.class)
public class ModelPredicateProviderRegistryMixin {
	@ModifyReturnValue(method = "method_27890", at = @At(value = "RETURN", ordinal = 2))
	private static float enchancement$rebalanceEquipment(float original) {
		if (ModConfig.rebalanceEquipment) {
			return original * 1.5F;
		}
		return original;
	}
}
