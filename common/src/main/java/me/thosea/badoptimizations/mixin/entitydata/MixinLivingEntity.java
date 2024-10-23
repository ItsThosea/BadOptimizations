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

	private boolean bo$isUsingItem = false;
	private boolean bo$potionSwirlsAmbient = false;
	private boolean bo$isUsingRiptide = false;
	private Hand bo$activeHand = Hand.MAIN_HAND;
	private float bo$health = 1.0f;
	private int bo$potionSwirlsColor = 0;
	private int bo$stuckArrowCount = 0;
	private int bo$stingerCount = 0;
	private Optional<BlockPos> bo$sleepingPosition = Optional.empty();

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 0))
	private Object onGetPotionSwirlsColor(DataTracker instance, TrackedData<Integer> data) {
		return bo$potionSwirlsColor;
	}

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 1))
	private Object onGetPotionSwirlsAmbient(DataTracker instance, TrackedData<Boolean> data) {
		return bo$potionSwirlsAmbient;
	}

	@Overwrite public boolean isUsingItem() {return bo$isUsingItem;}
	@Overwrite public Hand getActiveHand() {return bo$activeHand;}
	@Overwrite public boolean isUsingRiptide() {return bo$isUsingRiptide;}
	@Overwrite public float getHealth() {return bo$health;}
	@Overwrite public final int getStuckArrowCount() {return bo$stuckArrowCount;}
	@Overwrite public final int getStingerCount() {return bo$stingerCount;}
	@Overwrite public Optional<BlockPos> getSleepingPosition() {return bo$sleepingPosition;}

	@Override
	public void bo$refreshEntityData(int data) {
		super.bo$refreshEntityData(data);

		if(data == LIVING_FLAGS.getId()) {
			bo$isUsingItem = (dataTracker.get(LIVING_FLAGS) & 1) > 0;
			bo$activeHand = (dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			bo$isUsingRiptide = (dataTracker.get(LIVING_FLAGS) & 4) != 0;
		} else if(data == HEALTH.getId()) {
			bo$health = dataTracker.get(HEALTH);
		} else if(data == POTION_SWIRLS_COLOR.getId()) {
			bo$potionSwirlsColor = dataTracker.get(POTION_SWIRLS_COLOR);
		} else if(data == POTION_SWIRLS_AMBIENT.getId()) {
			bo$potionSwirlsAmbient = dataTracker.get(POTION_SWIRLS_AMBIENT);
		} else if(data == STUCK_ARROW_COUNT.getId()) {
			bo$stuckArrowCount = dataTracker.get(STUCK_ARROW_COUNT);
		} else if(data == STINGER_COUNT.getId()) {
			bo$stingerCount = dataTracker.get(STINGER_COUNT);
		} else if(data == SLEEPING_POSITION.getId()) {
			bo$sleepingPosition = dataTracker.get(SLEEPING_POSITION);
		}
	}
}