package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.thosea.badoptimizations.other.VersionSupplier;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
	@ModifyReturnValue(method = "getLeftText", at = @At("RETURN"))
	private List<String> addBadOptimizationsText(List<String> list) {
		list.add("");
		list.add(VersionSupplier.F3_TEXT);
		return list;
	}
}
