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
- **Don't do unnecessary FOV calculations if we don't need to**<p>
  When you're spectating either a player or nobody, the game calculates your FOV factor (e.g. charging bow / potion effects), even if your FOV effect scale is zero. This mod removes this calculation if your FOV effect scale is zero.<p>
- **Replace `removeIf` call in ToastManager**<p>
  This one replaces the default Java `removeIf` call used in vanilla for toasts with a more direct one. The default Java implementation calls the predicate twice, which, in this case, causes each toast to get rendered twice. This mod replaces the `removeIf` call with an `Iterator`, improving performance whether toasts are present or not. <p>

<p>
That's all the major ones, at least.
</p>

## But how much does it help?
*(tested on my own PC with an RTX 3060, intel i7 12700 and 4GB of allocated ram on Fabulously Optimized)*<p>
Without toasts, it goes from 1926-1955 FPS:<p>
<img src="https://github.com/Fabulously-Optimized/fabulously-optimized/assets/104597976/523f9117-5dfb-469a-a1e3-2e5c32597057" width="256"><p>
To 2008-2023 FPS:<p>
<img src="https://github.com/Fabulously-Optimized/fabulously-optimized/assets/104597976/a27fcfab-c9ac-4fb0-9dfa-fc2efc61b2f4" width="256"><p>
And with toasts, it goes from 1351-1384 FPS:<p>
<img src="https://github.com/Fabulously-Optimized/fabulously-optimized/assets/104597976/859f710b-036e-4080-9e94-ceaaf5a9bbf9" width="256"><p>
To 1414-1458 FPS:<p>
<img src="https://github.com/Fabulously-Optimized/fabulously-optimized/assets/104597976/5a01d4a7-0789-465d-a08f-aaa54386deb7" width="256"><p>

## Dependencies?
None.

*Available on [Modrinth](https://modrinth.com/mod/badoptimizations/) and [CurseForge](https://curseforge.com/minecraft/mc-mods/badoptimizations).*
