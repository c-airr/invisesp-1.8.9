package com.invisesp.render;

import com.invisesp.config.Config;
import com.invisesp.module.EspModule;
import com.invisesp.module.ParticleEspModule;
import com.invisesp.util.ArenaState;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public final class RenderHelper {
	private RenderHelper() {
	}

	public static void register() {
	}

	public static void renderWorldOverlays(float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null || !ArenaState.isInArena(client)) {
			return;
		}

		double camX = client.player.prevX + (client.player.x - client.player.prevX) * tickDelta;
		double camY = client.player.prevY + (client.player.y - client.player.prevY) * tickDelta;
		double camZ = client.player.prevZ + (client.player.z - client.player.prevZ) * tickDelta;

		if (Config.getInstance().espEnabled) {
			for (PlayerEntity player : client.world.playerEntities) {
				if (EspModule.getInstance().shouldGlow(player)) {
					drawEntityBox(player, camX, camY, camZ, tickDelta, EspModule.getInstance().getGlowColor(player), 0.9F);
				}
			}
		}

		if (Config.getInstance().particleEspEnabled) {
			int rgb = Config.getInstance().getParticleColorRgb();
			for (ParticleEspModule.ParticleCluster cluster : ParticleEspModule.getInstance().getClusters()) {
				drawBox(cluster.x - camX - 0.3, cluster.y - camY - 1.0, cluster.z - camZ - 0.3,
					cluster.x - camX + 0.3, cluster.y - camY + 0.8, cluster.z - camZ + 0.3, rgb, 0.8F);
			}
		}
	}

	private static void drawEntityBox(Entity entity, double camX, double camY, double camZ, float tickDelta, int rgb, float alpha) {
		double x = entity.prevX + (entity.x - entity.prevX) * tickDelta;
		double y = entity.prevY + (entity.y - entity.prevY) * tickDelta;
		double z = entity.prevZ + (entity.z - entity.prevZ) * tickDelta;
		Box box = entity.getBoundingBox().offset(-entity.x, -entity.y, -entity.z).offset(x, y, z);
		drawBox(box.minX - camX, box.minY - camY, box.minZ - camZ, box.maxX - camX, box.maxY - camY, box.maxZ - camZ, rgb, alpha);
	}

	private static void drawBox(double x1, double y1, double z1, double x2, double y2, double z2, int rgb, float alpha) {
		float r = ((rgb >> 16) & 0xFF) / 255.0F;
		float g = ((rgb >> 8) & 0xFF) / 255.0F;
		float b = (rgb & 0xFF) / 255.0F;

		GlStateManager.pushMatrix();
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(r, g, b, alpha);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(3, VertexFormats.POSITION);
		line(buffer, x1, y1, z1, x2, y1, z1);
		line(buffer, x2, y1, z1, x2, y1, z2);
		line(buffer, x2, y1, z2, x1, y1, z2);
		line(buffer, x1, y1, z2, x1, y1, z1);
		line(buffer, x1, y2, z1, x2, y2, z1);
		line(buffer, x2, y2, z1, x2, y2, z2);
		line(buffer, x2, y2, z2, x1, y2, z2);
		line(buffer, x1, y2, z2, x1, y2, z1);
		line(buffer, x1, y1, z1, x1, y2, z1);
		line(buffer, x2, y1, z1, x2, y2, z1);
		line(buffer, x2, y1, z2, x2, y2, z2);
		line(buffer, x1, y1, z2, x1, y2, z2);
		Tessellator.getInstance().draw();

		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private static void line(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2) {
		buffer.vertex(x1, y1, z1).next();
		buffer.vertex(x2, y2, z2).next();
	}
}
