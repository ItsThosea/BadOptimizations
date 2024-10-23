package me.thosea.badoptimizations.interfaces;

import net.minecraft.world.biome.source.BiomeAccess;

@FunctionalInterface
public interface BiomeSkyColorGetter {
	int get(int biomeX, int biomeY, int biomeZ);

	static BiomeSkyColorGetter of(BiomeAccess access) {
		return (x, y, z) -> {
			return access.getBiomeForNoiseGen(x, y, z).value().getSkyColor();
		};
	}
}