package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import me.thosea.badoptimizations.mixin.entitydata.MixinEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinClientPlayer extends MixinEntity {
	@Shadow public abstract SkinTextures getSkinTextures();

	@Override
	@SuppressWarnings("unchecked")
	public EntityRenderer<?> bo$getRenderer() {
		Model model = getSkinTextures().model();

		if(model == Model.WIDE) {
			return PlayerModelRendererHolder.WIDE_RENDERER;
		} else if(model == Model.SLIM) {
			return PlayerModelRendererHolder.SLIM_RENDERER;
		} else {
			throw new IncompatibleClassChangeError("BadOptimizations: unexpected player model type " + model);
		}
	}
}
