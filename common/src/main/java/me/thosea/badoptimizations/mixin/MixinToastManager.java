package me.thosea.badoptimizations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("Java8CollectionRemoveIf")
@Mixin(value = ToastManager.class, priority = 800)
public abstract class MixinToastManager {
	// List<Entry<?>> but i dont want to make an
	// accesswidener just for Entry when I don't need to
	@Shadow @Final private List<Object> visibleEntries;
	@Shadow @Final private Deque<Toast> toastQueue;

	// Don't do anything if we don't need to
	@ModifyExpressionValue(method = "draw", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;hudHidden:Z"))
	private boolean shouldSkipDraw(boolean hudHidden) {
		return hudHidden || (visibleEntries.isEmpty() && toastQueue.isEmpty());
	}

	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ljava/util/List;removeIf(Ljava/util/function/Predicate;)Z"))
	private boolean onVisibleEntiresRemoveIf(List<Object> list, Predicate<Object> predicate) {
		// actually List<Entry<?>> and Predicate<Entry<?>>

		// removeIf sometimes will call the
		// predicate twice per entry on ArrayList,
		// which is especially not good cause this
		// predicate draws stuff

		Iterator<Object> iterator = list.iterator();

		while(iterator.hasNext()) {
			if(predicate.test(iterator.next())) {
				iterator.remove();
			}
		}

		return false; // unused
	}

	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ljava/util/Deque;removeIf(Ljava/util/function/Predicate;)Z"))
	private boolean onToastQueueRemoveIf(Deque<Object> queue, Predicate<Object> predicate) {
		// Deque<Toast> and Predicate<Toast>

		Iterator<Object> iterator = queue.iterator();

		while(iterator.hasNext()) {
			if(predicate.test(iterator.next())) {
				iterator.remove();
			}
		}

		return false; // unused
	}
}
