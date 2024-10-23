package me.thosea.badoptimizations.fabric.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.thosea.badoptimizations.other.BOConfigScreen;

public class BadOptimizationsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return BOConfigScreen::new;
	}
}