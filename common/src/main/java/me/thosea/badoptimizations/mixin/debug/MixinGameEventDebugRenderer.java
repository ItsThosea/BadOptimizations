package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.GameEventDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GameEventDebugRenderer.class)
public class MixinGameEventDebugRenderer {
	@Shadow @Final private List<?> entries; // i dont wanna use accesswidener

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onInject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(entries.isEmpty()) ci.cancel();
	}
}