package com.invisesp.module;

import com.invisesp.config.Config;
import com.invisesp.util.ArenaState;
import com.invisesp.util.LeatherCapHelper;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public final class CapAlertModule {
	private CapAlertModule() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(CapAlertModule::tick);
	}

	private static void tick(MinecraftClient client) {
		Config cfg = Config.getInstance();
		if (!cfg.capAlertEnabled || !ArenaState.isInArena(client) || client.world == null || client.player == null) {
			return;
		}

		Integer myCapColor = LeatherCapHelper.getLeatherHelmetColor(client.player);
		if (myCapColor == null) {
			return;
		}

		double maxDistSq = (double) cfg.capAlertRange * cfg.capAlertRange;
		int closestBlocks = Integer.MAX_VALUE;

		for (PlayerEntity other : client.world.playerEntities) {
			if (other == client.player || other.isSpectator()) {
				continue;
			}
			if (client.getNetworkHandler().getPlayerListEntry(other.getUuid()) == null) {
				continue;
			}
			if (client.player.squaredDistanceTo(other) > maxDistSq) {
				continue;
			}

			Integer otherCapColor = LeatherCapHelper.getLeatherHelmetColor(other);
			if (otherCapColor != null && !otherCapColor.equals(myCapColor)) {
				int blocks = (int) Math.floor(client.player.distanceTo(other));
				if (blocks < closestBlocks) {
					closestBlocks = blocks;
				}
			}
		}

		if (closestBlocks != Integer.MAX_VALUE) {
			client.inGameHud.setOverlayMessage("Uważaj! (" + closestBlocks + "m)", true);
		}
	}
}
