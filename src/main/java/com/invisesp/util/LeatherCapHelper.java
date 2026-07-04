package com.invisesp.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public final class LeatherCapHelper {
	private static final int DEFAULT_LEATHER_COLOR = 0xA06540;

	private LeatherCapHelper() {
	}

	public static Integer getLeatherHelmetColor(PlayerEntity player) {
		ItemStack helmet = player.getArmorSlot(3);
		if (helmet == null || helmet.getItem() != Items.LEATHER_HELMET) {
			return null;
		}

		NbtCompound nbt = helmet.getNbt();
		if (nbt != null && nbt.contains("display")) {
			NbtCompound display = nbt.getCompound("display");
			if (display.contains("color")) {
				return display.getInt("color");
			}
		}

		return DEFAULT_LEATHER_COLOR;
	}
}
