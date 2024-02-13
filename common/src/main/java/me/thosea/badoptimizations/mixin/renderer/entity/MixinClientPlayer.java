package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.mixin.entitydata.MixinEntity;
import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinClientPlayer extends MixinEntity {
	@Shadow public abstract String getModel();

	@Override
	@SuppressWarnings("unchecked")
	public EntityRenderer<?> bo$getRenderer() {
		String model = getModel();

		if(model.equals("default")) {
			return PlayerModelRendererHolder.WIDE_RENDERER;
		} else if(model.equals("slim")) {
			return PlayerModelRendererHolder.SLIM_RENDERER;
		} else {
			throw new IncompatibleClassChangeError("BadOptimizations: unexpected player model type " + model);
		}
	}
}
