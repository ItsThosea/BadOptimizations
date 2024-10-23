package me.thosea.badoptimizations.other;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.io.File;

// don't throw dummy exception so intellij doesn't mark code as unreachable
public final class PlatformMethods {
	private PlatformMethods() {}

	@ExpectPlatform
	public static String getVersion() {
		return "";
	}

	@ExpectPlatform
	public static File getConfigFolder() {
		return new File(".");
	}

	@ExpectPlatform
	public static boolean isModLoaded(String id) {
		return true;
	}
}