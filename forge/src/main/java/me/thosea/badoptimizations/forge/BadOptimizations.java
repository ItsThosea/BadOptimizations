package me.thosea.badoptimizations.forge;

import me.thosea.badoptimizations.other.BOConfigScreen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory;

@Mod("badoptimizations")
public class BadOptimizations {
	public BadOptimizations() {
		ModLoadingContext.get().registerExtensionPoint(
				ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenFactory((mc, parent) -> new BOConfigScreen(parent))
		);
	}
}