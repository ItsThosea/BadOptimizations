## BadOptimizations
A collection of mostly micro optimizations that add up to something bigger!

## What does it do?
Multiple things:<p>

- **Avoid updating lightmap**<p>
  Updating lightmap textures are one of the most expensive parts of the client tick. During each tick, the client will do vector math to calculate lightmaps for blocks and the sky, then upload that new texture to the GPU. However, this can be completely avoided and cached if nothing affecting block brightness has changed. This mod will cancel lightmap updates if nothing that changes block brightness (e.g. gamma slider, potion effects, dimension) has changed. 
- **Sky color calculation optimizations**<p>
  Minecraft uses a cubic color sampler every frame to calculate the current sky color. This function loops 216 times to factor in surrounding biomes, even if all surrounding biomes are identical or have identical sky colors. This mod makes the color sampler only used if you are between biomes with different sky colors, and caches it for each tick. Otherwise, a shorter, simpler and faster sky color calculation is used.
- **Don't do debug logic if we don't need to**<p>
  Minecraft has four debug renderers that can be activated with a debug server: bees, game events, game test and villager AI. Even if there's nothing to process, the logic for these debug renderers are still executed. This mod will only execute them if debug is enabled and there is data from the server to process.

<p>
That's a few of them, at least.
You can disable any optimization in the config file if you need to.
</p>

## How much does it help?
Depends on your system and luck, but on my own PC, an RTX 3060, intel i7 12700 and 4GB of allocated ram on Fabulously Optimized:<p>
Without toasts, it goes from *1926-1955 FPS*:<p>
<img src="https://cdn.teamcelestial.org/api/shares/U3OTQ0N/files/d48c2c68-470f-4b9e-b319-2ced7496a94f?download=false" width="400"><p>
To 2008-2023 FPS:<p>
<img src="https://cdn.teamcelestial.org/api/shares/U3OTQ0N/files/491fc5a6-4dc7-43bd-b0fc-c9981890815a?download=false" width="400"><p>
And with toasts, it goes from *1351-1384 FPS*:<p>
<img src="https://cdn.teamcelestial.org/api/shares/U3OTQ0N/files/46f5bf33-4536-4381-9ae1-800f9881c4e8?download=false" width="400"><p>
To *1414-1458 FPS*:<p>
<img src="https://cdn.teamcelestial.org/api/shares/U3OTQ0N/files/62fd0efd-c77b-4e4b-be16-52187f06cb2b?download=false" width="400"><p>

## Dependencies?
None.

*Available on [Modrinth](https://modrinth.com/mod/badoptimizations/) and [CurseForge](https://curseforge.com/minecraft/mc-mods/badoptimizations).*