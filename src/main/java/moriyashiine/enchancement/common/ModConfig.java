/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.enchancement.client.util.AllowDuplicateKeybindingsMode;
import moriyashiine.enchancement.common.util.OverhaulMode;

import java.util.Arrays;
import java.util.List;

public class ModConfig extends MidnightConfig {
	@Entry
	public static List<String> disallowedEnchantments = Arrays.asList(
			"enchancement:disarm",
			"enchancement:assimilation",
			"enchancement:perception",
			"enchancement:sticky",
			"enchancement:molten",
			"enchancement:bury",
			"enchancement:grapple",
			"enchancement:strafe",
			"enchancement:brimstone",
			"enchancement:warp",
			"enchancement:chaos",
			"enchancement:phasing");
	@Entry
	public static boolean invertedList = false;

	@Entry
	public static OverhaulMode overhaulEnchantingTable = OverhaulMode.DISABLED;

	@Entry
	public static boolean singleLevelMode = false;
	@Entry
	public static int enchantmentLimit = 10;

	@Entry
	public static boolean disableDurability = false;
	@Entry
	public static boolean disableVelocityChecks = true;
	@Entry
	public static boolean enhanceMobs = true;
	@Entry
	public static boolean freeEnchantedBookMerging = false;
	@Entry
	public static boolean rebalanceConsumables = false;
	@Entry
	public static boolean rebalanceEnchantments = false;
	@Entry
	public static boolean rebalanceEquipment = true;
	@Entry
	public static boolean rebalanceProjectiles = true;
	@Entry
	public static boolean rebalanceStatusEffects = true;
	@Entry
	public static boolean toggleablePassives = true;

	@Entry(min = 0, max = 1)
	public static float weaponEnchantmentCooldownRequirement = 0.7F;

	@Entry(min = 1)
	public static int maxFellTreesBlocks = 1024;
	@Entry(min = 1)
	public static int maxFellTreesHorizontalLength = 7;
	@Entry(min = 1)
	public static int maxMineOreVeinsBlocks = 64;
	@Entry(min = 0)
	public static int coyoteBiteTicks = 3;

	@Entry(category = "client")
	public static boolean enchantmentDescriptions = true;
	@Entry(category = "client")
	public static boolean coloredEnchantmentNames = true;
	@Entry(category = "client")
	public static AllowDuplicateKeybindingsMode allowDuplicateKeybindings = AllowDuplicateKeybindingsMode.VANILLA_AND_ENCHANCEMENT;
	@Entry(category = "client")
	public static boolean invertedBounce = false;
	@Entry(category = "client")
	public static boolean doublePressDirectionBurst = false;
	@Entry(category = "client")
	public static boolean inputlessDirectionBurst = false;

	public static int encode() {
		StringBuilder builder = new StringBuilder();
		for (String value : disallowedEnchantments) {
			builder.append(value);
		}
		String encoding = builder.toString() +
				invertedList +
				overhaulEnchantingTable +
				singleLevelMode + enchantmentLimit +
				disableDurability + disableVelocityChecks +
				enhanceMobs +
				freeEnchantedBookMerging +
				rebalanceConsumables + rebalanceEnchantments + rebalanceEquipment + rebalanceProjectiles + rebalanceStatusEffects +
				toggleablePassives +
				weaponEnchantmentCooldownRequirement +
				maxFellTreesBlocks + maxFellTreesHorizontalLength + maxMineOreVeinsBlocks +
				coyoteBiteTicks;
		return encoding.hashCode();
	}

	static {
		MidnightConfig.init(Enchancement.MOD_ID, ModConfig.class);
	}
}
