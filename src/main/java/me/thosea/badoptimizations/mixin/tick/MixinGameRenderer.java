package me.thosea.badoptimizations.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public final class MixinGameRenderer {
	@Shadow @Final MinecraftClient client;

	@Unique private SimpleOption<Double> fovEffectScale;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterCreate(MinecraftClient client,
	                         HeldItemRenderer heldItemRenderer,
	                         ResourceManager resourceManager,
	                         BufferBuilderStorage buffers,
	                         CallbackInfo ci) {
		fovEffectScale = client.options.getFovEffectScale();
	}

	// don't do unneeded FOV calculations
	@WrapOperation(method = "updateFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getFovMultiplier()F"))
	private float getPlayerFov(AbstractClientPlayerEntity instance, Operation<Float> original) {
		if(fovEffectScale.getValue() == 0) {
			if(client.options.getPerspective() == Perspective.FIRST_PERSON &&
					client.getCameraEntity() instanceof AbstractClientPlayerEntity player &&
					player.isUsingSpyglass()) {
				return 0.1f;
			} else {
				return 1.0f;
			}
		} else {
			return original.call(instance);
		}
	}
}
