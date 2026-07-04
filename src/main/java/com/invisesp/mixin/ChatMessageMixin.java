package com.invisesp.mixin;

import com.invisesp.util.ArenaState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMessageMixin {
	@Inject(method = "onChatMessage", at = @At("HEAD"))
	private void invisesp$onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
		ArenaState.onChatMessage(packet.getMessage().asUnformattedString());
	}
}
