package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.interfaces.ClientMethods;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public final class MixinKeyboard {
	@Shadow @Final private MinecraftClient client;
	@Unique private ClientMethods clientMethods;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(MinecraftClient client, CallbackInfo ci) {
		this.clientMethods = (ClientMethods) client;
	}

	@Inject(method = "onKey", at = @At(value = "FIELD", ordinal = 1, shift = Shift.AFTER, target = "Lnet/minecraft/client/option/GameOptions;hudHidden:Z"))
	private void onToggleHud(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if(client.getDebugHud().shouldShowDebugHud()) {
			clientMethods.bo$updateFpsString();
		}
	}
}
