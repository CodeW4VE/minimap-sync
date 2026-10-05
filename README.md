# MinimapSync (W4VE fork)

Share minimap waypoints and custom icons through a Fabric server. Clients use Xaero's Minimap or VoxelMap. This is a maintained fork of [Earthcomputer/minimap-sync](https://github.com/Earthcomputer/minimap-sync), under the original MIT license.

## Installation

Install MinimapSync and [Fabric API](https://modrinth.com/mod/fabric-api) on both the server and clients. Each client also needs one supported minimap. The server needs no minimap mod.

Download the jar matching Minecraft from [GitHub Releases](https://github.com/CodeW4VE/minimap-sync/releases) or [Modrinth](https://modrinth.com/mod/minimap-sync-w4ve).

| Minecraft | Java | Fabric Loader | Xaero's Minimap | VoxelMap |
| --- | --- | --- | --- | --- |
| 1.21 / 1.21.1 | 21 | 0.16.5+ | 25.2.10 | 1.21-1.13.1 |
| 26.3 | 25 | 0.19.5+ | 26.5.3 | 26.3-1.16.13 (Fabric) |

These are the compatibility targets used for the builds. JourneyMap integration is not included in this fork. Xaero's World Map is optional; the legacy fork targeted version 1.39.12, and the 26.3 World Map integration has not been tested.

## Features

- Synchronize waypoint positions, names, colours, descriptions and dimensions.
- Share PNG icons and choose them in the waypoint editor.
- Keep private waypoints private and control teleport access on the server.
- Persist waypoint definitions, icons and teleport settings with the world.

Use `/waypoint` for commands and `/waypoint list` to view the current waypoints. `/waypoint config teleport` controls who may teleport; changing it requires operator level 2.

## Updating

Stop the server, replace the matching jars on the server and clients, and keep the same world and client configuration. Preserve the world's `minimapsync.json` and `minimapsync_icons/` directory. The saved data format and network protocol remain version 5.

Version **1.2.3-w4ve.2** adds native Minecraft 26.3 networking and GUI support, updates Xaero and VoxelMap integrations, and fixes icon actions from the shared selector. It also fixes duplicated Xaero world paths so shared waypoints appear in the active map, avoids a recursive path resolver, and releases custom icon textures when disconnecting.

## Building

Install JDK 21 for the legacy target and JDK 25 for 26.3. Set `JAVA_HOME_21_X64` and `JAVA_HOME_25_X64` if the JDKs are outside the local default paths.

```
python3 tools/multiversion.py
```

Separate jars are written to `build/multiversion/`. Builds run in isolated directories; the base source targets 1.21.1 and `variants/26.3/` supplies the native port.

## Licencia / Español

Fork mantenido de [Earthcomputer/minimap-sync](https://github.com/Earthcomputer/minimap-sync), con licencia [MIT](LICENSE). Copyright © 2022 Earthcomputer.

Instalá MinimapSync y Fabric API en servidor y clientes. Cada cliente también necesita Xaero's Minimap o VoxelMap, usando las versiones de la tabla. El servidor no necesita un minimapa. JourneyMap no está incluido.

Para actualizar, detené el servidor y reemplazá los jars que correspondan a Minecraft. Conservá el mundo, `minimapsync.json`, `minimapsync_icons/` y la configuración del cliente. El formato de datos y el protocolo siguen en versión 5.
