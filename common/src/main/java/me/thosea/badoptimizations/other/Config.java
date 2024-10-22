package me.thosea.badoptimizations.other;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@SuppressWarnings("unused")
public final class Config {
	private Config() {}

	public static final Logger LOGGER = LoggerFactory.getLogger("BadOptimizations");
	public static final File FILE = new File(PlatformMethods.getConfigFolder(), "badoptimizations.txt");
	public static final int CONFIG_VER = 3;

	@Nullable
	public static String error;

	public static boolean enable_lightmap_caching = true;
	public static int lightmap_time_change_needed_for_update = 80;

	public static boolean enable_sky_color_caching = true;
	public static int skycolor_time_change_needed_for_update = 3;

	public static boolean enable_debug_renderer_disable_if_not_needed = true;
	public static boolean enable_particle_manager_optimization = true;
	public static boolean enable_toast_optimizations = true;
	public static boolean enable_sky_angle_caching_in_worldrenderer = true;
	public static boolean enable_entity_renderer_caching = true;
	public static boolean enable_block_entity_renderer_caching = true;
	public static boolean enable_entity_flag_caching = true;
	public static boolean enable_remove_redundant_fov_calculations = true;
	public static boolean enable_remove_tutorial_if_not_demo = true;

	public static boolean show_f3_text = true;
	public static boolean ignore_mod_incompatibilities = false;
	public static boolean log_config = true;

	public static void load() {
		if(FILE.exists()) {
			LOGGER.info("Loading config from {}", FILE);

			try {
				loadConfig();
			} catch(Exception e) {
				error = """
						BadOptimizations failed to load the config,
						and some config options have not been read properly.
						Read the game log for details.""";
				LOGGER.error("Failed to load config from " + FILE + ". " +
						"If you need to, you can delete the file to generate a new one.", e);
			}
		} else {
			LOGGER.info("Creating config file version {} at {}", CONFIG_VER, FILE);

			try {
				writeConfig();
			} catch(Exception e) {
				error = "BadOptimizations failed to write the default config."
						+ "\nRead the game log for details.";
				LOGGER.error("Failed to write default config to " + FILE, e);
			}
		}
	}

	private static void loadConfig() throws Exception {
		Properties prop = new Properties();

		try(FileInputStream stream = new FileInputStream(FILE)) {
			prop.load(stream);
		}

		int ver = num(prop, "config_version");
		if(ver > CONFIG_VER) {
			LOGGER.warn("Config version is newer than supported, this may cause issues" +
					" (supported: {}, found: {})", CONFIG_VER, ver);
		} else if(ver < CONFIG_VER) {
			LOGGER.info("Upgrading config from version {} to supported version {}", ver, CONFIG_VER);
		} else {
			LOGGER.info("Config version: {}", CONFIG_VER);
		}

		lightmap_time_change_needed_for_update = num(prop, "lightmap_time_change_needed_for_update");
		enable_lightmap_caching = bool(prop, "enable_lightmap_caching")
				&& lightmap_time_change_needed_for_update > 1;

		enable_sky_color_caching = bool(prop, "enable_sky_color_caching");
		skycolor_time_change_needed_for_update = num(prop, "skycolor_time_change_needed_for_update");

		enable_debug_renderer_disable_if_not_needed = bool(prop, "enable_debug_renderer_disable_if_not_needed");
		enable_particle_manager_optimization = bool(prop, "enable_particle_manager_optimization");
		enable_toast_optimizations = bool(prop, "enable_toast_optimizations");
		enable_sky_angle_caching_in_worldrenderer = bool(prop, "enable_sky_angle_caching_in_worldrenderer");
		enable_entity_renderer_caching = bool(prop, "enable_entity_renderer_caching");
		enable_block_entity_renderer_caching = bool(prop, "enable_block_entity_renderer_caching");
		enable_entity_flag_caching = bool(prop, "enable_entity_flag_caching");
		enable_remove_redundant_fov_calculations = bool(prop, "enable_remove_redundant_fov_calculations");
		enable_remove_tutorial_if_not_demo = bool(prop, "enable_remove_tutorial_if_not_demo");

		show_f3_text = bool(prop, "show_f3_text");

		if(ver >= 2) {
			// Config version 2 (v2.1.1)
			ignore_mod_incompatibilities = bool(prop, "ignore_mod_incompatibilities");
			log_config = bool(prop, "log_config");
		}

		// Config version 3 (v2.1.2) removed the fps string optimization, nothing special to do

		if(ver < CONFIG_VER) {
			writeConfig();
			loadConfig();
			return;
		}

		if(log_config) {
			LOGGER.info("BadOptimizations config dump:");
			prop.forEach((key, value) -> {
				LOGGER.info("{}: {}", key, value);
			});
		}

		if(!ignore_mod_incompatibilities) {
			if(enable_entity_renderer_caching) {
				if(PlatformMethods.isModLoaded("twilightforest")) {
					enable_entity_renderer_caching = false;
					LOGGER.info("Disabled entity_renderer_caching because Twilight Forest is present");
				} else if(PlatformMethods.isModLoaded("aquaculture")) {
					enable_entity_renderer_caching = false;
					LOGGER.info("Disabled entity_renderer_caching because Aquaculture is present");
				}
			}

			if(enable_sky_color_caching) {
				if(PlatformMethods.isModLoaded("polytone")) {
					enable_sky_color_caching = false;
					LOGGER.info("Disabled sky_color_caching because Polytone is present");
				}
			}
		}
	}

