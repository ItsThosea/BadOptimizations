package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GameTestDebugRenderer.class)
public class MixinGameTestDebugRenderer {
	@Shadow @Final private Map<BlockPos, ?> markers; // i. do not want to use accesswidener

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(markers.isEmpty()) ci.cancel();
	}
}