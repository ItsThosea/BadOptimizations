package me.thosea.badoptimizations.mixin.renderer.blockentity;

import me.thosea.badoptimizations.interfaces.BlockEntityTypeMethods;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntityType.class)
public class MixinBlockEntityType implements BlockEntityTypeMethods {
	private BlockEntityRenderer<?> bo$renderer;

	@Override
	@SuppressWarnings("unchecked")
	public BlockEntityRenderer<?> bo$getRenderer() {
		return bo$renderer;
	}

	@Override
	public void bo$setRenderer(BlockEntityRenderer<?> renderer) {
		this.bo$renderer = renderer;
	}
}
