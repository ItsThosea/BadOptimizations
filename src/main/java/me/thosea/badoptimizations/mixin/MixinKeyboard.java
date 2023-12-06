package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.ClientAccessor;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public final class MixinKeyboard {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "onKey", at = @At(value = "FIELD", ordinal = 2, target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
	private void renderInject(CallbackInfo ci) {
		if(client.options.debugEnabled) {
			((ClientAccessor) client).updateFpsString();
		}
	}
}
