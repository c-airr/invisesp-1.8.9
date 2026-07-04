package com.invisesp.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public final class SubtitleHelper {
	private static long invisSubtitleCooldownUntil;

	private SubtitleHelper() {
	}

	public static void showInvisDrink(PlayerEntity player) {
		long now = System.currentTimeMillis();
		if (now < invisSubtitleCooldownUntil) {
			return;
		}

		invisSubtitleCooldownUntil = now + 40000L;
		TeamHelper team = TeamHelper.fromRgb(EspNameColor.extract(player));
		String playerName = player.getName().asUnformattedString();
		String subtitle = "Gracz " + playerName + " wypił Niewidke!";
		show(subtitle);
	}

	public static void showObsidianBed(TeamHelper team) {
		show("Lożko " + team.getGenitiveName() + " jest obsydianowane!");
	}

	private static void show(String subtitle) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.inGameHud != null) {
			client.inGameHud.setTitles(subtitle, "", 0, 60, 20);
		}
	}
}
