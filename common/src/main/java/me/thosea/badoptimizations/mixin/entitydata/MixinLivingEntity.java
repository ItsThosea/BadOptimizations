package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends MixinEntity {
	@Shadow @Final protected static TrackedData<Byte> LIVING_FLAGS;
	@Unique private boolean isUsingItem = false;
	@Unique private Hand activeHand = Hand.MAIN_HAND;
	@Unique private boolean isUsingRiptide = false;

	@Shadow @Final private static TrackedData<Float> HEALTH;
	@Unique private float health = 1.0f;

	@Shadow @Final private static TrackedData<Integer> POTION_SWIRLS_COLOR;
	@Unique private int potionSwirlsColor = 0;

	@Shadow @Final private static TrackedData<Boolean> POTION_SWIRLS_AMBIENT;
	@Unique private boolean potionSwirlsAmbient = false;

	@Shadow @Final private static TrackedData<Integer> STUCK_ARROW_COUNT;
	@Unique private int stuckArrowCount = 0;

	@Shadow @Final private static TrackedData<Integer> STINGER_COUNT;
	@Unique private int stingerCount = 0;

	@Shadow @Final private static TrackedData<Optional<BlockPos>> SLEEPING_POSITION;
	@Unique private Optional<BlockPos> sleepingPosition = Optional.empty();

	@Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
	private void isUsingItem(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(isUsingItem);
	}

	@Inject(method = "getActiveHand", at = @At("HEAD"), cancellable = true)
	private void getActiveHand(CallbackInfoReturnable<Hand> cir) {
		cir.setReturnValue(activeHand);
	}

	@Inject(method = "isUsingRiptide", at = @At("HEAD"), cancellable = true)
	private void isUsingRiptide(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(isUsingRiptide);
	}

	@Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
	private void getHealth(CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(health);
	}

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 0))
	private Object onGetPotionSwirlsColor(DataTracker instance, TrackedData<Integer> data) {
		return potionSwirlsColor;
	}

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 1))
	private Object onGetPotionSwirlsAmbient(DataTracker instance, TrackedData<Boolean> data) {
		return potionSwirlsAmbient;
	}

	@Inject(method = "getStuckArrowCount", at = @At("HEAD"), cancellable = true)
	private void getStuckArrowCount(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(stuckArrowCount);
	}

	@Inject(method = "getStingerCount", at = @At("HEAD"), cancellable = true)
	private void getStingerCount(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(stingerCount);
	}

	@Inject(method = "getSleepingPosition", at = @At("HEAD"), cancellable = true)
	private void getSleepingPosition(CallbackInfoReturnable<Optional<BlockPos>> cir) {
		cir.setReturnValue(sleepingPosition);
	}

	@Override
	public void badoptimizations$refreshEntityData(TrackedData<?> data) {
		super.badoptimizations$refreshEntityData(data);

		DataTracker dataTracker = getDataTracker();

		if(LIVING_FLAGS.equals(data)) {
			isUsingItem = (dataTracker.get(LIVING_FLAGS) & 1) > 0;
			activeHand = (dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			isUsingRiptide = (dataTracker.get(LIVING_FLAGS) & 4) != 0;
		} else if(HEALTH.equals(data)) {
			health = dataTracker.get(HEALTH);
		} else if(POTION_SWIRLS_COLOR.equals(data)) {
			potionSwirlsColor = dataTracker.get(POTION_SWIRLS_COLOR);
		} else if(POTION_SWIRLS_AMBIENT.equals(data)) {
			potionSwirlsAmbient = dataTracker.get(POTION_SWIRLS_AMBIENT);
		} else if(STUCK_ARROW_COUNT.equals(data)) {
			stuckArrowCount = dataTracker.get(STUCK_ARROW_COUNT);
		} else if(STINGER_COUNT.equals(data)) {
			stingerCount = dataTracker.get(STINGER_COUNT);
		} else if(SLEEPING_POSITION.equals(data)) {
			sleepingPosition = dataTracker.get(SLEEPING_POSITION);
		}
	}
}
