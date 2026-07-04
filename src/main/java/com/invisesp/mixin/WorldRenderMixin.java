package com.invisesp.mixin;

import com.invisesp.render.RenderHelper;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {
	@Inject(method = "renderEntities", at = @At("RETURN"))
	private void invisesp$afterEntities(Entity entity, CameraView cameraView, float tickDelta, CallbackInfo ci) {
		RenderHelper.renderWorldOverlays(tickDelta);
	}
}
