package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public final class MixinInGameHud {
	@Shadow @Final private MinecraftClient client;
	@Shadow private float spyglassScale;

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;spyglassScale:F", ordinal = 0))
	private float calculateSpyglass(float original) {
		return spyglassScale; // Discard lerp result
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderSpyglassOverlay(Lnet/minecraft/client/gui/DrawContext;F)V"))
	private void onRenderSpyglass(DrawContext context, float tickDelta, CallbackInfo ci) {
		spyglassScale = MathHelper.lerp(0.5F * client.getLastFrameDuration(), spyglassScale, 1.125F);
	}

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
