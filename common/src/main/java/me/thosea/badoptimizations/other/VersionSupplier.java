package me.thosea.badoptimizations.other;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class VersionSupplier {
	private VersionSupplier() {}

	public static final String F3_TEXT = "BadOptimizations " + getVersion();

	@ExpectPlatform
	private static String getVersion() {
		throw new AssertionError();
	}
}
