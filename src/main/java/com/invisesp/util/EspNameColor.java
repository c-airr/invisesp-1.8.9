package com.invisesp.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class EspNameColor {
	private EspNameColor() {
	}

	public static int extract(net.minecraft.entity.Entity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return 0xFFFFFF;
		}

		Text name = ((PlayerEntity) entity).getName();
		if (name == null) {
			return 0xFFFFFF;
		}

		Formatting formatting = name.getStyle().getColor();
		if (formatting != null && formatting.isColor()) {
			return formattingToRgb(formatting);
		}

		for (Text sibling : name.getSiblings()) {
			formatting = sibling.getStyle().getColor();
			if (formatting != null && formatting.isColor()) {
				return formattingToRgb(formatting);
			}
		}

		return 0xFFFFFF;
	}

	private static int formattingToRgb(Formatting formatting) {
		switch (formatting) {
			case BLACK: return 0x000000;
			case DARK_BLUE: return 0x0000AA;
			case DARK_GREEN: return 0x00AA00;
			case DARK_AQUA: return 0x00AAAA;
			case DARK_RED: return 0xAA0000;
			case DARK_PURPLE: return 0xAA00AA;
			case GOLD: return 0xFFAA00;
			case GRAY: return 0xAAAAAA;
			case DARK_GRAY: return 0x555555;
			case BLUE: return 0x5555FF;
			case GREEN: return 0x55FF55;
			case AQUA: return 0x55FFFF;
			case RED: return 0xFF5555;
			case LIGHT_PURPLE: return 0xFF55FF;
			case YELLOW: return 0xFFFF55;
			case WHITE: return 0xFFFFFF;
			default: return 0xFFFFFF;
		}
	}
}
