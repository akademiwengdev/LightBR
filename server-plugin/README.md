# LightBR Server Demo Plugin

A small Paper 1.21.4 plugin that demonstrates server-controlled LightBR settings. It replies to client ACKs on `lightbr:config` and sends per-setting updates on `lightbr:settings`.

## Build

```fish
../gradlew -p /home/ahmad/Documents/LightBR/server-plugin build
```

The jar will be at `server-plugin/build/libs/lightbr-server-plugin-0.1.0.jar`.

## Install

1. Drop the jar into your Paper server `plugins/` folder.
2. Start the server.

## Usage

- When a player joins, the client sends an ACK packet on `lightbr:config`; the server responds with ACK to enable server-controlled mode.
- Commands:
  - `/lightbrsettings setcontext [player] [enabled] [chunkXZ] [chunkY] [autoFix]` sends per-field updates for the current context. Use `default` for a field to let the client use its local value.
  - `/lightbrsettings resetcache [player]` sends RESET_CACHE.
  - `/lightbrsettings resetsettings [player]` sends RESET_SETTINGS.
  - `/lightbrsettings setregion <id> ...`, `/lightbrsettings addregion <id> ...`, and `/lightbrsettings removeregion <id>` update the demo context's region lists.

## Packet Format

The payloads are split across two channels:

- `lightbr:config`:
  - `varint packetType` (0 = ACK)
  - C2S: `varint protocolVersion` (the demo plugin supports version `6`)
  - S2C: no payload

- `lightbr:settings`:
  - `varint packetType`
  - `packetType = 1`: SET_ENABLED, boolean
  - `packetType = 2`: SET_RENDER_ALL_WATER, boolean
  - `packetType = 3`: SET_CHUNK_XZ, varint
  - `packetType = 4`: SET_CHUNK_Y, varint
  - `packetType = 5`: SET_RENDER_ALL_LAVA, boolean
  - `packetType = 6`: SET_ALWAYS_RENDER_REGIONS, region list
  - `packetType = 7`: RESET_CACHE, no additional fields
  - `packetType = 8`: RESET_SETTINGS, no additional fields
  - `packetType = 9`: BULK_SET_CONTEXT, sub-packet list
  - `packetType = 10`: ADD_ALWAYS_RENDER_REGIONS, region list
  - `packetType = 11`: REMOVE_ALWAYS_RENDER_REGIONS, varint ID
  - `packetType = 12`: SET_AUTO_FIX_INCOMPLETE_CHUNKS, boolean
