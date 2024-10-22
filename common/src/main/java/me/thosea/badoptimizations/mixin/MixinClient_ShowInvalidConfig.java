package me.thosea.badoptimizations.mixin;

import me.thosea.badoptimizations.other.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.List;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public class MixinClient_ShowInvalidConfig {
	@SuppressWarnings("DataFlowIssue") // only initialized if there is an error, see BOMixinPlugin
	@Inject(method = "createInitScreens", at = @At("RETURN"))
	private void afterCreateInitScreens(List<Function<Runnable, Screen>> list, CallbackInfo ci) {
		list.add(onClose -> {
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
		});
	}
}