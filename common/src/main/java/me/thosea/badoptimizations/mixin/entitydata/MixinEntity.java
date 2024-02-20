package me.thosea.badoptimizations.mixin.entitydata;

import me.thosea.badoptimizations.interfaces.EntityMethods;
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
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = Entity.class, priority = 700)
public abstract class MixinEntity implements EntityMethods {
	@Shadow @Final private static TrackedData<Optional<Text>> CUSTOM_NAME;
	@Shadow @Final protected static TrackedData<Byte> FLAGS;
	@Shadow @Final private static TrackedData<Boolean> NAME_VISIBLE;
	@Shadow @Final private static TrackedData<Boolean> SILENT;
	@Shadow @Final private static TrackedData<Boolean> NO_GRAVITY;
	@Shadow @Final protected static TrackedData<EntityPose> POSE;
	@Shadow @Final private static TrackedData<Integer> FROZEN_TICKS;
	@Shadow @Final private static TrackedData<Integer> AIR;

	@Shadow private World world;

	@Unique private boolean glowingBO;
	@Unique private boolean onFire = false;
	@Unique private boolean sneaking = false;
	@Unique private boolean sprinting = false;
	@Unique private boolean swimming = false;
	@Unique private boolean invisible = false;
	@Unique private boolean nameVisible = false;
	@Unique private boolean silent = false;
	@Unique private boolean noGravity = false;
	@Unique	private int frozenTicks = 0;
	@Unique private EntityPose pose = EntityPose.STANDING;
	@Unique private Optional<Text> customName = Optional.empty();
	@Unique private int remainingAirTicks = getMaxAir();

	@Shadow public abstract int getMaxAir();

	@Redirect(method = "isOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getFlag(I)Z"))
	private boolean getIsOnFire(Entity instance, int index) {
		return onFire;
	}

	@Shadow private boolean glowing; // vanilla glowing on the server

	@Overwrite public boolean isGlowing() {return world.isClient ? glowingBO : glowing;}
	@Overwrite public boolean isSneaking() {return sneaking;}
	@Overwrite public boolean isSprinting() {return sprinting;}
	@Overwrite public boolean isSwimming() {return swimming;}
	@Overwrite public boolean isInvisible() {return invisible;}
	@Overwrite public int getAir() {return remainingAirTicks;}
	@Overwrite @Nullable public Text getCustomName() {return customName.orElse(null);}
	@Overwrite public boolean hasCustomName() {return customName.isPresent();}
	@Overwrite public boolean isCustomNameVisible() {return nameVisible;}
	@Overwrite public boolean isSilent() {return silent;}
	@Overwrite public boolean hasNoGravity() {return noGravity;}
	@Overwrite public EntityPose getPose() {return pose;}
	@Overwrite public int getFrozenTicks() {return frozenTicks;}

	@Shadow @Final protected DataTracker dataTracker;

	@Override
	public void bo$refreshEntityData(int data) {
		if(data == FLAGS.getId()) {
			byte flags = dataTracker.get(FLAGS);

			onFire = getFlag(flags, 0);
			sneaking = getFlag(flags, 1);
			sprinting = getFlag(flags, 3);
			swimming = getFlag(flags, 4);
			invisible = getFlag(flags, 5);
			if(world.isClient) {
				glowingBO = getFlag(flags, 6);
			}
		} else if(data == AIR.getId()) {
			remainingAirTicks = dataTracker.get(AIR);
		} else if(data == CUSTOM_NAME.getId()) {
			customName = dataTracker.get(CUSTOM_NAME);
		} else if(data == NAME_VISIBLE.getId()) {
			nameVisible = dataTracker.get(NAME_VISIBLE);
		} else if(data == SILENT.getId()) {
			silent = dataTracker.get(SILENT);
		} else if(data == NO_GRAVITY.getId()) {
			noGravity = dataTracker.get(NO_GRAVITY);
		} else if(data == POSE.getId()) {
			pose = dataTracker.get(POSE);
		} else if(data == FROZEN_TICKS.getId()) {
			frozenTicks = dataTracker.get(FROZEN_TICKS);
		}
	}

	@Unique
	private boolean getFlag(byte flags, int index) {
		return (flags & 1 << index) != 0;
	}
}
