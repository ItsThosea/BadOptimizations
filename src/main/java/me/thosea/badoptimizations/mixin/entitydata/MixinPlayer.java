package me.thosea.badoptimizations.mixin.entitydata;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayer extends LivingEntity {
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

	@Overwrite
	public float getAbsorptionAmount() {
		return absorptionAmount;
	}

	@Overwrite
	public int getScore() {
		return score;
	}

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

	@Override
	@Overwrite
	public Arm getMainArm() {
		return mainArm;
	}

	@Overwrite
	public NbtCompound getShoulderEntityLeft() {
		return shoulderEntityLeft;
	}

	@Overwrite
	public NbtCompound getShoulderEntityRight() {
		return shoulderEntityRight;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);

		DataTracker dataTracker = getDataTracker();
		if(ABSORPTION_AMOUNT.equals(data)) {
			absorptionAmount = getDataTracker().get(ABSORPTION_AMOUNT);
		} else if(SCORE.equals(data)) {
			score = dataTracker.get(SCORE);
		} else if(PLAYER_MODEL_PARTS.equals(data)) {
			isCapeEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 1) == 1;
			isJacketEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 2) == 2;
			isLeftSleeveEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 4) == 4;
			isRightSleeveEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 8) == 8;
			isLeftPantsLegEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 16) == 16;
			isRightPantsLegEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 32) == 32;
			isHatEnabled = (dataTracker.get(PLAYER_MODEL_PARTS) & 64) == 64;
		} else if(MAIN_ARM.equals(data)) {
			mainArm = (byte) dataTracker.get(data) == 0 ? Arm.LEFT : Arm.RIGHT;
		} else if(LEFT_SHOULDER_ENTITY.equals(data)) {
			shoulderEntityLeft = dataTracker.get(LEFT_SHOULDER_ENTITY);
		} else if(RIGHT_SHOULDER_ENTITY.equals(data)) {
			shoulderEntityRight = dataTracker.get(RIGHT_SHOULDER_ENTITY);
		}
	}

	@Shadow
	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Shadow
	public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

	@Shadow
	public abstract Iterable<ItemStack> getArmorItems();

	protected MixinPlayer(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

}
