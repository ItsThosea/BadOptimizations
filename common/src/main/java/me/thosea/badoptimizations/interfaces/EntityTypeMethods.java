package me.thosea.badoptimizations.interfaces;

import net.minecraft.client.render.entity.EntityRenderer;

public interface EntityTypeMethods {
	EntityRenderer<?, ?> bo$getRenderer();
	void bo$setRenderer(EntityRenderer<?, ?> renderer);
}