package me.thosea.badoptimizations.mixin.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityFieldAccessor {
	@Accessor
	boolean isInNetherPortal();
}
