package me.thosea.badoptimizations.other;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

// Opens the config file and exits, generates one if not there
public final class BOConfigScreen extends Screen {
	private final Screen parent;

	public BOConfigScreen(Screen parent) {
		super(Text.empty());
		this.parent = parent;
	}

	@Override
	protected void init() {
		if(!Config.FILE.exists()) {
			try {
				Config.writeConfig();
			} catch(Exception e) {
				throw new RuntimeException("Failed to generate BadOptimizations config", e);
			}
		}
		Util.getOperatingSystem().open(Config.FILE);
		client.setScreen(parent);
	}
}