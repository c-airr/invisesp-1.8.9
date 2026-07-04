package com.invisesp;

import com.invisesp.command.CommandHandler;
import com.invisesp.config.Config;
import com.invisesp.module.AutoToolModule;
import com.invisesp.module.BedEspModule;
import com.invisesp.module.CapAlertModule;
import com.invisesp.module.InvisAlertModule;
import com.invisesp.module.LeftHudModule;
import com.invisesp.module.ParticleEspModule;
import com.invisesp.render.RenderHelper;
import com.invisesp.util.ArenaState;
import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvisEspClient implements ClientModInitializer {
	public static final String MOD_ID = "invisesp";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("[InvisESP] Initializing for 1.8.9...");
		Config.getInstance();
		ArenaState.register();
		ClientTickEvents.END_CLIENT_TICK.register(client -> ParticleEspModule.getInstance().tick());
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.interactionManager == null || !client.interactionManager.isBreakingBlock()) {
				AutoToolModule.reset();
			}
		});
		RenderHelper.register();
		CapAlertModule.register();
		BedEspModule.register();
		InvisAlertModule.register();
		LeftHudModule.register();
		CommandHandler.register();
		LOGGER.info("[InvisESP] Loaded successfully.");
	}
}
