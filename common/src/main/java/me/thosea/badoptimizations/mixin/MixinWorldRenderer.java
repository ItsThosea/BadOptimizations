package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorldRenderer.class, priority = 700)
public abstract class MixinWorldRenderer {
	// method_62215 = lambda in renderSky
	@WrapOperation(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyAngleRadians(F)F"))
	private float cacheSkyAngleRadians(ClientWorld world, float delta, Operation<Float> original,
	                                   @Share("skyAngleRadians") LocalFloatRef skyAngleRadians) {
		float result = original.call(world, delta);
		skyAngleRadians.set(result);
		return result;
	}

	@WrapOperation(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyAngle(F)F"))
	private float getSkyAngle(ClientWorld instance, float delta, Operation<Float> original,
	                          @Share("skyAngleRadians") LocalFloatRef skyAngleRadians) {
		return skyAngleRadians.get() / 6.2831855F;
	}
}