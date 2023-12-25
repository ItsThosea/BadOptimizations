package me.thosea.badoptimizations;

import net.minecraft.client.MinecraftClient;

public interface ClientAccessor {
	void badoptimizations$updateFpsString();

	static boolean shouldUpdateFpsString(MinecraftClient client) {
		return client.options.debugEnabled && (!client.options.hudHidden || client.currentScreen != null);
	}
}
