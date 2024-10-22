package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload.Bee;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BeeDebugRenderer.class)
public class MixinBeeDebugRenderer {
	@Shadow @Final private Map<BlockPos, ?> hives; // i dont wanna use accesswidener
	@Shadow @Final private Map<UUID, Bee> bees;

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;DDD)V", at = @At("HEAD"), cancellable = true)
	private void onRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(hives.isEmpty() && bees.isEmpty())
			ci.cancel();
	}
}