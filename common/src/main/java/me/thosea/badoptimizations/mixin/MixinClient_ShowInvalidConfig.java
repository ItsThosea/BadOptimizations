package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.thosea.badoptimizations.other.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs.QuickPlay;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.resource.ResourceReload;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;

@Mixin(MinecraftClient.class)
public class MixinClient_ShowInvalidConfig {
	// Title screen and accessibility screen
	@WrapOperation(method = "onInitFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	private void onSetScreen(MinecraftClient client, Screen screen, Operation<Void> original) {
		client.setScreen(bo$makeBOWarnScreen(() -> original.call(client, screen)));
	}

	@WrapOperation(method = "onInitFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/QuickPlay;startQuickPlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/RunArgs$QuickPlay;Lnet/minecraft/resource/ResourceReload;Lnet/minecraft/client/realms/RealmsClient;)V"))
	private void onStartQuickPlay(MinecraftClient client, QuickPlay quickPlay, ResourceReload reload, RealmsClient realms, Operation<Void> original) {
		client.setScreen(bo$makeBOWarnScreen(() -> {
			original.call(client, quickPlay, reload, realms);
		}));
	}

	private Screen bo$makeBOWarnScreen(Runnable onClose) {
		return new ConfirmScreen(yes -> {
			if(yes) { // continue
				onClose.run();
			} else { // open config file
				Util.getOperatingSystem().open(Config.FILE);
			}
		}, Text.empty(), Text.literal(Config.error),
				Text.literal("Continue"), Text.literal("Open config file")) {
			@Override
			protected void addButtons(int y) {
				super.addButtons(y);
				addButton(ButtonWidget.builder(Text.literal("Open log file"), button -> {
							Util.getOperatingSystem().open(new File("./logs/latest.log"));
						})
						.dimensions(this.width / 2 - 155 + 80, y + 25, 150, 20).build());
			}
		};
	}
}
