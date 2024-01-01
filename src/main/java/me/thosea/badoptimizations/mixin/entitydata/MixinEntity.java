package me.thosea.badoptimizations.mixin.entitydata;

import me.thosea.badoptimizations.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityAccessor {
	@Shadow @Final protected static TrackedData<Byte> FLAGS;
	@Shadow public World world;

	@Shadow
	protected abstract boolean getFlag(int index);

	@Shadow
	public abstract boolean isFireImmune();

	@Unique private boolean glowing;
	@Unique private boolean onFire = false;
	@Unique private boolean sneaking = false;
	@Unique private boolean sprinting = false;
	@Unique private boolean swimming = false;
	@Unique private boolean invisible = false;

	@Shadow @Final private static TrackedData<Integer> AIR;

	@Shadow
	public abstract int getMaxAir();

	@Unique private int remainingAirTicks = getMaxAir();

	@Shadow @Final private static TrackedData<Optional<Text>> CUSTOM_NAME;
	@Unique private Optional<Text> customName = Optional.empty();

	@Shadow @Final private static TrackedData<Boolean> NAME_VISIBLE;
	@Unique private boolean nameVisible = false;

	@Shadow @Final private static TrackedData<Boolean> SILENT;
	@Unique private boolean silent = false;

	@Shadow @Final private static TrackedData<Boolean> NO_GRAVITY;
	@Unique private boolean noGravity = false;

	@Shadow @Final protected static TrackedData<EntityPose> POSE;
	@Unique private EntityPose pose = EntityPose.STANDING;

	@Shadow @Final private static TrackedData<Integer> FROZEN_TICKS;

	@Shadow public abstract DataTracker getDataTracker();
	@Shadow private int fireTicks;
	@Unique	private int frozenTicks = 0;

	@Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
	private void isOnFire(CallbackInfoReturnable<Boolean> cir) {
		if(fireTicks > 0) {
			cir.setReturnValue(true);
		} else {
			cir.setReturnValue(onFire && world.isClient);
		}
	}

	@Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
	private void isSneaking(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(sneaking);
	}

	@Inject(method = "isSprinting", at = @At("HEAD"), cancellable = true)
	private void isSprinting(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(sprinting);
	}

	@Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
	private void isSwimming(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(swimming);
	}

	@Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
	private void isInvisible(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(invisible);
	}

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void isGlowing(CallbackInfoReturnable<Boolean> cir) {
		if(world.isClient) {
			cir.setReturnValue(glowing);
		}
	}

	@Inject(method = "getAir", at = @At("HEAD"), cancellable = true)
	private void getAir(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(remainingAirTicks);
	}

	@Inject(method = "getCustomName", at = @At("HEAD"), cancellable = true)
	private void getCustomName(CallbackInfoReturnable<Text> cir) {
		cir.setReturnValue(customName.orElse(null));
	}

	@Inject(method = "hasCustomName", at = @At("HEAD"), cancellable = true)
	private void hasCustomName(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(customName.isPresent());
	}

	@Inject(method = "isCustomNameVisible", at = @At("HEAD"), cancellable = true)
	private void isCustomNameVisible(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(nameVisible);
	}

	@Inject(method = "isSilent", at = @At("HEAD"), cancellable = true)
	private void isSilent(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(silent);
	}

	@Inject(method = "hasNoGravity", at = @At("HEAD"), cancellable = true)
	private void hasNoGravity(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(noGravity);
	}

	@Inject(method = "getPose", at = @At("HEAD"), cancellable = true)
	private void getPose(CallbackInfoReturnable<EntityPose> cir) {
		cir.setReturnValue(pose);
	}

	@Inject(method = "getFrozenTicks", at = @At("HEAD"), cancellable = true)
	private void getFrozenTicks(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(frozenTicks);
	}

	@Override
	public void badoptimizations$refreshEntityData(TrackedData<?> data) {
		DataTracker dataTracker = getDataTracker();

		if(FLAGS.equals(data)) {
			onFire = !isFireImmune() && getFlag(0);
			sneaking = getFlag(1);
			sprinting = getFlag(3);
			swimming = getFlag(4);
			invisible = getFlag(5);
			if(world.isClient) {
				glowing = getFlag(6);
			}
		} else if(AIR.equals(data)) {
			remainingAirTicks = dataTracker.get(AIR);
		} else if(CUSTOM_NAME.equals(data)) {
			customName = dataTracker.get(CUSTOM_NAME);
		} else if(NAME_VISIBLE.equals(data)) {
			nameVisible = dataTracker.get(NAME_VISIBLE);
		} else if(SILENT.equals(data)) {
			silent = dataTracker.get(SILENT);
		} else if(NO_GRAVITY.equals(data)) {
			noGravity = dataTracker.get(NO_GRAVITY);
		} else if(POSE.equals(data)) {
			pose = dataTracker.get(POSE);
		} else if(FROZEN_TICKS.equals(data)) {
			frozenTicks = dataTracker.get(FROZEN_TICKS);
		}
	}
}
