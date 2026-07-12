# MinimapSync (W4VE fork)

A fork of [Earthcomputer/minimap-sync](https://github.com/Earthcomputer/minimap-sync)
that syncs your minimap waypoints with the server so everyone shares the same set.
This fork exists to keep it working on **current Xaero's Minimap** builds.

> Maintained under the W4VE / CodeW4VE umbrella for the MineWave server. All credit
> for the mod goes to Earthcomputer; this fork only carries compatibility fixes.

## What this fork changes

- **Rebuilt against Xaero's Minimap `25.2.10_Fabric_1.21`** (upstream `1.2.2` was
  compiled against Xaero `24.4.0`). The stale build crashed on load with
  `Mixin transformation of ...WaypointsGuiRenderer failed / Scanned 0 target(s)`
  because the refmap pointed at old Xaero names. Recompiling regenerates it.
- **`WaypointReaderMixin` made optional (`require = 0`).** Xaero `25.2.x` removed
  `WaypointReader#getRenderBoxRight` (sizing moved to `getBoxScale` on the
  `xaero.hud` superclass). That mixin is purely cosmetic (custom-icon hitbox), so it
  no longer hard-crashes on newer Xaero. Porting it to `getBoxScale` is a later job.

**Target versions:** Minecraft `1.21.1`, Fabric, Xaero's Minimap `25.2.10`,
Xaero's World Map `1.39.12`. Client + server mod.

## Build

Needs JDK 21:

```
JAVA_HOME=/path/to/jdk-21 ./gradlew build
```

Output jar in `build/libs/`.

---

# MinimapSync (fork de W4VE) — Español

Fork de [Earthcomputer/minimap-sync](https://github.com/Earthcomputer/minimap-sync):
sincroniza los waypoints del minimapa con el servidor para que todos compartan el
mismo set. Este fork existe solo para mantenerlo funcionando con las versiones
**actuales de Xaero's Minimap**.

> Mantenido bajo W4VE / CodeW4VE para el servidor MineWave. Todo el crédito del mod
> es de Earthcomputer; este fork solo lleva arreglos de compatibilidad.

## Qué cambia este fork

- **Recompilado contra Xaero `25.2.10_Fabric_1.21`** (el `1.2.2` de upstream venía
  compilado contra Xaero `24.4.0`). Ese build viejo crasheaba al arrancar
  (`Mixin transformation of ...WaypointsGuiRenderer failed / Scanned 0 target(s)`)
  porque el refmap apuntaba a nombres viejos de Xaero. Recompilar lo regenera.
- **`WaypointReaderMixin` hecho opcional (`require = 0`).** Xaero `25.2.x` quitó
  `WaypointReader#getRenderBoxRight` (el tamaño pasó a `getBoxScale` en la
  superclase `xaero.hud`). Ese mixin es puramente cosmético (hitbox del icono
  custom), así que ya no tumba el arranque con Xaero nuevo. Portarlo a `getBoxScale`
  queda para más adelante.

**Versiones objetivo:** Minecraft `1.21.1`, Fabric, Xaero's Minimap `25.2.10`,
Xaero's World Map `1.39.12`. Mod de cliente + servidor.

## Licencia

MIT, igual que el upstream. Ver [LICENSE](LICENSE) — Copyright (c) 2022 Earthcomputer.
