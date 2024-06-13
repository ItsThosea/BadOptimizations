package me.thosea.badoptimizations.mixin.accessor;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface PlayerAccessor {
	@Accessor("underwaterVisibilityTicks")
	int bo$underwaterVisibilityTicks();
}
