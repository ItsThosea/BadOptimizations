package me.thosea.badoptimizations.mixin.debug;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer.PointOfInterest;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.custom.DebugBrainCustomPayload.Brain;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(VillageDebugRenderer.class)
public class MixinVillageDebugRenderer {
	@Shadow @Final private Map<BlockPos, PointOfInterest> pointsOfInterest;
	@Shadow @Final private Map<UUID, Brain> brains;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if(pointsOfInterest.isEmpty() && brains.isEmpty())
			ci.cancel();
	}
}