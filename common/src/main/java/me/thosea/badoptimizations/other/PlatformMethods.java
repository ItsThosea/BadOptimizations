package me.thosea.badoptimizations.other;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.io.File;

public final class PlatformMethods {
	private PlatformMethods() {}

	@ExpectPlatform
	public static String getVersion() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static File getConfigFolder() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isModLoaded(String id) {
		throw new AssertionError();
	}
}