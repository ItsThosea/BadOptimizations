package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public class MixinEntityType implements EntityTypeMethods {
	private EntityRenderer<?> bo$renderer;

	@Override
	public EntityRenderer<?> bo$getRenderer() {
		return bo$renderer;
	}

	@Override
	public void bo$setRenderer(EntityRenderer<?> renderer) {
		this.bo$renderer = renderer;
	}
}