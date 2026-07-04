package com.invisesp.module;

import com.invisesp.util.ArenaState;
import com.invisesp.util.TeamHelper;
import net.legacyfabric.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;

public final class LeftHudModule {
	private LeftHudModule() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register(LeftHudModule::render);
	}

	private static void render(MinecraftClient client, float tickDelta) {
		if (!ArenaState.isInArena(client) || client.player == null) {
			return;
		}

		List<InvisAlertModule.InvisibleEntry> invisible = InvisAlertModule.getInstance().getVisibleEntries(client);
		List<TeamHelper> obsidianTeams = BedEspModule.getInstance().getObsidianTeams();
		if (invisible.isEmpty() && obsidianTeams.isEmpty()) {
			return;
		}

		TextRenderer textRenderer = client.textRenderer;
		Window window = new Window(client);
		int x = 10;
		int y = window.getHeight() / 4;

		if (!invisible.isEmpty()) {
			textRenderer.drawWithShadow("Niewidzialni gracze:", x, y, 0xFFFFFF);
			y += 12;

			for (InvisAlertModule.InvisibleEntry entry : invisible) {
				String prefix = "- " + entry.name + " (" + entry.distance + "m)";
				int color = entry.team != null ? entry.team.getChatColor() : (0xFF000000 | entry.color);
				textRenderer.drawWithShadow(prefix, x, y, color);
				y += 10;
			}
			y += 4;
		}

		if (!obsidianTeams.isEmpty()) {
			textRenderer.drawWithShadow("Obsydianowe lozka:", x, y, 0xFFFFFF);
			y += 12;

			for (TeamHelper team : obsidianTeams) {
				textRenderer.drawWithShadow("- " + team.getShortName(), x, y, team.getChatColor());
				y += 10;
			}
		}
	}
}
