package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.ClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(MinecraftClient.class)
public abstract class MixinClient implements ClientAccessor {
	@Shadow private static int currentFps;
	@Shadow private int fpsCounter;
	@Shadow private long nextDebugInfoUpdateTime;

	@Shadow @Final public GameOptions options;
	@Shadow public String fpsDebugString;
	@Shadow private double gpuUtilizationPercentage;

	@Shadow protected abstract int getFramerateLimit();

	@Shadow private @Nullable GlTimer.Query currentGlTimerQuery;
	@Shadow private long metricsSampleDuration;

	@Override
	public void updateFpsString() {
		// decompiled
		int k = getFramerateLimit();

		if(currentGlTimerQuery != null && currentGlTimerQuery.isResultAvailable()) {
			this.gpuUtilizationPercentage = (double) this.currentGlTimerQuery.queryResult() * 100.0 / (double) metricsSampleDuration;
		}

		String string;
		if(gpuUtilizationPercentage > 0.0) {
			String var10000 = gpuUtilizationPercentage > 100.0 ? Formatting.RED + "100%" : Math.round(this.gpuUtilizationPercentage) + "%";
			string = " GPU: " + var10000;
		} else {
			string = "";
		}

		this.fpsDebugString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", currentFps, k == 260 ? "inf" : k, this.options.getEnableVsync()
				.getValue() ? " vsync" : "", this.options.getGraphicsMode()
				.getValue(), this.options.getCloudRenderMode()
				.getValue() == CloudRenderMode.OFF ? "" : (this.options.getCloudRenderMode()
				.getValue() == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"), this.options.getBiomeBlendRadius()
				.getValue(), string);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 7), cancellable = true)
	private void onDebugHandle(boolean tick, CallbackInfo ci) {
		if(!options.debugEnabled) {
			ci.cancel();

			if(Util.getMeasuringTimeMs() >= nextDebugInfoUpdateTime + 1000L) {
				// Track FPS
				currentFps = fpsCounter;
				nextDebugInfoUpdateTime += 1000L;
				fpsCounter = 0;
				fpsDebugString = "";
			}
		}
	}
}
