package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PlayerEntity.class, priority = 700)
public abstract class MixinPlayer extends MixinEntity {
	@Shadow @Final private static TrackedData<Float> ABSORPTION_AMOUNT;
	@Shadow @Final protected static TrackedData<NbtCompound> LEFT_SHOULDER_ENTITY;
	@Shadow @Final protected static TrackedData<NbtCompound> RIGHT_SHOULDER_ENTITY;
	@Shadow @Final protected static TrackedData<Byte> PLAYER_MODEL_PARTS;
	@Shadow @Final private static TrackedData<Integer> SCORE;
	@Shadow @Final protected static TrackedData<Byte> MAIN_ARM;

	@Unique private boolean isCapeEnabled = false;
	@Unique private boolean isJacketEnabled = false;
	@Unique private boolean isLeftSleeveEnabled = false;
	@Unique private boolean isRightSleeveEnabled = false;
	@Unique private boolean isLeftPantsLegEnabled = false;
	@Unique private boolean isRightPantsLegEnabled = false;
	@Unique private boolean isHatEnabled = false;
	@Unique private Arm mainArm = Arm.RIGHT;
	@Unique private float absorptionAmount = 0f;
	@Unique private int score = 0;

	@Unique private NbtCompound shoulderEntityLeft = new NbtCompound();
	@Unique private NbtCompound shoulderEntityRight = new NbtCompound();

	@Overwrite
	public boolean isPartVisible(PlayerModelPart modelPart) {
		return switch(modelPart) {
			case CAPE -> isCapeEnabled;
			case JACKET -> isJacketEnabled;
			case LEFT_SLEEVE -> isLeftSleeveEnabled;
			case RIGHT_SLEEVE -> isRightSleeveEnabled;
			case LEFT_PANTS_LEG -> isLeftPantsLegEnabled;
			case RIGHT_PANTS_LEG -> isRightPantsLegEnabled;
			case HAT -> isHatEnabled;
		};
	}

	@Overwrite public float getAbsorptionAmount() {return absorptionAmount;}
	@Overwrite public int getScore() {return score;}
	@Overwrite public Arm getMainArm() {return mainArm;}
	@Overwrite public NbtCompound getShoulderEntityLeft() {return shoulderEntityLeft;}
	@Overwrite public NbtCompound getShoulderEntityRight() {return shoulderEntityRight;}

	@Override
	public void bo$refreshEntityData(int data) {
		super.bo$refreshEntityData(data);

		if(data == ABSORPTION_AMOUNT.getId()) {
			absorptionAmount = dataTracker.get(ABSORPTION_AMOUNT);
		} else if(data == SCORE.getId()) {
			score = dataTracker.get(SCORE);
		} else if(data == PLAYER_MODEL_PARTS.getId()) {
			byte parts = dataTracker.get(PLAYER_MODEL_PARTS);

			isCapeEnabled = (parts & 1) == 1;
			isJacketEnabled = (parts & 2) == 2;
			isLeftSleeveEnabled = (parts & 4) == 4;
			isRightSleeveEnabled = (parts & 8) == 8;
			isLeftPantsLegEnabled = (parts & 16) == 16;
			isRightPantsLegEnabled = (parts & 32) == 32;
			isHatEnabled = (parts & 64) == 64;
		} else if(data == MAIN_ARM.getId()) {
			mainArm = dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
		} else if(data == LEFT_SHOULDER_ENTITY.getId()) {
			shoulderEntityLeft = dataTracker.get(LEFT_SHOULDER_ENTITY);
		} else if(data == RIGHT_SHOULDER_ENTITY.getId()) {
			shoulderEntityRight = dataTracker.get(RIGHT_SHOULDER_ENTITY);
		}
	}
}
