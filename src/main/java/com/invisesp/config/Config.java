package com.invisesp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("invisesp.json");
	private static Config INSTANCE;

	public boolean espEnabled = true;
	public String espColorMode = "nick";
	public int espRange = 64;
	public boolean particleEspEnabled = true;
	public int particleTimeout = 5;
	public String particleColor = "#FF00FF";
	public boolean capAlertEnabled = true;
	public int capAlertRange = 64;
	public boolean bedEspEnabled = true;

	public static Config getInstance() {
		if (INSTANCE == null) {
			INSTANCE = load();
		}
		return INSTANCE;
	}

	public static Config load() {
		if (Files.exists(CONFIG_PATH)) {
			try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
				INSTANCE = GSON.fromJson(reader, Config.class);
				if (INSTANCE == null) {
					INSTANCE = new Config();
				}
				return INSTANCE;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		INSTANCE = new Config();
		INSTANCE.save();
		return INSTANCE;
	}

	public void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(this, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getParticleColorRgb() {
		return parseHexColor(particleColor);
	}

	public int getEspFixedColorRgb() {
		return espColorMode.startsWith("#") ? parseHexColor(espColorMode) : 0xFFFFFF;
	}

	public boolean isNickColorMode() {
		return "nick".equalsIgnoreCase(espColorMode);
	}

	public static int parseHexColor(String hex) {
		try {
			return Integer.parseInt(hex.replace("#", ""), 16);
		} catch (NumberFormatException e) {
			return 0xFFFFFF;
		}
	}
}
