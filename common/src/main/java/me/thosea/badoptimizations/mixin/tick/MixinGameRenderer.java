package me.thosea.badoptimizations.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public final class MixinGameRenderer {
	@Shadow @Final private MinecraftClient client;

	// don't do unneeded FOV calculations
	@WrapOperation(method = "updateFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getFovMultiplier(ZF)F"))
	private float getPlayerFov(AbstractClientPlayerEntity player,
	                           boolean firstPerson, float fovEffectScale,
	                           Operation<Float> original) {
		if(fovEffectScale == 0f) {
			if(client.options.getPerspective() == Perspective.FIRST_PERSON && player.isUsingSpyglass()) {
				return 0.1f;
			} else {
				return 1.0f;
			}
		} else {
			return original.call(player, firstPerson, fovEffectScale);
		}
	}
}