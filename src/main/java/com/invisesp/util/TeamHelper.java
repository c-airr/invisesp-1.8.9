package com.invisesp.util;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public enum TeamHelper {
	RED("Czerwonych", "Czerwona", Formatting.RED, 0xFF5555, new String[] {"red"}),
	BLUE("Niebieskich", "Niebieska", Formatting.BLUE, 0x5555FF, new String[] {"blue", "light_blue"}),
	GREEN("Zielonych", "Zielona", Formatting.GREEN, 0x55FF55, new String[] {"green", "lime"}),
	YELLOW("Zoltych", "Zolta", Formatting.YELLOW, 0xFFFF55, new String[] {"yellow"}),
	CYAN("Morskich", "Morska", Formatting.AQUA, 0x55FFFF, new String[] {"cyan", "aqua"}),
	ORANGE("Zlotych", "Zlota", Formatting.GOLD, 0xFFAA00, new String[] {"orange"}),
	PINK("Rozowych", "Rozowa", Formatting.LIGHT_PURPLE, 0xFF55FF, new String[] {"pink", "magenta"}),
	WHITE("Bialych", "Biala", Formatting.WHITE, 0xFFFFFF, new String[] {"white"});

	private final String genitiveName;
	private final String shortName;
	private final Formatting formatting;
	private final int rgb;
	private final String[] blockTokens;

	TeamHelper(String genitiveName, String shortName, Formatting formatting, int rgb, String[] blockTokens) {
		this.genitiveName = genitiveName;
		this.shortName = shortName;
		this.formatting = formatting;
		this.rgb = rgb;
		this.blockTokens = blockTokens;
	}

	public String getGenitiveName() {
		return genitiveName;
	}

	public String getShortName() {
		return shortName;
	}

	public int getChatColor() {
		return 0xFF000000 | rgb;
	}

	public LiteralText coloredText(String text) {
		LiteralText literal = new LiteralText(text);
		literal.getStyle().setFormatting(formatting);
		return literal;
	}

	public static TeamHelper fromBlockName(String blockName) {
		String name = blockName.toLowerCase();
		if (!name.contains("wool") && !name.contains("clay") && !name.contains("hardened_clay")) {
			return null;
		}

		for (TeamHelper team : values()) {
			for (String token : team.blockTokens) {
				if (name.contains(token + "_wool") || name.contains(token + "_clay") || name.contains(token + "_stained_hardened_clay")) {
					return team;
				}
			}
		}
		return null;
	}

	public static TeamHelper fromRgb(int rgb) {
		int masked = rgb & 0xFFFFFF;
		for (TeamHelper team : values()) {
			if (team.rgb == masked) {
				return team;
			}
		}
		return null;
	}

	public static TeamHelper detectFromSurroundings(java.util.Map<TeamHelper, Integer> counts) {
		TeamHelper best = null;
		int bestCount = 0;
		for (java.util.Map.Entry<TeamHelper, Integer> entry : counts.entrySet()) {
			if (entry.getValue() > bestCount) {
				bestCount = entry.getValue();
				best = entry.getKey();
			}
		}
		return bestCount > 0 ? best : null;
	}

	public static java.util.EnumMap<TeamHelper, Integer> newCountMap() {
		return new java.util.EnumMap<TeamHelper, Integer>(TeamHelper.class);
	}
}
