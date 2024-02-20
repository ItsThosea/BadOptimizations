package me.thosea.badoptimizations.mixin.fps_string;

import me.thosea.badoptimizations.interfaces.ClientMethods;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
	@Unique private ClientMethods clientMethods;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(MinecraftClient client, CallbackInfo ci) {
		this.clientMethods = (ClientMethods) client;
	}

	@Shadow public abstract boolean shouldShowDebugHud();

	@Inject(method = "toggleDebugHud", at = @At("TAIL"))
	private void renderInject(CallbackInfo ci) {
		if(shouldShowDebugHud()) {
			clientMethods.bo$updateFpsString();
		}
	}
}
