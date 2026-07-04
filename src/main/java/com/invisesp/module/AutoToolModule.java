package com.invisesp.module;

import com.invisesp.util.ArenaState;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public final class AutoToolModule {
	private static BlockPos lastSwitchedPos;

	private AutoToolModule() {
	}

	public static void onBlockBreakStart(BlockPos pos) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!ArenaState.isInArena(client) || client.world == null || client.player == null) {
			return;
		}

		if (pos.equals(lastSwitchedPos)) {
			return;
		}

		Block block = client.world.getBlockState(pos).getBlock();
		int slot = resolveToolSlot(client, block);
		if (slot >= 0 && slot != client.player.inventory.selectedSlot) {
			client.player.inventory.selectedSlot = slot;
			client.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(slot));
			client.world.playSound(client.player.x, client.player.y, client.player.z, "random.click", 0.5F, 1.2F);
			lastSwitchedPos = new BlockPos(pos);
		}
	}

	public static void reset() {
		lastSwitchedPos = null;
	}

	private static int resolveToolSlot(MinecraftClient client, Block block) {
		String blockId = blockName(block);
		if (blockId.contains("wool")) {
			return findHotbarSlot(client, Items.SHEARS);
		}
		if (blockId.contains("end_stone") || blockId.contains("clay") || blockId.contains("hardened_clay")) {
			return findPickaxeSlot(client);
		}
		if (blockId.contains("ladder") || blockId.contains("log") || blockId.contains("wood")) {
			return findAxeSlot(client);
		}
		return -1;
	}

	private static int findHotbarSlot(MinecraftClient client, Item item) {
		for (int slot = 0; slot < 9; slot++) {
			ItemStack stack = client.player.inventory.main[slot];
			if (stack != null && stack.getItem() == item) {
				return slot;
			}
		}
		return -1;
	}

	private static int findPickaxeSlot(MinecraftClient client) {
		for (int slot = 0; slot < 9; slot++) {
			ItemStack stack = client.player.inventory.main[slot];
			if (stack != null && stack.getItem().getTranslationKey().contains("pickaxe")) {
				return slot;
			}
		}
		return -1;
	}

	private static int findAxeSlot(MinecraftClient client) {
		for (int slot = 0; slot < 9; slot++) {
			ItemStack stack = client.player.inventory.main[slot];
			if (stack != null && stack.getItem().getTranslationKey().contains("axe")) {
				return slot;
			}
		}
		return -1;
	}

	private static String blockName(Block block) {
		return block.getTranslationKey().replace("tile.", "").replace("block.", "").toLowerCase();
	}
}
