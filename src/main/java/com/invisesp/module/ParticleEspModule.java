package com.invisesp.module;

import com.invisesp.config.Config;
import com.invisesp.util.ArenaState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleEspModule {
	private static final ParticleEspModule INSTANCE = new ParticleEspModule();
	private final Map<Long, ParticleCluster> clusters = new ConcurrentHashMap<Long, ParticleCluster>();

	public static ParticleEspModule getInstance() {
		return INSTANCE;
	}

	public void onParticleReceived(ParticleS2CPacket packet) {
		if (!Config.getInstance().particleEspEnabled) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (!ArenaState.isInArena(client) || client.world == null || client.player == null) {
			return;
		}

		if (packet.getParameters() != ParticleType.WITCH_SPELL) {
			return;
		}

		double x = packet.getX();
		double y = packet.getY();
		double z = packet.getZ();
		Vec3d pos = new Vec3d(x, y, z);

		for (PlayerEntity player : client.world.playerEntities) {
			if (player != client.player && !player.isInvisible() && player.getBoundingBox().expand(1.0, 1.0, 1.0).contains(pos)) {
				return;
			}
		}

		long key = gridKey(x, y, z);
		ParticleCluster existing = clusters.get(key);
		if (existing == null) {
			clusters.put(key, new ParticleCluster(x, y, z));
		} else {
			existing.update(x, y, z);
		}
	}

	public void tick() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!Config.getInstance().particleEspEnabled || !ArenaState.isInArena(client)) {
			clusters.clear();
			return;
		}

		long now = System.currentTimeMillis();
		long timeout = Config.getInstance().particleTimeout * 1000L;
		clusters.values().removeIf(cluster -> now - cluster.lastUpdate > timeout);
	}

	public Collection<ParticleCluster> getClusters() {
		return clusters.values();
	}

	private static long gridKey(double x, double y, double z) {
		int gx = (int) Math.floor(x * 2.0);
		int gy = (int) Math.floor(y * 2.0);
		int gz = (int) Math.floor(z * 2.0);
		return ((long) gx & 0xFFFFF) << 40 | ((long) gy & 0xFFFFF) << 20 | (long) gz & 0xFFFFF;
	}

	public static class ParticleCluster {
		public double x;
		public double y;
		public double z;
		public long lastUpdate;
		public int count;

		public ParticleCluster(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lastUpdate = System.currentTimeMillis();
			this.count = 1;
		}

		public void update(double nx, double ny, double nz) {
			this.x = (this.x * count + nx) / (count + 1);
			this.y = (this.y * count + ny) / (count + 1);
			this.z = (this.z * count + nz) / (count + 1);
			this.lastUpdate = System.currentTimeMillis();
			count++;
		}
	}
}
