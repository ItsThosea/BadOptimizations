package me.thosea.badoptimizations.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Entry;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.data.TrackedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

// All entity data is only accessed on one thread,
// so this mixin removes unnecessary locks.
@Mixin(DataTracker.class)
public abstract class MixinDataTracker {
	@Shadow @Final private Int2ObjectMap<Entry<?>> entries;
	@Shadow @Final private Entity trackedEntity;
	@Shadow private boolean dirty;

	@Shadow
	protected abstract <T> void copyToFrom(Entry<T> to, SerializedEntry<?> from);

//	@Unique private final Thread thread = Thread.currentThread();

	@Overwrite
	private <T> Entry<T> getEntry(TrackedData<T> key) {
//		checkThread();
		return (Entry<T>) entries.get(key.getId());
	}

	@Overwrite
	private <T> void addTrackedData(TrackedData<T> key, T value) {
//		checkThread();
		entries.put(key.getId(), new Entry<>(key, value));
	}

	@Overwrite
	@Nullable
	public List<SerializedEntry<?>> getChangedEntries() {
//		checkThread();
		List<SerializedEntry<?>> list = null;

		for(Entry<?> value : this.entries.values()) {
			if(!value.isUnchanged()) {
				if(list == null) {
					list = new ArrayList<>();
				}

				list.add(value.toSerialized());
			}
		}

		return list;
	}

	@Overwrite
	public void writeUpdatedEntries(List<SerializedEntry<?>> entries) {
//		checkThread();
		for(SerializedEntry<?> value : entries) {
			Entry<?> entry = this.entries.get(value.id());
			if(entry != null) {
				copyToFrom(entry, value);
				trackedEntity.onTrackedDataSet(entry.getData());
			}
		}

		trackedEntity.onDataTrackerUpdate(entries);
	}

	@Overwrite
	@Nullable
	public List<SerializedEntry<?>> getDirtyEntries() {
//		checkThread();
		List<SerializedEntry<?>> list = null;
		if(dirty) {
			for(Entry<?> value : this.entries.values()) {
				if(value.isDirty()) {
					value.setDirty(false);
					if(list == null) {
						list = new ArrayList<>();
					}

					list.add(value.toSerialized());
				}
			}
		}

		dirty = false;
		return list;
	}

	// @Unique
	// private void checkThread() {
//		if(thread != Thread.currentThread())
//			System.exit(69);
// 	}
}
