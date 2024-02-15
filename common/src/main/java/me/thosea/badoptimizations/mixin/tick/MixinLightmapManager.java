package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.interfaces.LightmapMethods;
import me.thosea.badoptimizations.mixin.accessor.GameRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager implements LightmapMethods {
	@Shadow @Final private MinecraftClient client;

	@Unique private boolean allowUpdate = false;
	@Unique private int lastTick = -1;

	@Unique private boolean previousHasLightning;
	@Unique private double previousGamma;
	@Unique private DimensionEffects previousDimension;
	@Unique private boolean previousInWater;
	@Unique private long previousTime;
	@Unique private boolean previousHasNightVision;

	@Unique private float previousSkyDarkness;
	@Unique private GameRendererAccessor gameRendererAccessor;

	@Override
	public void bo$setGameRendererAccessor(GameRendererAccessor gameRendererAccessor) {
		this.gameRendererAccessor = gameRendererAccessor;
	}

	@Unique
	private boolean isREALLYDirty() {
		boolean result = false;

		boolean hasLightning = client.world.getLightningTicksLeft() != 0;
		if(hasLightning != previousHasLightning) {
			previousHasLightning = hasLightning;
			result = true;
		}

		double gamma = client.options.getGamma().getValue();
		if(previousGamma != gamma) {
			previousGamma = gamma;
			result = true;
		}

		DimensionEffects dimension = client.world.getDimensionEffects();
		if(previousDimension != dimension) {
			previousDimension = dimension;
			result = true;
		}

		boolean inWater = client.player.isSubmergedInWater();
		if(previousInWater != inWater) {
			previousInWater = inWater;
			result = true;
		}

		long time = client.world.getTimeOfDay();
		if(Math.abs(time - previousTime) >= 80) {
			previousTime = time;
			result = true;
		}

		float skyDarkness = gameRendererAccessor.getSkyDarkness();
		if(previousSkyDarkness != skyDarkness) {
			previousSkyDarkness = skyDarkness;
			result = true;
		}

		boolean hasNightVision = client.player.hasStatusEffect(StatusEffects.NIGHT_VISION);
		if(previousHasNightVision != hasNightVision) {
			previousHasNightVision = hasNightVision;
			result = true;
		}

		return result;
	}

	@Inject(method = "enable", at = @At("TAIL"))
	private void onEnable(CallbackInfo ci) {
		if(client.player == null) return;

		int tick = client.player.age;
		if(lastTick != tick) {
			lastTick = tick;

			if(isREALLYDirty()) {
				allowUpdate = true;
				tick();
				allowUpdate = false;
			}
		}
	}

	@Shadow public abstract void tick();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		if(!allowUpdate) {
			ci.cancel();
		}
	}
}
