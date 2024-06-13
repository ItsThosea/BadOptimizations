package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.thosea.badoptimizations.other.PlatformMethods;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 999)
public class MixinDebugHud_AddText {
	private static final String BO$F3_TEXT = "BadOptimizations " + PlatformMethods.getVersion();

	@ModifyReturnValue(method = "getLeftText", at = @At("RETURN"))
	private List<String> addBadOptimizationsText(List<String> list) {
		list.add("");
		list.add(BO$F3_TEXT);
		return list;
	}
}
