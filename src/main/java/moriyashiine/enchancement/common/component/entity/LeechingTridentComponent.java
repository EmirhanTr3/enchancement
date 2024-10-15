/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.common.component.entity;

import moriyashiine.enchancement.common.enchantment.effect.LeechingTridentEffect;
import moriyashiine.enchancement.common.init.ModDamageTypes;
import moriyashiine.enchancement.common.init.ModEnchantmentEffectComponentTypes;
import moriyashiine.enchancement.common.init.ModEntityComponents;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.Collections;

public class LeechingTridentComponent implements AutoSyncedComponent, CommonTickingComponent {
	private final TridentEntity obj;
	private LeechData leechData = null;
	private LivingEntity stuckEntity = null;
	private int stuckEntityId = -1;
	private int ticksLeeching = 0;
	private float renderTicks = 0, stabTicks = 0;

	public LeechingTridentComponent(TridentEntity obj) {
		this.obj = obj;
	}

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (tag.contains("LeechData")) {
			leechData = LeechData.deserialize(tag.getCompound("LeechData"));
		}
		stuckEntityId = tag.getInt("StuckEntityId");
		ticksLeeching = tag.getInt("TicksLeeching");
		renderTicks = tag.getFloat("RenderTicks");
		stabTicks = tag.getFloat("StabTicks");
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (leechData != null) {
			tag.put("LeechData", leechData.serialize());
		}
		tag.putInt("StuckEntityId", stuckEntityId);
		tag.putInt("TicksLeeching", ticksLeeching);
		tag.putFloat("RenderTicks", renderTicks);
		tag.putFloat("StabTicks", stabTicks);
	}

	@Override
	public void tick() {
		if (stuckEntityId == -2) {
			stuckEntity = null;
			stuckEntityId = -1;
		} else if (stuckEntityId != -1 && stuckEntity == null && obj.getWorld().getEntityById(stuckEntityId) instanceof LivingEntity living) {
			stuckEntity = living;
		} else {
			if (stuckEntity != null && stuckEntity.isAlive()) {
				obj.setVelocity(Vec3d.ZERO);
				if (++ticksLeeching > leechData.maxTicks()) {
					stuckEntityId = -2;
				}
				renderTicks += 1 / 20F;
				stabTicks = Math.max(0, stabTicks - stabTicks / 20F);
			} else {
				stuckEntityId = -2;
				ticksLeeching = 0;
				renderTicks = 0;
				stabTicks = 0;
			}
		}
	}

	@Override
	public void serverTick() {
		tick();
		if (stuckEntity != null && stuckEntity.isAlive()) {
			if (obj.getOwner() instanceof LivingEntity living && living.isAlive()) {
				obj.refreshPositionAfterTeleport(stuckEntity.getX(), stuckEntity.getEyeY(), stuckEntity.getZ());
				if (ticksLeeching % 20 == 0 && stuckEntity.damage(ModDamageTypes.create(obj.getWorld(), ModDamageTypes.LIFE_DRAIN, obj, living), leechData.damage())) {
					living.heal(leechData.healAmount());
					stuckEntity.timeUntilRegen = 0;
					stabTicks = 1;
					sync();
				}
			} else {
				stuckEntityId = -2;
				sync();
			}
		}
	}

	@Override
	public void clientTick() {
		tick();
		if (stuckEntity != null && stuckEntity.isAlive() && stabTicks == 19 / 20F) {
			if (MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson() || stuckEntity != MinecraftClient.getInstance().cameraEntity) {
				for (int i = 0; i < 6; i++) {
					obj.getWorld().addParticle(ParticleTypes.DAMAGE_INDICATOR, stuckEntity.getParticleX(0.5), stuckEntity.getBodyY(0.5), stuckEntity.getParticleZ(0.5), 0, 0, 0);
				}
			}
		}
	}

	public void sync() {
		ModEntityComponents.LEECHING_TRIDENT.sync(obj);
	}

	public boolean hasLeech() {
		return leechData != null;
	}

	public LivingEntity getStuckEntity() {
		return stuckEntity;
	}

	public void setStuckEntityId(int stuckEntityId) {
		this.stuckEntityId = stuckEntityId;
	}

	public float getRenderTicks() {
		return renderTicks;
	}

	public float getStabTicks() {
		return stabTicks;
	}

	public static void maybeSet(LivingEntity user, ItemStack stack, Entity entity) {
		if (entity instanceof TridentEntity) {
			MutableFloat damage = new MutableFloat(), healAmount = new MutableFloat(), duration = new MutableFloat();
			if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, ModEnchantmentEffectComponentTypes.LEECHING_TRIDENT)) {
				LeechingTridentEffect.setValues(user.getRandom(), damage, healAmount, duration, Collections.singleton(stack));
			} else if (!(user instanceof PlayerEntity) && EnchancementUtil.hasAnyEnchantmentsWith(user, ModEnchantmentEffectComponentTypes.LEECHING_TRIDENT)) {
				LeechingTridentEffect.setValues(user.getRandom(), damage, healAmount, duration, user.getEquippedItems());
			}
			if (damage.floatValue() != 0) {
				LeechingTridentComponent leechingTridentComponent = ModEntityComponents.LEECHING_TRIDENT.get(entity);
				leechingTridentComponent.leechData = new LeechData(damage.floatValue(), healAmount.floatValue(), MathHelper.floor(duration.floatValue() * 20));
				leechingTridentComponent.sync();
			}
		}
	}

	public record LeechData(float damage, float healAmount, int maxTicks) {
		static LeechData deserialize(NbtCompound nbt) {
			return new LeechData(nbt.getFloat("Damage"), nbt.getFloat("HealAmount"), nbt.getInt("MaxTicks"));
		}

		NbtCompound serialize() {
			NbtCompound nbt = new NbtCompound();
			nbt.putFloat("Damage", damage());
			nbt.putFloat("HealAmount", healAmount());
			nbt.putInt("MaxTicks", maxTicks());
			return nbt;
		}
	}
}