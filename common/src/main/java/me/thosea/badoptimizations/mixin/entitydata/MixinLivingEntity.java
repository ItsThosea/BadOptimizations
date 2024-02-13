package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = LivingEntity.class, priority = 700)
public abstract class MixinLivingEntity extends MixinEntity {
	@Shadow @Final protected static TrackedData<Byte> LIVING_FLAGS;
	@Shadow @Final private static TrackedData<Float> HEALTH;
	@Shadow @Final private static TrackedData<Integer> POTION_SWIRLS_COLOR;
	@Shadow @Final private static TrackedData<Boolean> POTION_SWIRLS_AMBIENT;
	@Shadow @Final private static TrackedData<Integer> STUCK_ARROW_COUNT;
	@Shadow @Final private static TrackedData<Integer> STINGER_COUNT;
	@Shadow @Final private static TrackedData<Optional<BlockPos>> SLEEPING_POSITION;

	@Unique private boolean isUsingItem = false;
	@Unique private boolean potionSwirlsAmbient = false;
	@Unique private boolean isUsingRiptide = false;
	@Unique private Hand activeHand = Hand.MAIN_HAND;
	@Unique private float health = 1.0f;
	@Unique private int potionSwirlsColor = 0;
	@Unique private int stuckArrowCount = 0;
	@Unique private int stingerCount = 0;
	@Unique private Optional<BlockPos> sleepingPosition = Optional.empty();

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 0))
	private Object onGetPotionSwirlsColor(DataTracker instance, TrackedData<Integer> data) {
		return potionSwirlsColor;
	}

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 1))
	private Object onGetPotionSwirlsAmbient(DataTracker instance, TrackedData<Boolean> data) {
		return potionSwirlsAmbient;
	}

	@Overwrite public boolean isUsingItem() {return isUsingItem;}
	@Overwrite public Hand getActiveHand() {return activeHand;}
	@Overwrite public boolean isUsingRiptide() {return isUsingRiptide;}
	@Overwrite public float getHealth() {return health;}
	@Overwrite public final int getStuckArrowCount() {return stuckArrowCount;}
	@Overwrite public final int getStingerCount() {return stingerCount;}
	@Overwrite public Optional<BlockPos> getSleepingPosition() {return sleepingPosition;}

	@Override
	public void bo$refreshEntityData(int data) {
		super.bo$refreshEntityData(data);

		if(data == LIVING_FLAGS.getId()) {
			isUsingItem = (dataTracker.get(LIVING_FLAGS) & 1) > 0;
			activeHand = (dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			isUsingRiptide = (dataTracker.get(LIVING_FLAGS) & 4) != 0;
		} else if(data == HEALTH.getId()) {
			health = dataTracker.get(HEALTH);
		} else if(data == POTION_SWIRLS_COLOR.getId()) {
			potionSwirlsColor = dataTracker.get(POTION_SWIRLS_COLOR);
		} else if(data == POTION_SWIRLS_AMBIENT.getId()) {
			potionSwirlsAmbient = dataTracker.get(POTION_SWIRLS_AMBIENT);
		} else if(data == STUCK_ARROW_COUNT.getId()) {
			stuckArrowCount = dataTracker.get(STUCK_ARROW_COUNT);
		} else if(data == STINGER_COUNT.getId()) {
			stingerCount = dataTracker.get(STINGER_COUNT);
		} else if(data == SLEEPING_POSITION.getId()) {
			sleepingPosition = dataTracker.get(SLEEPING_POSITION);
		}
	}
}
