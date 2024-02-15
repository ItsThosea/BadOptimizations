package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
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

	@Unique private float previousRainGradient;
	@Unique private float rainGradientMultiplier;

	@Unique private float previousThunderGradient;
	@Unique private float thunderGradientMultiplier;

	@Unique private int previousLightningTicks;

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	private void onGetColorHead(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if(skyColorCache == null) return;

		int tick = client.player.age;

		if(lastTick != tick) {
			lastTick = tick;

			boolean doMiniUpdate = false;

			float rainGradient = this.rainGradient; // protected in World
			if(previousRainGradient != rainGradient) {
				previousRainGradient = rainGradient;
				doMiniUpdate = true;
				if(rainGradient > 0) {
					rainGradientMultiplier = 1.0f - rainGradient * 0.75F;
				}
			}

			float thunderGradient = this.thunderGradient; // protected in World
			if(previousThunderGradient != thunderGradient) {
				previousThunderGradient = thunderGradient;
				doMiniUpdate = true;
				if(thunderGradient > 0) {
					thunderGradientMultiplier = 1.0f - thunderGradient * 0.75F;
				}
			}

			int lightningTicks = getLightningTicksLeft(); // shadow method
			if(previousLightningTicks != lightningTicks) {
				doMiniUpdate = true;
				previousLightningTicks = lightningTicks;
			}

			if(isBiomeDirty(cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25))) {
				return;
			} else {
				long time = getTimeOfDay(); // public World method
				if(doMiniUpdate || Math.abs(time - previousTime) >= 3) {
					previousTime = time;
					calcSkyColor(tickDelta);
				}
			}
		}

		cir.setReturnValue(skyColorCache);
	}

	@Unique
	private boolean isBiomeDirty(Vec3d pos) {
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

	@Unique
	private void calcSkyColor(float delta) {
		double x = 0;
		double y = 0;
		double z = 0;

		for(double multiplier : BiomeSkyColorGetter.MULTIPLIERS) {
			x += biomeColorVector.x * multiplier;
			y += biomeColorVector.y * multiplier;
			z += biomeColorVector.z * multiplier;
		}

		float angle = MathHelper.cos(getSkyAngle(1.0f) * 6.2831855F) * 2.0F + 0.5F;
		angle = MathHelper.clamp(angle, 0.0F, 1.0F);
		double multiplier = (1.0 / 4096) * angle;

		x *= multiplier;
		y *= multiplier;
		z *= multiplier;

		if(rainGradient > 0.0f) {
			double color = (x * 0.3F + y * 0.59F + z * 0.11F) * 0.6F;

			x = x * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
			y = y * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
			z = z * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
		}
		if(thunderGradient > 0.0f) {
			double color = (x * 0.3F + y * 0.59F + z * 0.11F) * 0.2F;

			x = x * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
			y = y * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
			z = z * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
		}
		if(previousLightningTicks > 0) {
			float lightningMultiplier = previousLightningTicks - delta;
			if(lightningMultiplier > 1.0F) {
				lightningMultiplier = 1.0F;
			}

			lightningMultiplier *= 0.45F;
			x = x * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
			y = y * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
			z = z * (1.0F - lightningMultiplier) + lightningMultiplier;
		}

		skyColorCache = new Vec3d(x, y, z);
	}

	@Inject(method = "getSkyColor", at = @At("RETURN"))
	private void onGetColorTail(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		skyColorCache = cir.getReturnValue();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterInit(CallbackInfo ci) {
		lastTick = -1;
		previousBiomeColor = Integer.MIN_VALUE;
		biomeColorVector = Vec3d.ZERO;
		biomeColors = BiomeSkyColorGetter.of(getBiomeAccess());
	}

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
		throw new AssertionError("nuh uh");
	}
}
