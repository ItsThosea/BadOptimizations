package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow @Final protected static TrackedData<Byte> FLAGS;
	@Shadow private World world;

	@Shadow
	protected abstract boolean getFlag(int index);

	@Shadow
	public abstract boolean isFireImmune();

	@Shadow private boolean glowing; // serverside
	@Unique private boolean glowingClient;
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
	@Unique	private int frozenTicks = 0;

	@Redirect(method = "isOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getFlag(I)Z"))
	private boolean getIsOnFire(Entity instance, int index) {
		return onFire;
	}

	@Overwrite
	public boolean isSneaking() {
		return sneaking;
	}

	@Overwrite
	public boolean isSprinting() {
		return sprinting;
	}

	@Overwrite
	public boolean isSwimming() {
		return swimming;
	}

	@Overwrite
	public boolean isInvisible() {
		return invisible;
	}

	@Overwrite
	public boolean isGlowing() {
		return world.isClient ? glowingClient : glowing;
	}

	@Overwrite
	public int getAir() {
		return remainingAirTicks;
	}

	@Overwrite
	@Nullable
	public Text getCustomName() {
		return customName.orElse(null);
	}

	@Overwrite
	public boolean hasCustomName() {
		return customName.isPresent();
	}

	@Overwrite
	public boolean isCustomNameVisible() {
		return nameVisible;
	}

	@Overwrite
	public boolean isSilent() {
		return silent;
	}

	@Overwrite
	public boolean hasNoGravity() {
		return noGravity;
	}

	@Overwrite
	public EntityPose getPose() {
		return pose;
	}

	@Overwrite
	public int getFrozenTicks() {
		return frozenTicks;
	}

	@Inject(method = "onTrackedDataSet", at = @At("HEAD"))
	private void onDataSet(TrackedData<?> data, CallbackInfo ci) {
		DataTracker dataTracker = getDataTracker();

		if(FLAGS.equals(data)) {
			onFire = !isFireImmune() && getFlag(0);
			sneaking = getFlag(1);
			sprinting = getFlag(3);
			swimming = getFlag(4);
			invisible = getFlag(5);
			if(world.isClient)
				glowingClient = getFlag(6);
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
