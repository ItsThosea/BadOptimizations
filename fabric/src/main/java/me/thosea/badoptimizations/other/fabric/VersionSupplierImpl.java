package me.thosea.badoptimizations.other.fabric;

import net.fabricmc.loader.api.FabricLoader;

public final class VersionSupplierImpl {
	private VersionSupplierImpl() {}

	public static String getVersion() {
		return FabricLoader.getInstance().getModContainer("badoptimizations")
				.map(mod -> mod.getMetadata().getVersion().getFriendlyString())
				.orElse("[unknown version]");
	}
}
