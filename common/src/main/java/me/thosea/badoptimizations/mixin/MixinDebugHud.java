package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.ClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
	@Shadow public abstract boolean shouldShowDebugHud();
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "toggleDebugHud", at = @At("TAIL"))
	private void renderInject(CallbackInfo ci) {
		if(shouldShowDebugHud()) {
			((ClientAccessor) client).badoptimizations$updateFpsString();
		}
	}
}
