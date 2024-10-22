package me.thosea.badoptimizations.other;

import me.thosea.badoptimizations.mixin.tick.MixinClientWorld;
import me.thosea.badoptimizations.mixin.tick.MixinLightmapManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

import java.util.Objects;

/**
 * Holds common factors for {@link MixinClientWorld} & {@link MixinLightmapManager}
 */
public final class CommonColorFactors {
	private CommonColorFactors() {}

	private static final MinecraftClient CLIENT = Objects.requireNonNull(MinecraftClient.getInstance(), "loaded too early");

	public static final CommonColorFactors SKY_COLOR = new CommonColorFactors();
	public static final CommonColorFactors LIGHTMAP = new CommonColorFactors();

	private static int lastUpdateTick = -1;

	public static float lastRainGradient;
	public static float lastThunderGradient;
	public static int lastLightningTicks;

	public static float rainGradientMultiplier;
	public static float thunderGradientMultiplier;

	private boolean thisInstanceDirty;
	private boolean didTickChange;
	private long lastTime;

	public static void tick() {
		int tick = CLIENT.player.age;
		if(lastUpdateTick == tick) return;
		lastUpdateTick = tick;

		ClientWorld world = CLIENT.world;
		float tickDelta = CLIENT.getRenderTickCounter().getTickDelta(false);
		boolean result = false;

		float rainGradient = world.getRainGradient(tickDelta);
		float thunderGradient = world.getThunderGradient(tickDelta);
		int lightningTicks = world.getLightningTicksLeft();

		if(rainGradient != lastRainGradient) {
			result = true;
			lastRainGradient = rainGradient;

			if(rainGradient > 0) {
				rainGradientMultiplier = 1.0f - rainGradient * 0.75F;
			} else {
				rainGradientMultiplier = 0;
			}
		}
		if(thunderGradient != lastThunderGradient) {
			result = true;
			lastThunderGradient = thunderGradient;
			if(thunderGradient > 0) {
				thunderGradientMultiplier = 1.0f - thunderGradient * 0.75F;
			} else {
				thunderGradientMultiplier = 0;
			}
		}
		if(lastLightningTicks != lightningTicks) {
			result = true;
			lastLightningTicks = lightningTicks;
		}

		SKY_COLOR.didTickChange = true;
		LIGHTMAP.didTickChange = true;

		if(result) {
			SKY_COLOR.thisInstanceDirty = true;
			LIGHTMAP.thisInstanceDirty = true;
		}
	}

	public boolean isDirty() {
		if(this.thisInstanceDirty) {
			this.thisInstanceDirty = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean didTickChange() {
		if(this.didTickChange) {
			this.didTickChange = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return in-game time passed since the last call of {@link #updateLastTime()}
	 */
	public long getTimeDelta() {
		return Math.abs(CLIENT.world.getLunarTime() - lastTime);
	}

	public void updateLastTime() {
		lastTime = CLIENT.world.getLunarTime();
	}
}