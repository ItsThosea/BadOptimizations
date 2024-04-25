package me.thosea.badoptimizations.other.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public final class PlatformMethodsImpl {
	private PlatformMethodsImpl() {}

	public static String getVersion() {
		return FabricLoader.getInstance().getModContainer("badoptimizations")
				.map(mod -> mod.getMetadata().getVersion().getFriendlyString())
				.orElse("[unknown version]");
	}

	public static File getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir().toFile();
	}

	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}
}
