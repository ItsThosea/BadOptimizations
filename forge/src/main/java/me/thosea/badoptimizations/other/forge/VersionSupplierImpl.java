package me.thosea.badoptimizations.other.forge;

import net.minecraftforge.fml.ModList;

public final class VersionSupplierImpl {
	private VersionSupplierImpl() {}

	public static String getVersion() {
		return ModList.get().getModContainerById("badoptimizations")
				.map(mod -> mod.getModInfo().getVersion().toString())
				.orElse("[unknown version]");
	}
}
