package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class MixinEntityType implements EntityTypeMethods {
	@Unique private EntityRenderer<?> renderer;

	@Override
	public EntityRenderer<?> bo$getRenderer() {
		return renderer;
	}

	@Override
	public void bo$setRenderer(EntityRenderer<?> renderer) {
		this.renderer = renderer;
	}
}
