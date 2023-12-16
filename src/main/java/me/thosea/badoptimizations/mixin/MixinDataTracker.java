package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.DummyLock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.ReadWriteLock;

// All entity data is only accessed on one thread,
// so this mixin removes unnecessary locks.
@Mixin(DataTracker.class)
public abstract class MixinDataTracker {
	@Shadow @Final @Mutable private ReadWriteLock lock;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void replaceLock(Entity trackedEntity, CallbackInfo ci) {
		this.lock = DummyLock.INSTANCE;
	}
}
