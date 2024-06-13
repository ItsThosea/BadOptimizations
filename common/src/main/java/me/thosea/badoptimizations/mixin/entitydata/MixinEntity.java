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

	private boolean bo$glowingClient;
	private boolean bo$onFire = false;
	private boolean bo$sneaking = false;
	private boolean bo$sprinting = false;
	private boolean bo$swimming = false;
	private boolean bo$invisible = false;
	private boolean bo$nameVisible = false;
	private boolean bo$silent = false;
	private boolean bo$noGravity = false;
	private int bo$frozenTicks = 0;
	private EntityPose bo$pose = EntityPose.STANDING;
	private Optional<Text> bo$customName = Optional.empty();
	private int bo$remainingAirTicks = getMaxAir();

	@Shadow public abstract int getMaxAir();

	@Redirect(method = "isOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getFlag(I)Z"))
	private boolean getIsOnFire(Entity instance, int index) {
		return bo$onFire;
	}

	@Shadow private boolean glowing; // vanilla glowing on the server

	@Overwrite public boolean isGlowing() {return world.isClient ? bo$glowingClient : glowing;}
	@Overwrite public boolean isSneaking() {return bo$sneaking;}
	@Overwrite public boolean isSprinting() {return bo$sprinting;}
	@Overwrite public boolean isSwimming() {return bo$swimming;}
	@Overwrite public boolean isInvisible() {return bo$invisible;}
	@Overwrite public int getAir() {return bo$remainingAirTicks;}
	@Overwrite @Nullable public Text getCustomName() {return bo$customName.orElse(null);}
	@Overwrite public boolean hasCustomName() {return bo$customName.isPresent();}
	@Overwrite public boolean isCustomNameVisible() {return bo$nameVisible;}
	@Overwrite public boolean isSilent() {return bo$silent;}
	@Overwrite public boolean hasNoGravity() {return bo$noGravity;}
	@Overwrite public EntityPose getPose() {return bo$pose;}
	@Overwrite public int getFrozenTicks() {return bo$frozenTicks;}

	@Shadow @Final protected DataTracker dataTracker;

	@Override
	public void bo$refreshEntityData(int data) {
		if(data == FLAGS.getId()) {
			byte flags = dataTracker.get(FLAGS);

			bo$onFire = bo$getFlag(flags, 0);
			bo$sneaking = bo$getFlag(flags, 1);
			bo$sprinting = bo$getFlag(flags, 3);
			bo$swimming = bo$getFlag(flags, 4);
			bo$invisible = bo$getFlag(flags, 5);
			if(world.isClient) {
				bo$glowingClient = bo$getFlag(flags, 6);
			}
		} else if(data == AIR.getId()) {
			bo$remainingAirTicks = dataTracker.get(AIR);
		} else if(data == CUSTOM_NAME.getId()) {
			bo$customName = dataTracker.get(CUSTOM_NAME);
		} else if(data == NAME_VISIBLE.getId()) {
			bo$nameVisible = dataTracker.get(NAME_VISIBLE);
		} else if(data == SILENT.getId()) {
			bo$silent = dataTracker.get(SILENT);
		} else if(data == NO_GRAVITY.getId()) {
			bo$noGravity = dataTracker.get(NO_GRAVITY);
		} else if(data == POSE.getId()) {
			bo$pose = dataTracker.get(POSE);
		} else if(data == FROZEN_TICKS.getId()) {
			bo$frozenTicks = dataTracker.get(FROZEN_TICKS);
		}
	}

	private boolean bo$getFlag(byte flags, int index) {
		return (flags & 1 << index) != 0;
	}
}
