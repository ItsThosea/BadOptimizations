package me.thosea.badoptimizations.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
	@Shadow @Final private Map<ParticleTextureSheet, Queue<Particle>> particles;

	@Inject(method = "renderParticles", at = @At("HEAD"), cancellable = true)
	private void onRender(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
		if(particles.isEmpty()) {
			ci.cancel();
		}
	}
}