package com.invisesp.command;

import com.invisesp.config.Config;
import net.legacyfabric.fabric.api.command.v2.CommandRegistrar;
import net.legacyfabric.fabric.api.command.v2.lib.sponge.CommandResult;
import net.legacyfabric.fabric.api.command.v2.lib.sponge.args.GenericArguments;
import net.legacyfabric.fabric.api.command.v2.lib.sponge.spec.CommandSpec;
import net.legacyfabric.fabric.api.permission.v1.PermissibleCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public final class CommandHandler {
	private CommandHandler() {
	}

	public static void register() {
		CommandRegistrar.EVENT.register((manager, dedicated) -> manager.register(buildRoot(), "invisesp"));
	}

	private static CommandSpec buildRoot() {
		return CommandSpec.builder()
			.executor((src, ctx) -> showStatus(src))
			.child(CommandSpec.builder().executor((src, ctx) -> reload(src)).build(), "reload")
			.child(CommandSpec.builder()
				.child(CommandSpec.builder().executor((src, ctx) -> toggleEsp(src, true)).build(), "on")
				.child(CommandSpec.builder().executor((src, ctx) -> toggleEsp(src, false)).build(), "off")
				.child(CommandSpec.builder()
					.arguments(GenericArguments.string(new LiteralText("value")))
					.executor((src, ctx) -> setEspColor(src, ctx.requireOne("value")))
					.build(), "color")
				.child(CommandSpec.builder()
					.arguments(GenericArguments.integer(new LiteralText("range")))
					.executor((src, ctx) -> setEspRange(src, ctx.requireOne("range")))
					.build(), "range")
				.build(), "esp")
			.child(CommandSpec.builder()
				.child(CommandSpec.builder().executor((src, ctx) -> toggleParticle(src, true)).build(), "on")
				.child(CommandSpec.builder().executor((src, ctx) -> toggleParticle(src, false)).build(), "off")
				.child(CommandSpec.builder()
					.arguments(GenericArguments.integer(new LiteralText("seconds")))
					.executor((src, ctx) -> setParticleTimeout(src, ctx.requireOne("seconds")))
					.build(), "timeout")
				.child(CommandSpec.builder()
					.arguments(GenericArguments.string(new LiteralText("color")))
					.executor((src, ctx) -> setParticleColor(src, ctx.requireOne("color")))
					.build(), "color")
				.build(), "particle")
			.child(CommandSpec.builder()
				.child(CommandSpec.builder().executor((src, ctx) -> toggleCapAlert(src, true)).build(), "on")
				.child(CommandSpec.builder().executor((src, ctx) -> toggleCapAlert(src, false)).build(), "off")
				.child(CommandSpec.builder()
					.arguments(GenericArguments.integer(new LiteralText("range")))
					.executor((src, ctx) -> setCapAlertRange(src, ctx.requireOne("range")))
					.build(), "range")
				.build(), "capalert")
			.child(CommandSpec.builder()
				.child(CommandSpec.builder().executor((src, ctx) -> toggleBedEsp(src, true)).build(), "on")
				.child(CommandSpec.builder().executor((src, ctx) -> toggleBedEsp(src, false)).build(), "off")
				.build(), "bedesp")
			.build();
	}

	private static CommandResult showStatus(PermissibleCommandSource src) {
		Config cfg = Config.getInstance();
		send(src, fmt("[InvisESP] Moduly:", Formatting.GRAY));
		send(src, line("ESP", cfg.espEnabled, "kolor: " + cfg.espColorMode, "zasięg: " + cfg.espRange));
		send(src, line("ParticleESP", cfg.particleEspEnabled, "timeout: " + cfg.particleTimeout + "s", "kolor: " + cfg.particleColor));
		send(src, line("Cap Alert", cfg.capAlertEnabled, "zasięg: " + cfg.capAlertRange + "m"));
		send(src, line("Bed ESP / Obsydian", cfg.bedEspEnabled));
		send(src, fmt("Wszystkie moduly dzialaja tylko podczas areny.", Formatting.GRAY));
		return CommandResult.success();
	}

	private static LiteralText line(String name, boolean enabled, String... details) {
		LiteralText text = fmt("  " + (enabled ? "+" : "-") + " " + name + " — " + (enabled ? "ON" : "OFF"), enabled ? Formatting.GREEN : Formatting.RED);
		for (String detail : details) {
			text.append(fmt(" | " + detail, Formatting.GRAY));
		}
		return text;
	}

	private static CommandResult reload(PermissibleCommandSource src) {
		Config.load();
		send(src, fmt("[InvisESP] Config przeladowany.", Formatting.GREEN));
		return CommandResult.success();
	}

	private static CommandResult toggleEsp(PermissibleCommandSource src, boolean on) {
		Config cfg = Config.getInstance();
		cfg.espEnabled = on;
		cfg.save();
		send(src, fmt("[InvisESP] ESP " + (on ? "wlaczony" : "wylaczony"), on ? Formatting.GREEN : Formatting.RED));
		return CommandResult.success();
	}

	private static CommandResult setEspColor(PermissibleCommandSource src, String value) {
		Config cfg = Config.getInstance();
		cfg.espColorMode = value;
		cfg.save();
		send(src, fmt("[InvisESP] ESP kolor -> " + value, Formatting.YELLOW));
		return CommandResult.success();
	}

	private static CommandResult setEspRange(PermissibleCommandSource src, int range) {
		Config cfg = Config.getInstance();
		cfg.espRange = range;
		cfg.save();
		send(src, fmt("[InvisESP] ESP zasieg -> " + range, Formatting.YELLOW));
		return CommandResult.success();
	}

	private static CommandResult toggleParticle(PermissibleCommandSource src, boolean on) {
		Config cfg = Config.getInstance();
		cfg.particleEspEnabled = on;
		cfg.save();
		send(src, fmt("[InvisESP] ParticleESP " + (on ? "wlaczony" : "wylaczony"), on ? Formatting.GREEN : Formatting.RED));
		return CommandResult.success();
	}

	private static CommandResult setParticleTimeout(PermissibleCommandSource src, int sec) {
		Config cfg = Config.getInstance();
		cfg.particleTimeout = sec;
		cfg.save();
		send(src, fmt("[InvisESP] ParticleESP timeout -> " + sec + "s", Formatting.YELLOW));
		return CommandResult.success();
	}

	private static CommandResult setParticleColor(PermissibleCommandSource src, String color) {
		Config cfg = Config.getInstance();
		cfg.particleColor = color;
		cfg.save();
		send(src, fmt("[InvisESP] ParticleESP kolor -> " + color, Formatting.LIGHT_PURPLE));
		return CommandResult.success();
	}

	private static CommandResult toggleCapAlert(PermissibleCommandSource src, boolean on) {
		Config cfg = Config.getInstance();
		cfg.capAlertEnabled = on;
		cfg.save();
		send(src, fmt("[InvisESP] Cap Alert " + (on ? "wlaczony" : "wylaczony"), on ? Formatting.GREEN : Formatting.RED));
		return CommandResult.success();
	}

	private static CommandResult setCapAlertRange(PermissibleCommandSource src, int range) {
		Config cfg = Config.getInstance();
		cfg.capAlertRange = range;
		cfg.save();
		send(src, fmt("[InvisESP] Cap Alert zasieg -> " + range + "m", Formatting.YELLOW));
		return CommandResult.success();
	}

	private static CommandResult toggleBedEsp(PermissibleCommandSource src, boolean on) {
		Config cfg = Config.getInstance();
		cfg.bedEspEnabled = on;
		cfg.save();
		send(src, fmt("[InvisESP] Bed ESP " + (on ? "wlaczone" : "wylaczone"), on ? Formatting.GREEN : Formatting.RED));
		return CommandResult.success();
	}

	private static LiteralText fmt(String message, Formatting formatting) {
		LiteralText text = new LiteralText(message);
		text.getStyle().setFormatting(formatting);
		return text;
	}

	private static void send(PermissibleCommandSource src, LiteralText text) {
		src.sendMessage(text);
	}
}
