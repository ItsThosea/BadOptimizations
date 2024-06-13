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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager {
	@Shadow @Final private MinecraftClient client;
	@Unique private CommonColorFactors commonFactors;

	@Unique private boolean allowUpdate = false;

	@Unique private double lastGamma;
	@Unique private DimensionEffects lastDimension;
	@Unique private boolean lastNightVision;
	@Unique private boolean lastConduitPower;

	@Unique private float previousSkyDarkness;
	@Unique private GameRendererAccessor gameRendererAccessor;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(GameRenderer renderer, MinecraftClient client, CallbackInfo ci) {
		this.gameRendererAccessor = (GameRendererAccessor) renderer;
		this.commonFactors = CommonColorFactors.LIGHTMAP;
	}

	@Unique
	private boolean isDirty() {
		boolean result = false;

		DimensionEffects dimension = client.world.getDimensionEffects();
		if(lastDimension != dimension) {
			lastDimension = dimension;
			result = true;
		}
		float skyDarkness = gameRendererAccessor.bo$getSkyDarkness();
		if(previousSkyDarkness != skyDarkness) {
			previousSkyDarkness = skyDarkness;
			result = true;
		}
		double gamma = client.options.getGamma().getValue();
		if(lastGamma != gamma) { // jamma celestial??
			lastGamma = gamma;
			result = true;
		}

		PlayerAccessor accessor = (PlayerAccessor) client.player;
		if(client.player.isSubmergedInWater() && accessor.bo$underwaterVisibilityTicks() < 600) {
			result = true;
		}

		StatusEffectInstance nightVision = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
		boolean hasNightVision = nightVision != null;
		if(lastNightVision != hasNightVision) {
			lastNightVision = hasNightVision;
			result = true;
		} else if(nightVision != null && nightVision.getDuration() < 200) {
			result = true; // flicker effect
		}

		boolean conduitPower = client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER);
		if(lastConduitPower != conduitPower) {
			lastConduitPower = conduitPower;
			result = true;
		}

		if(commonFactors.getTimeDelta() >= Config.lightmap_time_change_needed_for_update) {
			result = true;
		}

		return result;
	}

	@Inject(method = "enable", at = @At("TAIL"))
	private void onEnable(CallbackInfo ci) {
		if(client.player == null) return;

		CommonColorFactors.tick(client.getTickDelta());

		if(commonFactors.didTickChange() && (commonFactors.isDirty()) | this.isDirty()) {
			allowUpdate = true;
			tick();
			allowUpdate = false;
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
