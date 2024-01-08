package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.ClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.gui.screen.Screen;
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

@Mixin(MinecraftClient.class)
public abstract class MixinClient implements ClientAccessor {
	@Shadow private static int currentFps;
	@Shadow private int fpsCounter;
	@Shadow private long nextDebugInfoUpdateTime;

	@Shadow @Final public GameOptions options;
	@Shadow public String fpsDebugString;
	@Shadow private double gpuUtilizationPercentage;

	@Shadow
	protected abstract int getFramerateLimit();

	@Shadow private @Nullable GlTimer.Query currentGlTimerQuery;
	@Shadow private long metricsSampleDuration;

	@Override
	public void badoptimizations$updateFpsString() {
		int limit = getFramerateLimit();

		if(currentGlTimerQuery != null && currentGlTimerQuery.isResultAvailable()) {
			this.gpuUtilizationPercentage = (double) this.currentGlTimerQuery.queryResult() * 100.0 / (double) metricsSampleDuration;
		}

		StringBuilder builder = new StringBuilder(32);

		builder.append(currentFps).append(" fps T: ");
		builder.append(limit == 260 ? "inf" : limit);

		if(this.options.getEnableVsync().getValue()) {
			builder.append(" vsync");
		}

		builder.append(options.getGraphicsMode().getValue());

		CloudRenderMode clouds = options.getCloudRenderMode().getValue();
		if(clouds != CloudRenderMode.OFF) {
			builder.append(clouds == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds");
		}

		builder.append(" B: ");
		builder.append(this.options.getBiomeBlendRadius().getValue());

		if(gpuUtilizationPercentage > 0.0) {
			String var10000 = gpuUtilizationPercentage > 100.0 ? Formatting.RED + "100%" : Math.round(this.gpuUtilizationPercentage) + "%";
			builder.append(" GPU: ").append(var10000);
		}

		// this.fpsDebugString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", currentFps, k == 260 ? "inf" : k, this.options.getEnableVsync()
		// 		.getValue() ? " vsync" : "", this.options.getGraphicsMode()
		// 		.getValue(), this.options.getCloudRenderMode()
		// 		.getValue() == CloudRenderMode.OFF ? "" : (this.options.getCloudRenderMode()
		// 		.getValue() == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"), this.options.getBiomeBlendRadius()
		// 		.getValue(), string);
		this.fpsDebugString = builder.toString();
	}

	@Inject(method = "setScreen", at = @At("TAIL"))
	private void onOpenScreen(Screen screen, CallbackInfo ci) {
		if(screen != null && options.hudHidden && options.debugEnabled) {
			badoptimizations$updateFpsString();
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 7), cancellable = true)
	private void onDebugHandle(boolean tick, CallbackInfo ci) {
		ci.cancel();

		while(Util.getMeasuringTimeMs() >= nextDebugInfoUpdateTime + 1000L) {
			// Track FPS
			currentFps = fpsCounter;
			nextDebugInfoUpdateTime += 1000L;
			fpsCounter = 0;

			if(ClientAccessor.shouldUpdateFpsString(MinecraftClient.getInstance())) {
				badoptimizations$updateFpsString();
			}
		}
	}
}
