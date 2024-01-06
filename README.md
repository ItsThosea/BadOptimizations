## BadOptimizations

This mod started off optimizing minor things and only increasing performance by a tiny bit (hence the name), but later started doing some bigger optimizations.
Most optimization mods focus on rendering performance and optimizing Minecraft's render engine, but this mod's focus is on the things other than that. This mod is to be used in conjunction with other optimization mods like Sodium.<p>

## Wait, what does this even do?
It does multiple things:<p>

- **Avoid updating lightmap**<p>
  Updating lightmap textures are probably the most expensive part of the client tick. During each tick, the client will do some vector math to calculate lightmaps for blocks and the sky, then upload that new texture to the GPU. However, this can be completely avoided and cached if nothing affecting brightness is changed. This mod will cancel lightmap updates if nothing that changes brightness (e.g. gamma slider, potion effects) has changed. 
- **Avoid calculating cloud and sky colors**<p>
  Rather than calculating the current sky and cloud colors every frame, it will only be done every tick, and only will be done if something affecting it has changed.
- **Don't do F3 calculations if we're not in the F3 menu**<p>
  You'd be surprised to see how expensive the calculations are to F3, even when it's not open. Notably, the FPS string uses `String.format`, a very slow function call. This mod makes that function only get called if you're actually in the F3 menu. Don't worry, this won't break FPS counter mods.<p>*(also, you really shouldn't be using F3 often anyway, it decreases performance significantly, use an FPS counter mod.)*</p>
- **Remove unnecessary thread synchronization from DataTracker / SyncedEntityData**<p>
  Minecraft uses thread locks to make sure only one thread accesses DataTracker at a time, which uses (somewhat) expensive thread locks. These are completely redundant. I wrote a temporary script that would crash the game if more than one thread accesses an entity's DataTracker, ever. The game didn't crash at all.
- **Don't do unnecessary FOV calculations if we don't need to**<p>
  When you're spectating either a player or nobody, the game calculates your FOV factor (e.g. charging bow / potion effects), even if your FOV effect scale is zero. This mod removes this calculation if your FOV effect scale is zero.<p>
- **Replace `removeIf` call in ToastManager**<p>
  This one replaces the default Java `removeIf` call used in vanilla for toasts with a more direct one. The default Java implementation calls the predicate twice, which, in this case, causes each toast to get rendered twice. This mod replaces the `removeIf` call with an `Iterator`, improving performance whether toasts are present or not. <p>

<p>
That's all the big optimizations. The rest don't do a lot, but still help (e.g. caching entity flags, avoiding unnecessary lerp calls)
</p>
