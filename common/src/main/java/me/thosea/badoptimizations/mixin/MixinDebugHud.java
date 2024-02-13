package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.thosea.badoptimizations.interfaces.ClientMethods;
import me.thosea.badoptimizations.other.VersionSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
	@Unique private ClientMethods clientMethods;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(MinecraftClient client, CallbackInfo ci) {
		this.clientMethods = (ClientMethods) client;
	}

	@Inject(method = "toggleDebugHud", at = @At("TAIL"))
	private void renderInject(CallbackInfo ci) {
		if(shouldShowDebugHud()) {
			clientMethods.bo$updateFpsString();
		}
	}

	@ModifyReturnValue(method = "getLeftText", at = @At("RETURN"))
	private List<String> addBadOptimizationsText(List<String> list) {
		list.add("");
		list.add(VersionSupplier.F3_TEXT);
		return list;
	}

	@Shadow public abstract boolean shouldShowDebugHud();
}
