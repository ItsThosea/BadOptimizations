package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.other.Config;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BOMixinPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {
		Config.load();
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixin) {
		mixin = mixin.substring("me.thosea.badoptimizations.mixin.".length());

		if(mixin.equals("MixinClient_ShowInvalidConfig")) {
			return Config.error != null;
		} else if(mixin.equals("tick.MixinLightmapManager") || mixin.equals("accessor.GameRendererAccessor")) {
			return Config.enable_lightmap_caching;
		} else if(mixin.equals("tick.MixinClientWorld")) {
			return Config.enable_sky_color_caching;
		} else if(mixin.startsWith("debug.")) {
			return Config.enable_debug_renderer_disable_if_not_needed;
		} else if(mixin.startsWith("fps_string.")) {
			return Config.enable_fps_string_optimization;
		} else if(mixin.equals("MixinParticleManager")) {
			return Config.enable_particle_manager_optimization;
		} else if(mixin.equals("MixinToastManager")) {
			return Config.enable_toast_optimizations;
		} else if(mixin.equals("MixinWorldRenderer")) {
			return Config.enable_sky_angle_caching_in_worldrenderer;
		} else if(mixin.startsWith("renderer.entity.")) {
			return Config.enable_entity_renderer_caching;
		} else if(mixin.startsWith("renderer.blockentity.")) {
			return Config.enable_block_entity_renderer_caching;
		} else if(mixin.equals("tick.MixinGameRenderer")) {
			return Config.enable_remove_redundant_fov_calculations;
		} else if(mixin.equals("tick.MixinTutorial")) {
			return Config.enable_remove_tutorial_if_not_demo;
		} else if(mixin.equals("MixinDebugHud_AddText")) {
			return Config.show_f3_text;
		}

		throw new RuntimeException("No config option for mixin " + mixin);
	}

	// blah
	@Override public String getRefMapperConfig() {return null;}
	@Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	@Override public List<String> getMixins() {return null;}
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
