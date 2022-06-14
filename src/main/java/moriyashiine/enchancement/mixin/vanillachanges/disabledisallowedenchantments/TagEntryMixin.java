package moriyashiine.enchancement.mixin.vanillachanges.disabledisallowedenchantments;

import moriyashiine.enchancement.common.ModConfig;
import net.minecraft.tag.TagEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(TagEntry.class)
public class TagEntryMixin {
	@Shadow
	@Final
	private Identifier id;

	@Inject(method = "resolve", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private <T> void enchancement$preventTagsFromAddingRemovedEnchantments(TagEntry.ValueGetter<T> valueGetter, Consumer<T> consumer, CallbackInfoReturnable<Boolean> cir) {
		if (!ModConfig.isEnchantmentAllowed(id) && Registry.ENCHANTMENT.get(id) != null) {
			cir.setReturnValue(true);
		}
	}
}
