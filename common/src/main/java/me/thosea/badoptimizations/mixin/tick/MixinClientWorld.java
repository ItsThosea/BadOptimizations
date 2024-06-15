package me.thosea.badoptimizations.mixin.tick;

import me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import me.thosea.badoptimizations.other.CommonColorFactors;
import me.thosea.badoptimizations.other.Config;
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

	private BiomeSkyColorGetter bo$biomeColors;
	private CommonColorFactors bo$commonFactors;

	private Vec3d bo$skyColorCache;

	private int bo$lastBiomeColor;
	private Vec3d bo$biomeColorVector;

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	private void onGetSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if(bo$skyColorCache == null || client.player == null) return;

		CommonColorFactors.tick();

		if(this.bo$commonFactors.didTickChange()) {
			if(bo$isBiomeDirty(cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25))) {
				bo$commonFactors.updateLastTime();
				// Do vanilla behavior, so surrounding biomes are factored in
				return;
			} else if(bo$commonFactors.isDirty() || bo$commonFactors.getTimeDelta() >= Config.skycolor_time_change_needed_for_update) {
				bo$skyColorCache = bo$calcSkyColor(tickDelta);
				bo$commonFactors.updateLastTime();
			}
		}

		cir.setReturnValue(bo$skyColorCache);
	}

	private boolean bo$isBiomeDirty(Vec3d pos) {
		int x = MathHelper.floor(pos.x);
		int y = MathHelper.floor(pos.y);
		int z = MathHelper.floor(pos.z);

		int color = bo$biomeColors.get(x - 2, y - 2, z - 2);
		if(bo$lastBiomeColor != color) {
			bo$lastBiomeColor = color;
			bo$biomeColorVector = Vec3d.unpackRgb(color);
			return true;
		} else if(bo$biomeColors.get(x + 3, y + 3, z + 3) != color) {
			return true;
		}

		return false;
	}

	@Shadow public abstract int getLightningTicksLeft();
	@Shadow public abstract Vec3d getSkyColor(Vec3d cameraPos, float tickDelta);

	private Vec3d bo$calcSkyColor(float delta) {
		float angle = MathHelper.cos(getSkyAngle(1.0f) * 6.2831855F) * 2.0F + 0.5F;
		angle = MathHelper.clamp(angle, 0.0F, 1.0F);

		double x = bo$biomeColorVector.x * angle;
		double y = bo$biomeColorVector.y * angle;
		double z = bo$biomeColorVector.z * angle;

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
		bo$skyColorCache = cir.getReturnValue();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterInit(CallbackInfo ci) {
		bo$commonFactors = CommonColorFactors.SKY_COLOR;
		bo$lastBiomeColor = Integer.MIN_VALUE;
		bo$biomeColorVector = Vec3d.ZERO;
		bo$biomeColors = BiomeSkyColorGetter.of(getBiomeAccess());
	}

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
		throw new AssertionError("nuh uh");
	}
}
