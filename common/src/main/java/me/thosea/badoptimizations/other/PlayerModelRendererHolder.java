package me.thosea.badoptimizations.other;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

public final class PlayerModelRendererHolder {
	private PlayerModelRendererHolder() {}

	public static EntityRenderer<? extends PlayerEntity, ?> WIDE_RENDERER;
	public static EntityRenderer<? extends PlayerEntity, ?> SLIM_RENDERER;
}