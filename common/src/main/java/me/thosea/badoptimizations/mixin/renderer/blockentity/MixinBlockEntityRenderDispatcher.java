package me.thosea.badoptimizations.mixin.renderer.blockentity;

import me.thosea.badoptimizations.interfaces.BlockEntityTypeMethods;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Map.Entry;

@Mixin(value = BlockEntityRenderDispatcher.class, priority = 700)
public abstract class MixinBlockEntityRenderDispatcher {
	@Shadow private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers;

	@Overwrite
	@Nullable
	public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
		return ((BlockEntityTypeMethods) blockEntity.getType()).bo$getRenderer();
	}

	@Inject(method = "reload", at = @At("RETURN"))
	private void afterReload(ResourceManager manager, CallbackInfo ci) {
		for(Entry<BlockEntityType<?>, BlockEntityRenderer<?>> entry : renderers.entrySet()) {
			((BlockEntityTypeMethods) entry.getKey()).bo$setRenderer(entry.getValue());
		}
	}
}