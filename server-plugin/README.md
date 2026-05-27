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
  - `/lightbrsettings setcontext [player] [enabled] [chunkXZ] [chunkY]` sends per-field updates for the current context.
  - `/lightbrsettings resetcache [player]` sends RESET_CACHE.

## Packet Format

The payloads are split across two channels:

- `lightbr:config`:
  - `varint packetType` (0 = ACK)
  - C2S: `varint protocolVersion`
  - S2C: no payload

- `lightbr:settings`:
  - `varint packetType`
  - For packet types 1..8, a single value or list follows (see `docs/lightbr-settings-protocol.md`)
  - `packetType = 9` is RESET_CACHE with no additional fields
  - `packetType = 10` is RESET_SETTINGS with no additional fields
