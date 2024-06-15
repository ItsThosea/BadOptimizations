package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.mixin.accessor.GameRendererAccessor;
import me.thosea.badoptimizations.mixin.accessor.PlayerAccessor;
import me.thosea.badoptimizations.other.CommonColorFactors;
import me.thosea.badoptimizations.other.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager {
	@Shadow @Final private MinecraftClient client;

	private CommonColorFactors bo$commonFactors;
	private boolean bo$allowUpdate = false;

	private double bo$lastGamma;
	private DimensionEffects bo$lastDimension;
	private boolean bo$lastNightVision;
	private boolean bo$lastConduitPower;

	private float bo$previousSkyDarkness;
	private GameRendererAccessor bo$gameRendererAccessor;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(GameRenderer renderer, MinecraftClient client, CallbackInfo ci) {
		this.bo$gameRendererAccessor = (GameRendererAccessor) renderer;
		this.bo$commonFactors = CommonColorFactors.LIGHTMAP;
	}

	private boolean bo$isDirty() {
		boolean result = false;

		DimensionEffects dimension = client.world.getDimensionEffects();
		if(bo$lastDimension != dimension) {
			bo$lastDimension = dimension;
			result = true;
		}
		float skyDarkness = bo$gameRendererAccessor.bo$getSkyDarkness();
		if(bo$previousSkyDarkness != skyDarkness) {
			bo$previousSkyDarkness = skyDarkness;
			result = true;
		}
		double gamma = client.options.getGamma().getValue();
		if(bo$lastGamma != gamma) { // jamma celestial??
			bo$lastGamma = gamma;
			result = true;
		}

		PlayerAccessor accessor = (PlayerAccessor) client.player;
		if(client.player.isSubmergedInWater() && accessor.bo$underwaterVisibilityTicks() < 600) {
			result = true;
		}

		StatusEffectInstance nightVision = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
		boolean hasNightVision = nightVision != null;
		if(bo$lastNightVision != hasNightVision) {
			bo$lastNightVision = hasNightVision;
			result = true;
		} else if(nightVision != null && nightVision.getDuration() < 200) {
			result = true; // flicker effect
		}

		boolean conduitPower = client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER);
		if(bo$lastConduitPower != conduitPower) {
			bo$lastConduitPower = conduitPower;
			result = true;
		}

		if(bo$commonFactors.getTimeDelta() >= Config.lightmap_time_change_needed_for_update) {
			result = true;
		}

		return result;
	}

	@Inject(method = "enable", at = @At("TAIL"))
	private void onEnable(CallbackInfo ci) {
		if(client.player == null) return;

		CommonColorFactors.tick(client.getTickDelta());

		if(bo$commonFactors.didTickChange() && (bo$commonFactors.isDirty()) | this.bo$isDirty()) {
			bo$commonFactors.updateLastTime();

			bo$allowUpdate = true;
			tick();
			bo$allowUpdate = false;
		}
	}

	@Shadow public abstract void tick();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		if(!bo$allowUpdate) {
			ci.cancel();
		}
	}
}
