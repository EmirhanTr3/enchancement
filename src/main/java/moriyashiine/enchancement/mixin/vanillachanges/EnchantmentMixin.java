package moriyashiine.enchancement.mixin.vanillachanges;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.enchancement.common.Enchancement;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
	private void enchancement$singleEnchantmentMode(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
		if (Enchancement.getConfig().singleEnchantmentMode) {
			cir.setReturnValue(false);
		}
	}

	@ModifyExpressionValue(method = "getName", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private int enchancement$singleLevelMode(int value) {
		if (Enchancement.getConfig().singleLevelMode) {
			return 1;
		}
		return value;
	}
}