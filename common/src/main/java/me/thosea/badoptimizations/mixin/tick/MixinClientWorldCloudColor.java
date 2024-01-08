package me.thosea.badoptimizations.mixin.tick;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
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
public abstract class MixinClientWorldCloudColor extends World {
	@Shadow @Final private MinecraftClient client;

	@Unique private Vec3d cloudColorCache;
	@Unique private int lastTick;

	@Unique private long previousTime;
	@Unique private float previousRainGradient;
	@Unique private float previousThunderGradient;

	@Unique
	private boolean isCloudColorDirty() {
		boolean result = false;

		long time = getTimeOfDay(); // public World method
		if(Math.abs(time - previousTime) >= 50) {
			previousTime = time;
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

		return result;
	}

	@Shadow public abstract int getLightningTicksLeft();
	@Shadow public abstract Vec3d getCloudsColor(float tickDelta);

	@Inject(method = "getCloudsColor", at = @At("HEAD"), cancellable = true)
	private void onGetCloudColorHead(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if(lastTick == -1) return;

		int tick = client.player.age;

		if(lastTick != tick) {
			lastTick = tick;

			if(isCloudColorDirty()) {
				return;
			}
		}

		cir.setReturnValue(cloudColorCache);
	}

	@Inject(method = "getCloudsColor", at = @At("TAIL"))
	private void onGetCloudColorTail(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		cloudColorCache = cir.getReturnValue();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterInit(CallbackInfo ci) {
		lastTick = -1;
		getCloudsColor(client.getTickDelta());
	}

	protected MixinClientWorldCloudColor(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
		throw new AssertionError("nuh uh");
	}
}
