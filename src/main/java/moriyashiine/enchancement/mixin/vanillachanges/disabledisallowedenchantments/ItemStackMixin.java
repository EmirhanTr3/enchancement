/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.mixin.vanillachanges.disabledisallowedenchantments;

import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@ModifyVariable(method = "addEnchantment", at = @At("HEAD"), argsOnly = true)
	private RegistryEntry<Enchantment> enchancement$disableDisallowedEnchantments(RegistryEntry<Enchantment> value) {
		if (!EnchancementUtil.isEnchantmentAllowed(value)) {
			return EnchancementUtil.getReplacement(value, (ItemStack) (Object) this);
		}
		return value;
	}
}
