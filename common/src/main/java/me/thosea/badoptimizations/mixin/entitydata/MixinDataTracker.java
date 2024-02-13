package me.thosea.badoptimizations.mixin.entitydata;

import me.thosea.badoptimizations.interfaces.EntityMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Entry;
import net.minecraft.entity.data.TrackedData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Mixin(DataTracker.class)
public abstract class MixinDataTracker {
	@Shadow @Final @Mutable private ReadWriteLock lock;
	@Unique private EntityMethods entityMethods;

	@Inject(method = "set", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/entity/data/DataTracker$Entry;set(Ljava/lang/Object;)V"))
	private void onDataSet(TrackedData<?> key, Object value, CallbackInfo ci) {
		entityMethods.bo$refreshEntityData(key.getId());
	}

	@Inject(method = "copyToFrom", at = @At("TAIL"))
	private void onCopy(Entry<?> to, Entry<?> from, CallbackInfo ci) {
		entityMethods.bo$refreshEntityData(from.getData().getId());
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void replaceLock(Entity trackedEntity, CallbackInfo ci) {
		Lock lock = new Lock() {
			@Override
			public void lock() {}
			@Override
			public void lockInterruptibly() {}
			@Override
			public boolean tryLock() {return true;}
			@Override
			public boolean tryLock(long time, @NotNull TimeUnit unit) {return true;}
			@Override
			public void unlock() {}
			@NotNull
			@Override
			public Condition newCondition() {
				throw new UnsupportedOperationException();
			}
		};

		this.lock = new ReadWriteLock() {
			@NotNull @Override
			public Lock readLock() {return lock;}
			@NotNull @Override
			public Lock writeLock() {return lock;}
		};
		this.entityMethods = (EntityMethods) trackedEntity;
	}
}
