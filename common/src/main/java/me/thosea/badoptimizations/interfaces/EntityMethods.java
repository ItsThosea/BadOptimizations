package me.thosea.badoptimizations.interfaces;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

public interface EntityMethods {
	void bo$refreshEntityData(int data);

	<T extends Entity> EntityRenderer<T> bo$getRenderer();
}
