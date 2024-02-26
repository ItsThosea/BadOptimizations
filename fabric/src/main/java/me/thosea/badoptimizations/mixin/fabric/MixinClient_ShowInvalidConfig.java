package me.thosea.badoptimizations.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.thosea.badoptimizations.other.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;

// Forge moves these function calls into
// a lambda so we need two mixins.
@Mixin(MinecraftClient.class)
public class MixinClient_ShowInvalidConfig {
	// Targets both TitleScreen and BanScreen
	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	private void onSetScreen(MinecraftClient client, Screen screen, Operation<Void> original) {
		if(Config.error == null) {
			original.call(client, screen);
			return;
		}

		client.setScreen(makeBOWarnScreen(() -> original.call(client, screen)));
	}

	@WrapOperation(method = "method_45026", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ConnectScreen;connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V"))
	private void onQuickplayConnect(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, Operation<Void> original) {
		if(Config.error == null) {
			original.call(screen, client, address, info);
			return;
		}

		client.setScreen(makeBOWarnScreen(() -> {
			original.call(screen, client, address, info);
		}));
	}

	@Unique
	private Screen makeBOWarnScreen(Runnable onClose) {
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
				addButton(new ButtonWidget(
						this.width / 2 - 155 + 80, y + 25, 150, 20,
						Text.literal("Open log file"), button -> {
					Util.getOperatingSystem().open(new File("./logs/latest.log"));
				}));
			}
		};
	}
}
