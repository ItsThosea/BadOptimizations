package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayer extends MixinEntity {
	@Shadow @Final private static TrackedData<Float> ABSORPTION_AMOUNT;
	@Unique private float absorptionAmount = 0f;

	@Shadow @Final private static TrackedData<Integer> SCORE;
	@Unique private int score = 0;

	@Shadow @Final protected static TrackedData<Byte> PLAYER_MODEL_PARTS;
	@Unique private boolean isCapeEnabled = false;
	@Unique private boolean isJacketEnabled = false;
	@Unique private boolean isLeftSleeveEnabled = false;
	@Unique private boolean isRightSleeveEnabled = false;
	@Unique private boolean isLeftPantsLegEnabled = false;
	@Unique private boolean isRightPantsLegEnabled = false;
	@Unique private boolean isHatEnabled = false;

	@Shadow @Final protected static TrackedData<Byte> MAIN_ARM;
	@Unique private Arm mainArm = Arm.RIGHT;

	@Shadow @Final protected static TrackedData<NbtCompound> LEFT_SHOULDER_ENTITY;
	@Unique private NbtCompound shoulderEntityLeft = new NbtCompound();

	@Shadow @Final protected static TrackedData<NbtCompound> RIGHT_SHOULDER_ENTITY;
	@Unique private NbtCompound shoulderEntityRight = new NbtCompound();

	@Inject(method = "getAbsorptionAmount", at = @At("HEAD"), cancellable = true)
	private void getAbsorptionAmount(CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(absorptionAmount);
	}

	@Inject(method = "getScore", at = @At("HEAD"), cancellable = true)
	private void getScore(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(score);
	}

	@Inject(method = "isPartVisible", at = @At("HEAD"), cancellable = true)
	private void isPartVisible(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(switch(modelPart) {
			case CAPE -> isCapeEnabled;
			case JACKET -> isJacketEnabled;
			case LEFT_SLEEVE -> isLeftSleeveEnabled;
			case RIGHT_SLEEVE -> isRightSleeveEnabled;
			case LEFT_PANTS_LEG -> isLeftPantsLegEnabled;
			case RIGHT_PANTS_LEG -> isRightPantsLegEnabled;
			case HAT -> isHatEnabled;
		});
	}

	@Inject(method = "getMainArm", at = @At("HEAD"), cancellable = true)
	private void getMainArm(CallbackInfoReturnable<Arm> cir) {
		cir.setReturnValue(mainArm);
	}

	@Inject(method = "getShoulderEntityLeft", at = @At("HEAD"), cancellable = true)
	private void getShoulderEntityLeft(CallbackInfoReturnable<NbtCompound> cir) {
		cir.setReturnValue(shoulderEntityLeft);
	}

	@Inject(method = "getShoulderEntityRight", at = @At("HEAD"), cancellable = true)
	private void getShoulderEntityRight(CallbackInfoReturnable<NbtCompound> cir) {
		cir.setReturnValue(shoulderEntityRight);
	}

	@Override
	public void badoptimizations$refreshEntityData(TrackedData<?> data) {
		super.badoptimizations$refreshEntityData(data);

		DataTracker dataTracker = getDataTracker();

		if(ABSORPTION_AMOUNT.equals(data)) {
			absorptionAmount = getDataTracker().get(ABSORPTION_AMOUNT);
		} else if(SCORE.equals(data)) {
			score = dataTracker.get(SCORE);
		} else if(PLAYER_MODEL_PARTS.equals(data)) {
			byte parts = dataTracker.get(PLAYER_MODEL_PARTS);

			isCapeEnabled = (parts & 1) == 1;
			isJacketEnabled = (parts & 2) == 2;
			isLeftSleeveEnabled = (parts & 4) == 4;
			isRightSleeveEnabled = (parts & 8) == 8;
			isLeftPantsLegEnabled = (parts & 16) == 16;
			isRightPantsLegEnabled = (parts & 32) == 32;
			isHatEnabled = (parts & 64) == 64;
		} else if(MAIN_ARM.equals(data)) {
			mainArm = (byte) dataTracker.get(data) == 0 ? Arm.LEFT : Arm.RIGHT;
		} else if(LEFT_SHOULDER_ENTITY.equals(data)) {
			shoulderEntityLeft = dataTracker.get(LEFT_SHOULDER_ENTITY);
		} else if(RIGHT_SHOULDER_ENTITY.equals(data)) {
			shoulderEntityRight = dataTracker.get(RIGHT_SHOULDER_ENTITY);
		}
	}
}
