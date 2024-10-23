package me.thosea.badoptimizations.interfaces;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

public interface BlockEntityTypeMethods {
	<T extends BlockEntity> BlockEntityRenderer<T> bo$getRenderer();
	void bo$setRenderer(BlockEntityRenderer<?> renderer);
}