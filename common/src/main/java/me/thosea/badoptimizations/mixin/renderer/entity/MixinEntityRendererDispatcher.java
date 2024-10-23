package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.interfaces.EntityMethods;
import me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Map.Entry;

@Mixin(value = EntityRenderDispatcher.class, priority = 700)
public abstract class MixinEntityRendererDispatcher {
	@Shadow private Map<EntityType<?>, EntityRenderer<?, ?>> renderers;
	@Shadow private Map<Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers;

	@Overwrite
	public <T extends Entity & EntityMethods> EntityRenderer<? super T, ?> getRenderer(T entity) {
		return entity.bo$getRenderer();
	}

	@Inject(method = "reload", at = @At("RETURN"))
	private void afterReload(ResourceManager manager, CallbackInfo ci) {
		for(Entry<EntityType<?>, EntityRenderer<?, ?>> entry : renderers.entrySet()) {
			((EntityTypeMethods) entry.getKey()).bo$setRenderer(entry.getValue());
		}

		// Used by MixinClientPlayer
		PlayerModelRendererHolder.WIDE_RENDERER = modelRenderers.get(Model.WIDE);
		PlayerModelRendererHolder.SLIM_RENDERER = modelRenderers.get(Model.SLIM);
	}
}