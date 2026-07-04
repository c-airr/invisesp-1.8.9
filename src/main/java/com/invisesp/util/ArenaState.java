package com.invisesp.util;

import net.legacyfabric.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public final class ArenaState {
	private static final Logger LOGGER = LogManager.getLogger("invisesp");
	private static final Pattern TIME_SUFFIX = Pattern.compile("(\\d+\\s*m\\s*\\d+\\s*s|\\d+\\s*s|\\d{1,2}:\\d{2})\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern BEDWARS_HINT = Pattern.compile("bed\\s*wars|bedwars|lozko|łóżko", Pattern.CASE_INSENSITIVE);
	private static volatile boolean chatArenaActive;
	private static volatile boolean lastCombinedState;

	private ArenaState() {
	}

	public static void register() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			chatArenaActive = false;
			lastCombinedState = false;
		});
	}

	public static void onChatMessage(String plain) {
		if (plain.isEmpty()) {
			return;
		}

		if (isArenaStartMessage(plain)) {
			chatArenaActive = true;
			LOGGER.info("[InvisESP] Arena detected from chat: {}", plain);
		} else if (isArenaEndMessage(plain)) {
			chatArenaActive = false;
			LOGGER.info("[InvisESP] Arena ended from chat: {}", plain);
		}
	}

	public static boolean isInArena(MinecraftClient client) {
		if (client.player == null) {
			return false;
		}

		boolean scoreboardArena = checkScoreboard(client);
		boolean inArena = chatArenaActive || scoreboardArena;
		if (inArena != lastCombinedState) {
			lastCombinedState = inArena;
			LOGGER.info("[InvisESP] Arena state -> {} (chat={}, scoreboard={})", inArena, chatArenaActive, scoreboardArena);
		}
		return inArena;
	}

	private static boolean checkScoreboard(MinecraftClient client) {
		Scoreboard scoreboard = client.world != null ? client.world.getScoreboard() : client.player.getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);
		if (objective == null) {
			return false;
		}

		String title = stripLegacyFormatting(objective.getDisplayName());
		if (BEDWARS_HINT.matcher(title).find()) {
			return true;
		}

		List<ScoreboardPlayerScore> entries = new ArrayList<ScoreboardPlayerScore>(scoreboard.getAllPlayerScores(objective));
		entries.sort(Comparator.comparingInt(ScoreboardPlayerScore::getScore).reversed());

		for (int lineIndex : new int[] {2, 3}) {
			if (lineIndex < entries.size() && hasTimerSuffix(entries.get(lineIndex).getPlayerName())) {
				return true;
			}
		}

		for (ScoreboardPlayerScore entry : entries) {
			String line = stripLegacyFormatting(entry.getPlayerName());
			if (TIME_SUFFIX.matcher(line).find()) {
				return true;
			}
			if (BEDWARS_HINT.matcher(line).find() && TIME_SUFFIX.matcher(line).find()) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasTimerSuffix(String text) {
		return TIME_SUFFIX.matcher(stripLegacyFormatting(text)).find();
	}

	private static boolean isArenaStartMessage(String plain) {
		String lower = plain.toLowerCase();
		return plain.equals("Start!") || lower.contains("witaj na bedwars");
	}

	private static boolean isArenaEndMessage(String plain) {
		String lower = plain.toLowerCase();
		return lower.contains("gra sie skonczyla")
			|| lower.contains("gra się skończyła")
			|| lower.contains("przenoszenie do lobby")
			|| lower.contains("zostales przeniesiony do lobby")
			|| lower.contains("zostałeś przeniesiony do lobby");
	}

	private static String stripLegacyFormatting(String text) {
		return text.replaceAll("§.", "").replaceAll("&.", "");
	}
}
