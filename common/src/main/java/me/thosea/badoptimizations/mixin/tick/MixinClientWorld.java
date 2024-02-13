package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {
	@Shadow @Final private MinecraftClient client;
	@Unique private BiomeSkyColorGetter biomeColors;

	@Unique private Vec3d skyColorCache;
	@Unique private int lastTick;

	@Unique private long previousTime;

	@Unique private int previousBiomeColor;
	@Unique private Vec3d biomeColorVector;

	@Unique private Vec3d previousCameraPos;

	@Unique private float previousRainGradient;
	@Unique private float previousThunderGradient;
	@Unique private int previousLightningTicks;

	@Unique
	private boolean isSkyColorDirty(Vec3d pos) {
		boolean result = false;

		if(isBiomeDirty(pos)) {
			result = true;
		}

		float rainGradient = this.rainGradient; // protected in World
		if(previousRainGradient != rainGradient) {
			previousRainGradient = rainGradient;
			result = true;
		}

		float thunderGradient = this.thunderGradient; // protected in World
		if(previousThunderGradient != thunderGradient) {
			previousThunderGradient = thunderGradient;
			result = true;
		}

		int lightningTicks = getLightningTicksLeft(); // shadow method
		if(previousLightningTicks != lightningTicks) {
			previousLightningTicks = lightningTicks;
			result = true;
		}

		return result;
	}

	@Unique
	private boolean isBiomeDirty(Vec3d pos) {
		if(pos.squaredDistanceTo(previousCameraPos) < (0.23 * 0.23)) {
			return false;
		} else {
			previousCameraPos = pos;
		}

		int x = MathHelper.floor(pos.x);
		int y = MathHelper.floor(pos.y);
		int z = MathHelper.floor(pos.z);

		int color = biomeColors.get(x, y, z);
		if(previousBiomeColor != color) {
			previousBiomeColor = color;
			biomeColorVector = Vec3d.unpackRgb(color);
			return true;
		} else if(biomeColors.get(x - 2, y - 2, z - 2) != color
				|| biomeColors.get(x + 3, y + 3, z + 3) != color) {
			return true;
		}

		return false;
	}

	@Shadow public abstract int getLightningTicksLeft();
	@Shadow public abstract Vec3d getSkyColor(Vec3d cameraPos, float tickDelta);

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	private void onGetColorHead(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if(skyColorCache == null) return;

		int tick = client.player.age;

		if(lastTick != tick) {
			lastTick = tick;

			cameraPos = cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25);

			if(isSkyColorDirty(cameraPos)) {
				return;
			} else {
				long time = getTimeOfDay(); // public World method
				if(Math.abs(time - previousTime) >= 3) {
					previousTime = time;
					calcSkyColor();
				}
			}
		}

		cir.setReturnValue(skyColorCache);
	}

	@Unique
	private void calcSkyColor() {
		double x = 0;
		double y = 0;
		double z = 0;

		for(double multiplier : BiomeSkyColorGetter.MULTIPLIERS) {
			x += biomeColorVector.x * multiplier;
			y += biomeColorVector.y * multiplier;
			z += biomeColorVector.z * multiplier;
		}

		double multiplier = 1.0 / 4096;
		x *= multiplier;
		y *= multiplier;
		z *= multiplier;

		float angle = MathHelper.cos(getSkyAngle(1.0f) * 6.2831855F) * 2.0F + 0.5F;
		angle = MathHelper.clamp(angle, 0.0F, 1.0F);

		skyColorCache = new Vec3d(x * angle, y * angle, z * angle);
	}

	@Inject(method = "getSkyColor", at = @At("RETURN"))
	private void onGetColorTail(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		skyColorCache = cir.getReturnValue();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterInit(CallbackInfo ci) {
		lastTick = -1;
		previousCameraPos = new Vec3d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
		biomeColors = BiomeSkyColorGetter.of(getBiomeAccess());
	}

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
		throw new AssertionError("nuh uh");
	}
}
