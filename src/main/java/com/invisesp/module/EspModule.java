package com.invisesp.module;

import com.invisesp.config.Config;
import com.invisesp.util.ArenaState;
import com.invisesp.util.EspNameColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class EspModule {
	private static final EspModule INSTANCE = new EspModule();

	public static EspModule getInstance() {
		return INSTANCE;
	}

	public boolean shouldGlow(Entity entity) {
		if (!Config.getInstance().espEnabled) {
			return false;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (!ArenaState.isInArena(client) || client.player == null || client.world == null) {
			return false;
		}

		if (!(entity instanceof PlayerEntity)) {
			return false;
		}

		if (entity == client.player) {
			return false;
		}

		double range = Config.getInstance().espRange;
		return client.player.squaredDistanceTo(entity) <= range * range;
	}

	public int getGlowColor(Entity entity) {
		if (entity.isInvisible()) {
			return System.currentTimeMillis() % 300L < 150L ? 0xFF0000 : 0xFFFF00;
		}

		Config cfg = Config.getInstance();
		return cfg.isNickColorMode() ? EspNameColor.extract(entity) : cfg.getEspFixedColorRgb();
	}
}
