package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
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

	@Overwrite
	public boolean isUsingItem() {
		return isUsingItem;
	}

	@Overwrite
	public Hand getActiveHand() {
		return activeHand;
	}

	@Overwrite
	public boolean isUsingRiptide() {
		return isUsingRiptide;
	}

	@Overwrite
	public float getHealth() {
		return health;
	}

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 0))
	private Object onGetPotionSwirlsColor(DataTracker instance, TrackedData<Integer> data) {
		return potionSwirlsColor;
	}

	@Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;get(Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;", ordinal = 1))
	private Object onGetPotionSwirlsAmbient(DataTracker instance, TrackedData<Boolean> data) {
		return potionSwirlsAmbient;
	}

	@Overwrite
	public final int getStuckArrowCount() {
		return stuckArrowCount;
	}

	@Overwrite
	public final int getStingerCount() {
		return stingerCount;
	}

	@Overwrite
	public Optional<BlockPos> getSleepingPosition() {
		return sleepingPosition;
	}

	@Redirect(method = "tickRiptide", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z", opcode = Opcodes.GETFIELD))
	private boolean onRiptideEnd(World instance) {
		return false;
	}

	@Inject(method = "onTrackedDataSet", at = @At("HEAD"))
	private void onDataSet(TrackedData<?> data, CallbackInfo ci) {
		DataTracker ourDataTracker = getDataTracker();

		if(LIVING_FLAGS.equals(data)) {
			isUsingItem = (ourDataTracker.get(LIVING_FLAGS) & 1) > 0;
			activeHand = (ourDataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			isUsingRiptide = (ourDataTracker.get(LIVING_FLAGS) & 4) != 0;
		} else if(HEALTH.equals(data)) {
			health = ourDataTracker.get(HEALTH);
		} else if(POTION_SWIRLS_COLOR.equals(data)) {
			potionSwirlsColor = ourDataTracker.get(POTION_SWIRLS_COLOR);
		} else if(POTION_SWIRLS_AMBIENT.equals(data)) {
			potionSwirlsAmbient = ourDataTracker.get(POTION_SWIRLS_AMBIENT);
		} else if(STUCK_ARROW_COUNT.equals(data)) {
			stuckArrowCount = ourDataTracker.get(STUCK_ARROW_COUNT);
		} else if(STINGER_COUNT.equals(data)) {
			stingerCount = ourDataTracker.get(STINGER_COUNT);
		} else if(SLEEPING_POSITION.equals(data)) {
			sleepingPosition = ourDataTracker.get(SLEEPING_POSITION);
		}
	}

	protected MixinLivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
}
