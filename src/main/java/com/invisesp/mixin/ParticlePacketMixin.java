package com.invisesp.mixin;

import com.invisesp.module.ParticleEspModule;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ParticlePacketMixin {
	@Inject(method = "onParticle", at = @At("HEAD"))
	private void invisesp$onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		ParticleEspModule.getInstance().onParticleReceived(packet);
	}
}
