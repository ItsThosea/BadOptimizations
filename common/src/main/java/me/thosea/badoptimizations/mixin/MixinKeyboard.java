package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.ClientAccessor;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public final class MixinKeyboard {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "onKey", at = @At(value = "FIELD", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
	private void onToggleF3(CallbackInfo ci) {
		if(ClientAccessor.shouldUpdateFpsString(client)) {
			((ClientAccessor) client).badoptimizations$updateFpsString();
		}
	}

	@Inject(method = "onKey", at = @At(value = "FIELD", ordinal = 1, shift = Shift.AFTER, target = "Lnet/minecraft/client/option/GameOptions;hudHidden:Z"))
	private void onToggleHud(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if(ClientAccessor.shouldUpdateFpsString(client)) {
			((ClientAccessor) client).badoptimizations$updateFpsString();
		}
	}
}
