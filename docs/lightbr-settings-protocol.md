# LightBR Settings Protocol Specification

This document specifies the custom payload protocol used to control LightBR rendering settings over the network.

## Overview

- Config Channel: `lightbr:config`
- Settings Channel: `lightbr:settings`
- Direction:
  - `lightbr:config`: C2S ACK, S2C ACK
  - `lightbr:settings`: S2C only
- Transport: Minecraft Custom Payload using the same binary layout as `PacketByteBuf`
- Packet type is always the first field and is encoded as a VarInt

## Packet Types

### Config Channel (`lightbr:config`)

| ID | Name | Direction | Payload |
| --- | --- | --- | --- |
| 0 | ACK | C2S + S2C | C2S: protocol version; S2C: no payload |

### Settings Channel (`lightbr:settings`)

| ID | Name | Direction | Payload |
| --- | --- | --- | --- |
| 1 | SET_ENABLED | S2C | boolean |
| 2 | SET_RENDER_ALL_WATER | S2C | boolean |
| 3 | SET_CHUNK_XZ | S2C | varint |
| 4 | SET_CHUNK_Y | S2C | varint |
| 5 | SET_RENDER_ALL_LAVA | S2C | boolean |
| 6 | SET_UNRENDER_BLOCK_ENTITIES | S2C | boolean |
| 7 | SET_ALWAYS_RENDER_BLOCK_ENTITIES | S2C | list of strings |
| 8 | SET_ALWAYS_RENDER_REGIONS | S2C | list of regions |
| 9 | RESET_CACHE | S2C | no payload |
| 10 | RESET_SETTINGS | S2C | no payload |

Notes:
- The client sends `ACK` once on join over `lightbr:config`. This packet contains the protocol version client used.
- The server replies with `ACK` (S2C) over `lightbr:config` to signal server-controlled mode.
- When server control is active, the server sends individual settings packets over `lightbr:settings` as needed.

## Packet Layouts

### ACK (client -> server, config channel)

```
varint packetType = 0
varint protocolVersion
```

### ACK (server -> client, config channel)

```
varint packetType = 0
```

### Settings Packets (server -> client)

```
varint packetType
payload
```

Payload layouts:
- `SET_ENABLED`: `boolean value`
- `SET_RENDER_ALL_WATER`: `boolean value`
- `SET_CHUNK_XZ`: `varint value`
- `SET_CHUNK_Y`: `varint value`
- `SET_RENDER_ALL_LAVA`: `boolean value`
- `SET_UNRENDER_BLOCK_ENTITIES`: `boolean value`
- `SET_ALWAYS_RENDER_BLOCK_ENTITIES`: `varint count` + `string * count`
- `SET_ALWAYS_RENDER_REGIONS`: `varint count` + `Region * count`
- `RESET_CACHE`: no additional fields
- `RESET_SETTINGS`: no additional fields

### Region Layout

```
double ax
double ay
double az
double bx
double by
double bz
```

Regions are axis-aligned boxes. The ordering is preserved as written (no automatic min/max normalization).

## Behavioral Rules

- On client join, send C2S `ACK` on `lightbr:config`. The server replies with S2C `ACK` to enable server-controlled mode.
- If server-controlled mode is active, the client merges server-provided settings with local defaults.
- When a client receives any settings packet (S2C), it must:
  - apply the updated value to the server override context
  - clear render caches
  - reload the world renderer
- When a client receives `RESET_CACHE`, it must:
  - clear render caches
  - reload the world renderer
- When a client receives `RESET_SETTINGS`, it must:
  - clear all server overrides and revert to local defaults
  - clear render caches
  - reload the world renderer

## Implementation References

- Server encoder: `server-plugin/src/main/java/org/wengdev/lightbr/server/LightBRSettingsCodec.java`
- Server handler: `server-plugin/src/main/java/org/wengdev/lightbr/server/LightBRServerPlugin.java`
- Client handler: `src/main/java/org/wengdev/lightbr/LightBR.java`
- Payload wrappers: `src/main/java/org/wengdev/lightbr/network/SettingsPayload.java`, `src/main/java/org/wengdev/lightbr/network/ConfigPayload.java`
