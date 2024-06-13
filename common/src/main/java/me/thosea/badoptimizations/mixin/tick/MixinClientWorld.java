package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import me.thosea.badoptimizations.other.CommonColorFactors;
import me.thosea.badoptimizations.other.Config;
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

import static me.thosea.badoptimizations.other.CommonColorFactors.lastLightningTicks;
import static me.thosea.badoptimizations.other.CommonColorFactors.rainGradientMultiplier;
import static me.thosea.badoptimizations.other.CommonColorFactors.thunderGradientMultiplier;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {
	@Shadow @Final private MinecraftClient client;
	@Unique private BiomeSkyColorGetter biomeColors;
	@Unique private CommonColorFactors commonFactors;

	@Unique private Vec3d skyColorCache;

	@Unique private int lastBiomeColor;
	@Unique private Vec3d biomeColorVector;

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	private void onGetSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if(skyColorCache == null || client.player == null) return;

		CommonColorFactors.tick(tickDelta);

		if(this.commonFactors.didTickChange()) {
			if(isBiomeDirty(cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25))) {
				// Do vanilla behavior, so surrounding biomes are factored in
				return;
			} else if(commonFactors.isDirty() || commonFactors.getTimeDelta() >= Config.skycolor_time_change_needed_for_update) {
				skyColorCache = calcSkyColor(tickDelta);
			}
		}

		cir.setReturnValue(skyColorCache);
	}

	@Unique
	private boolean isBiomeDirty(Vec3d pos) {
		int x = MathHelper.floor(pos.x);
		int y = MathHelper.floor(pos.y);
		int z = MathHelper.floor(pos.z);

		int color = biomeColors.get(x - 2, y - 2, z - 2);
		if(lastBiomeColor != color) {
			lastBiomeColor = color;
			biomeColorVector = Vec3d.unpackRgb(color);
			return true;
		} else if(biomeColors.get(x + 3, y + 3, z + 3) != color) {
			return true;
		}

		return false;
	}

	@Shadow public abstract int getLightningTicksLeft();
	@Shadow public abstract Vec3d getSkyColor(Vec3d cameraPos, float tickDelta);

	@Unique
	private Vec3d calcSkyColor(float delta) {
		float angle = MathHelper.cos(getSkyAngle(1.0f) * 6.2831855F) * 2.0F + 0.5F;
		angle = MathHelper.clamp(angle, 0.0F, 1.0F);

		double x = biomeColorVector.x * angle;
		double y = biomeColorVector.y * angle;
		double z = biomeColorVector.z * angle;

		if(rainGradientMultiplier > 0.0f) {
			double color = (x * 0.3F + y * 0.59F + z * 0.11F) * 0.6F;

			x = x * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
			y = y * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
			z = z * rainGradientMultiplier + color * (1.0 - rainGradientMultiplier);
		}
		if(thunderGradientMultiplier > 0.0f) {
			double color = (x * 0.3F + y * 0.59F + z * 0.11F) * 0.2F;

			x = x * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
			y = y * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
			z = z * thunderGradientMultiplier + color * (1.0 - thunderGradientMultiplier);
		}
		if(lastLightningTicks > 0) {
			float lightningMultiplier = lastLightningTicks - delta;
			if(lightningMultiplier > 1.0F) {
				lightningMultiplier = 1.0F;
			}

			lightningMultiplier *= 0.45F;
			x = x * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
			y = y * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
			z = z * (1.0F - lightningMultiplier) + lightningMultiplier;
		}

		return new Vec3d(x, y, z);
	}

	@Inject(method = "getSkyColor", at = @At("RETURN"))
	private void afterGetSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		skyColorCache = cir.getReturnValue();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterInit(CallbackInfo ci) {
		commonFactors = CommonColorFactors.SKY_COLOR;
		lastBiomeColor = Integer.MIN_VALUE;
		biomeColorVector = Vec3d.ZERO;
		biomeColors = BiomeSkyColorGetter.of(getBiomeAccess());
	}

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
		throw new AssertionError("nuh uh");
	}
}
