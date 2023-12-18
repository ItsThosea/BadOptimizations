package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public final class MixinInGameHud {
	@Shadow @Final private MinecraftClient client;

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 1))
	private float calculatePortalEffect(float delta, float start, float end, Operation<Float> original) {
		if(!client.player.inNetherPortal) {
			return 0f;
		} else {
			return original.call(delta, start, end);
		}
	}

	@Inject(method = "tickAutosaveIndicator", at = @At("HEAD"), cancellable = true)
	private void onTickAutosaveIndicator(CallbackInfo ci) {
		 // Technically this check is already done but not as early
		if(!client.isInSingleplayer()) ci.cancel();
	}
}
