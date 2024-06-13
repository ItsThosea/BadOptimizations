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

@Mixin(value = PlayerEntity.class, priority = 700)
public abstract class MixinPlayer extends MixinEntity {
	@Shadow @Final private static TrackedData<Float> ABSORPTION_AMOUNT;
	@Shadow @Final protected static TrackedData<NbtCompound> LEFT_SHOULDER_ENTITY;
	@Shadow @Final protected static TrackedData<NbtCompound> RIGHT_SHOULDER_ENTITY;
	@Shadow @Final protected static TrackedData<Byte> PLAYER_MODEL_PARTS;
	@Shadow @Final private static TrackedData<Integer> SCORE;
	@Shadow @Final protected static TrackedData<Byte> MAIN_ARM;

	private boolean bo$isCapeEnabled = false;
	private boolean bo$isJacketEnabled = false;
	private boolean bo$isLeftSleeveEnabled = false;
	private boolean bo$isRightSleeveEnabled = false;
	private boolean bo$isLeftPantsLegEnabled = false;
	private boolean bo$isRightPantsLegEnabled = false;
	private boolean bo$isHatEnabled = false;
	private Arm bo$mainArm = Arm.RIGHT;
	private float bo$absorptionAmount = 0f;
	private int bo$score = 0;

	private NbtCompound bo$shoulderEntityLeft = new NbtCompound();
	private NbtCompound bo$shoulderEntityRight = new NbtCompound();

	@Overwrite
	public boolean isPartVisible(PlayerModelPart modelPart) {
		return switch(modelPart) {
			case CAPE -> bo$isCapeEnabled;
			case JACKET -> bo$isJacketEnabled;
			case LEFT_SLEEVE -> bo$isLeftSleeveEnabled;
			case RIGHT_SLEEVE -> bo$isRightSleeveEnabled;
			case LEFT_PANTS_LEG -> bo$isLeftPantsLegEnabled;
			case RIGHT_PANTS_LEG -> bo$isRightPantsLegEnabled;
			case HAT -> bo$isHatEnabled;
		};
	}

	@Overwrite public float getAbsorptionAmount() {return bo$absorptionAmount;}
	@Overwrite public int getScore() {return bo$score;}
	@Overwrite public Arm getMainArm() {return bo$mainArm;}
	@Overwrite public NbtCompound getShoulderEntityLeft() {return bo$shoulderEntityLeft;}
	@Overwrite public NbtCompound getShoulderEntityRight() {return bo$shoulderEntityRight;}

	@Override
	public void bo$refreshEntityData(int data) {
		super.bo$refreshEntityData(data);

		if(data == ABSORPTION_AMOUNT.getId()) {
			bo$absorptionAmount = dataTracker.get(ABSORPTION_AMOUNT);
		} else if(data == SCORE.getId()) {
			bo$score = dataTracker.get(SCORE);
		} else if(data == PLAYER_MODEL_PARTS.getId()) {
			byte parts = dataTracker.get(PLAYER_MODEL_PARTS);

			bo$isCapeEnabled = (parts & 1) == 1;
			bo$isJacketEnabled = (parts & 2) == 2;
			bo$isLeftSleeveEnabled = (parts & 4) == 4;
			bo$isRightSleeveEnabled = (parts & 8) == 8;
			bo$isLeftPantsLegEnabled = (parts & 16) == 16;
			bo$isRightPantsLegEnabled = (parts & 32) == 32;
			bo$isHatEnabled = (parts & 64) == 64;
		} else if(data == MAIN_ARM.getId()) {
			bo$mainArm = dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
		} else if(data == LEFT_SHOULDER_ENTITY.getId()) {
			bo$shoulderEntityLeft = dataTracker.get(LEFT_SHOULDER_ENTITY);
		} else if(data == RIGHT_SHOULDER_ENTITY.getId()) {
			bo$shoulderEntityRight = dataTracker.get(RIGHT_SHOULDER_ENTITY);
		}
	}
}
