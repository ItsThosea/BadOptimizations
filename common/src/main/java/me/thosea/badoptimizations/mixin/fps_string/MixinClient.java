package me.thosea.badoptimizations.mixin.fps_string;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.thosea.badoptimizations.interfaces.ClientMethods;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlTimer.Query;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MixinClient implements ClientMethods {
	@Shadow private static int currentFps;
	@Shadow private int fpsCounter;
	@Shadow private long nextDebugInfoUpdateTime;

	@Shadow @Final public GameOptions options;
	@Shadow public String fpsDebugString;
	@Shadow private double gpuUtilizationPercentage;
	@Shadow @Nullable public Screen currentScreen;

	@Shadow
	protected abstract int getFramerateLimit();

	@Override
	public void bo$updateFpsString() {
		StringBuilder builder = new StringBuilder(32);

		int limit = getFramerateLimit();

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

	@Override
	public boolean bo$showDebugScreen() {
		return options.debugEnabled && (!options.hudHidden || currentScreen != null);
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", ordinal = 2, target = "Lnet/minecraft/client/MinecraftClient;currentGlTimerQuery:Lnet/minecraft/client/gl/GlTimer$Query;"))
	private Query onGetGlTimeQuery(Query original) {
		if(original == null || !bo$showDebugScreen()) {
			return null;
		}

		return original;
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"))
	private long onGetTime(Operation<Long> original) {
		// Identical to the vanilla loop...
		// but w/o String.format
		while(original.call() >= nextDebugInfoUpdateTime + 1000L) {
			currentFps = fpsCounter;
			nextDebugInfoUpdateTime += 1000L;
			fpsCounter = 0;

			if(bo$showDebugScreen()) {
				bo$updateFpsString();
			}
		}

		return 0; // Don't run the vanilla loop
	}
}
