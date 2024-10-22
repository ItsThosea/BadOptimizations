package me.thosea.badoptimizations.mixin.tick;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.tutorial.TutorialManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TutorialManager.class)
public final class MixinTutorial {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		if(!client.isDemo()) ci.cancel();
	}
}