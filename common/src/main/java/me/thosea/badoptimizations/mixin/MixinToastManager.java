package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Deque;
import java.util.List;

@Mixin(ToastManager.class)
public abstract class MixinToastManager {
	// List<Entry<?>> but i dont want to make an
	// accesswidener just for Entry when I'm just checking if it's empty'
	@Shadow @Final private List<Object> visibleEntries;
	@Shadow @Final private Deque<Toast> toastQueue;

	// Don't do anything if we don't need to
	@ModifyExpressionValue(method = "draw", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;hudHidden:Z"))
	private boolean shouldSkipDraw(boolean hudHidden) {
		return hudHidden || (visibleEntries.isEmpty() && toastQueue.isEmpty());
	}
}
