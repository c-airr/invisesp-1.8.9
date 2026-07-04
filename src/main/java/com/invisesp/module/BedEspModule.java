package com.invisesp.module;

import com.invisesp.config.Config;
import com.invisesp.util.ArenaState;
import com.invisesp.util.SubtitleHelper;
import com.invisesp.util.TeamHelper;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BedEspModule {
	private static final BedEspModule INSTANCE = new BedEspModule();
	private final Map<BlockPos, BedRecord> beds = new ConcurrentHashMap<BlockPos, BedRecord>();
	private int tickCounter;

	public static BedEspModule getInstance() {
		return INSTANCE;
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::tick);
	}

	private void tick(MinecraftClient client) {
		if (!Config.getInstance().bedEspEnabled || !ArenaState.isInArena(client)) {
			beds.clear();
			return;
		}

		if (client.world == null || client.player == null) {
			return;
		}

		tickCounter++;
		if (tickCounter % 20 != 0) {
			return;
		}

		scanForBeds(client);
		updateBeds(client);
	}

	private void scanForBeds(MinecraftClient client) {
		BlockPos center = new BlockPos(client.player);
		int radius = 48;
		beds.keySet().removeIf(pos -> pos.getSquaredDistance(center) > (radius + 16) * (radius + 16));

		for (int x = -radius; x <= radius; x += 4) {
			for (int y = -24; y <= 24; y += 4) {
				for (int z = -radius; z <= radius; z += 4) {
					BlockPos sample = center.add(x, y, z);
					if (!isChunkLoaded(client, sample)) {
						continue;
					}

					for (int dx = 0; dx < 4; dx++) {
						for (int dy = 0; dy < 4; dy++) {
							for (int dz = 0; dz < 4; dz++) {
								BlockPos checkPos = sample.add(dx, dy, dz);
								BlockState state = client.world.getBlockState(checkPos);
								if (state.getBlock() instanceof BedBlock) {
									beds.putIfAbsent(new BlockPos(checkPos), new BedRecord(new BlockPos(checkPos)));
								}
							}
						}
					}
				}
			}
		}
	}

	private void updateBeds(MinecraftClient client) {
		for (BedRecord record : beds.values()) {
			EnumMap<TeamHelper, Integer> teamCounts = TeamHelper.newCountMap();
			Set<BlockPos> currentObsidian = new HashSet<BlockPos>();

			for (int dx = -3; dx <= 3; dx++) {
				for (int dz = -3; dz <= 3; dz++) {
					for (int dy = 0; dy <= 4; dy++) {
						BlockPos checkPos = record.pos.add(dx, dy, dz);
						if (checkPos.equals(record.pos) || !isChunkLoaded(client, checkPos)) {
							continue;
						}

						BlockState state = client.world.getBlockState(checkPos);
						Block block = state.getBlock();
						if (block.getMaterial().isReplaceable() || block instanceof BedBlock) {
							continue;
						}

						String name = blockName(block);
						TeamHelper team = TeamHelper.fromBlockName(name);
						if (team != null) {
							Integer count = teamCounts.get(team);
							teamCounts.put(team, count == null ? 1 : count + 1);
						}

						if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
							currentObsidian.add(new BlockPos(checkPos));
						}
					}
				}
			}

			TeamHelper detectedTeam = TeamHelper.detectFromSurroundings(teamCounts);
			if (detectedTeam != null) {
				record.team = detectedTeam;
			}

			if (!record.baselineCaptured) {
				record.baselineObsidian.addAll(currentObsidian);
				record.baselineCaptured = true;
			}

			boolean hadObsidian = record.hasPlayerObsidian;
			record.hasPlayerObsidian = false;
			for (BlockPos obsidianPos : currentObsidian) {
				if (!record.baselineObsidian.contains(obsidianPos)) {
					record.hasPlayerObsidian = true;
					break;
				}
			}

			if (record.hasPlayerObsidian && !hadObsidian && record.team != null && !record.obsidianAlertShown) {
				SubtitleHelper.showObsidianBed(record.team);
				record.obsidianAlertShown = true;
			}
		}
	}

	public List<TeamHelper> getObsidianTeams() {
		Set<TeamHelper> teams = new HashSet<TeamHelper>();
		for (BedRecord record : beds.values()) {
			if (record.hasPlayerObsidian && record.team != null) {
				teams.add(record.team);
			}
		}

		List<TeamHelper> sorted = new ArrayList<TeamHelper>(teams);
		sorted.sort(Comparator.comparing(TeamHelper::getShortName));
		return sorted;
	}

	public Collection<BedRecord> getBeds() {
		return beds.values();
	}

	private static boolean isChunkLoaded(MinecraftClient client, BlockPos pos) {
		return client.world.isLoaded(pos, false);
	}

	private static String blockName(Block block) {
		return block.getTranslationKey().replace("tile.", "").replace("block.", "").toLowerCase();
	}

	public static final class BedRecord {
		public final BlockPos pos;
		public TeamHelper team;
		public final Set<BlockPos> baselineObsidian = new HashSet<BlockPos>();
		public boolean baselineCaptured;
		public boolean hasPlayerObsidian;
		public boolean obsidianAlertShown;

		public BedRecord(BlockPos pos) {
			this.pos = pos;
		}
	}
}