	private static boolean bool(Properties prop, String name) throws Exception {
		String str = prop.getProperty(name);

		if(str == null) {
			throw new Exception("Config option " + name + " not found.");
		}

		if(str.equalsIgnoreCase("true")) {
			return true;
		} else if(str.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new Exception("Config option " + name + " is not \"true\" or \"false\" (\"" + str + "\").");
		}
	}

	private static int num(Properties prop, String name) throws Exception {
		String str = prop.getProperty(name);

		if(str == null) {
			throw new Exception("Config option " + name + " not found.");
		}

		int result;

		try {
			result = Integer.parseInt(str);
		} catch(Exception e) {
			throw new Exception("Config option " + name + " is not a valid number (\"" + str + "\").");
		}

		if(result < 0) {
			throw new Exception("Config option " + name + " is negative (" + str + ")");
		}

		return result;
	}

	private static void writeConfig() throws Exception {
		File parent = FILE.getParentFile();
		if(!parent.exists()) {
			if(!parent.mkdirs()) {
				throw new Exception("Failed to create config directory at " + parent);
			}
		}

		String data =
				"""
						# BadOptimizations configuration
						# Here you can configure stuff, mostly enabling/disabling specific optimizations.

						# Whether we should cancel updating the lightmap if not needed.
						enable_lightmap_caching: %s
						# How much the in-game time must change in ticks (1/20th of a second)
						# for the lightmap to immediately update.
						# Higher values will result in less frequent updates
						# to block lighting, but better performance.
						# Values below 2 will disable hte optimization.
						lightmap_time_change_needed_for_update: %s
										
						# Whether the sky's color should be cached unless you're on a biome border.
						enable_sky_color_caching: %s
						# How much the in-game time must change in ticks for the sky color to
						# be recalculated with our own calculation. Higher values will result in
						# the sky updating less frequently, but slightly better performance.
						# Values below 2 will all have the same effect.
						skycolor_time_change_needed_for_update: %s
						
						# Whether we should avoid calling debug renderers
						# and their calculations if there are no debug entries to render
						enable_debug_renderer_disable_if_not_needed: %s
										
						#
						# Micro optimizations
						#
										
						# Whether we should avoid calling the particle manager
						# and its calculations if there are no particles.
						enable_particle_manager_optimization: %s
						# Whether we should avoid calling the toast manager
						# if there are no toasts
						enable_toast_optimizations: %s
						# Whether the result of getSkyAngle should be cached
						# for the entire frame during rendering
						enable_sky_angle_caching_in_worldrenderer: %s
						# Whether entity renderers should be stored directly in EntityType
						# instead of a HashMap.
						# Disable to fix compatibility with Twilight Forest
						enable_entity_renderer_caching: %s
						# Whether block entity renderers should be stored in BlockEntityType
						# instead of a HashMap.
						enable_block_entity_renderer_caching: %s
						# Whether entity flags should be cached instead of
						# calling DataTracker.
						# Also removes the unnecessary thread lock in DataTracker
						# however this is also done by Lithium (they don't conflict, however).
						enable_entity_flag_caching: %s
						# Whether we should avoid calling FOV calculations
						# if the FOV effect scale is zero.
						enable_remove_redundant_fov_calculations: %s
						# Don't tick the tutorial if the game is not in demo mode.
						enable_remove_tutorial_if_not_demo: %s
						
						#
						# Other
						#
						
						# Whether BadOptimizations <version> should be added onto
						# the left text of the F3 menu.
						show_f3_text: %s
						
						# Some config options will be force-disabled if certain mods are present
						# due to incompatibilities (e.g. entity rendering caching
						# is disabled w/ Twilight Forest).
						# However, if you still want to use the optimizations, you can override it
						# by setting this to true. Beware of crashes. And Herobrine.
						ignore_mod_incompatibilities: %s
						
						# Whether to log the entire config into console when booting up.
						# If you plan on reporting an issue, please keep this on.
						log_config: %s
						
						# Do not change this
						config_version: %s
						""".formatted(
						enable_lightmap_caching,
						lightmap_time_change_needed_for_update,
						enable_sky_color_caching,
						skycolor_time_change_needed_for_update,
						enable_debug_renderer_disable_if_not_needed,
						enable_particle_manager_optimization,
						enable_toast_optimizations,
						enable_sky_angle_caching_in_worldrenderer,
						enable_entity_renderer_caching,
						enable_block_entity_renderer_caching,
						enable_entity_flag_caching,
						enable_remove_redundant_fov_calculations,
						enable_remove_tutorial_if_not_demo,
						show_f3_text,
						ignore_mod_incompatibilities,
						log_config,
						CONFIG_VER
				);

		if(FILE.exists()) FILE.delete();
		Files.writeString(FILE.toPath(), data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}
}