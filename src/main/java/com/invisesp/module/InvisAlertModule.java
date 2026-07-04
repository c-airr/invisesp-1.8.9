package com.invisesp.module;

import com.invisesp.util.ArenaState;
import com.invisesp.util.EspNameColor;
import com.invisesp.util.SubtitleHelper;
import com.invisesp.util.TeamHelper;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class InvisAlertModule {
	private static final InvisAlertModule INSTANCE = new InvisAlertModule();
	private final Set<UUID> currentlyInvisible = new HashSet<UUID>();
	private final Set<UUID> subtitleShown = new HashSet<UUID>();

	public static InvisAlertModule getInstance() {
		return INSTANCE;
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::tick);
	}

	private void tick(MinecraftClient client) {
		if (!ArenaState.isInArena(client) || client.world == null || client.player == null) {
			currentlyInvisible.clear();
			subtitleShown.clear();
			return;
		}

		Set<UUID> seenThisTick = new HashSet<UUID>();

		for (PlayerEntity player : client.world.playerEntities) {
			if (player == client.player || player.isSpectator()) {
				continue;
			}
			if (client.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null) {
				continue;
			}

			if (!player.isInvisible()) {
				subtitleShown.remove(player.getUuid());
				continue;
			}

			seenThisTick.add(player.getUuid());
			if (!currentlyInvisible.contains(player.getUuid()) && !subtitleShown.contains(player.getUuid())) {
				SubtitleHelper.showInvisDrink(player);
				subtitleShown.add(player.getUuid());
			}
		}

		currentlyInvisible.clear();
		currentlyInvisible.addAll(seenThisTick);
	}

	public List<InvisibleEntry> getVisibleEntries(MinecraftClient client) {
		List<InvisibleEntry> entries = new ArrayList<InvisibleEntry>();
		if (!ArenaState.isInArena(client) || client.world == null || client.player == null) {
			return entries;
		}

		for (PlayerEntity player : client.world.playerEntities) {
			if (player == client.player || player.isSpectator()) {
				continue;
			}
			if (client.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null) {
				continue;
			}
			if (!player.isInvisible()) {
				continue;
			}

			int distance = (int) Math.floor(client.player.distanceTo(player));
			int color = EspNameColor.extract(player);
			TeamHelper team = TeamHelper.fromRgb(color);
			entries.add(new InvisibleEntry(player.getName().asUnformattedString(), color, team, distance));
		}

		entries.sort(Comparator.comparingInt(entry -> entry.distance));
		return entries;
	}

	public static final class InvisibleEntry {
		public final String name;
		public final int color;
		public final TeamHelper team;
		public final int distance;

		public InvisibleEntry(String name, int color, TeamHelper team, int distance) {
			this.name = name;
			this.color = color;
			this.team = team;
			this.distance = distance;
		}
	}
}
