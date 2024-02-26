## BadOptimizations
A collection of mostly micro optimizations that add up to something bigger!

## Wait, what does this even do?
It does multiple things:<p>

- **Avoid updating lightmap**<p>
  Updating lightmap textures are probably the most expensive part of the client tick. During each tick, the client will do some vector math to calculate lightmaps for blocks and the sky, then upload that new texture to the GPU. However, this can be completely avoided and cached if nothing affecting brightness is changed. This mod will cancel lightmap updates if nothing that changes brightness (e.g. gamma slider, potion effects) has changed. 
- **Sky color calculation optimizations**<p>
  Minecraft uses `CubicScampler.sampleColor` every frame to calculate the current sky color. This function loops 216 times to factor in surrounding biomes-even if all surrounding biomes are identical or have identical sky colors. This mod makes this function only get called if you are between biomes with different sky colors and only makes it get called every tick. Otherwise, a shorter, simpler and faster sky color calculation is used.
- **Don't do debug logic if we don't need to**<p>
  Minecraft has four debug renderers that can be activated with a debug server: bees, game events, game test and villager AI. Even if there's nothing to process, the logic for these debug renderers are still executed. This mod will only execute them if debug is enabled and there is data to process.

<p>
That's all the major ones, at least.
You can disable any optimization in the config file if you need to.
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
