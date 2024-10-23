package me.thosea.badoptimizations.mixin.renderer.entity;

import me.thosea.badoptimizations.interfaces.EntityMethods;
import me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityMethods {
	private EntityTypeMethods bo$typeMethods;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void afterInit(EntityType<?> type, World world, CallbackInfo ci) {
		this.bo$typeMethods = (EntityTypeMethods) type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EntityRenderer<?, ?> bo$getRenderer() {
		return bo$typeMethods.bo$getRenderer();
	}
}