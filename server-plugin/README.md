# LightBR Server Demo Plugin

A small Paper 1.21.4 plugin that demonstrates server-controlled LightBR settings. It sends an ACK + render context on join and exposes a command to send SET_CONTEXT and RESET_CACHE packets.

## Build

```fish
../gradlew -p /home/ahmad/Documents/LightBR/server-plugin build
```

The jar will be at `server-plugin/build/libs/lightbr-server-plugin-0.1.0.jar`.

## Install

1. Drop the jar into your Paper server `plugins/` folder.
2. Start the server.

## Usage

- On join, players receive an ACK + context packet.
- Commands:
  - `/lightbrsettings ack [player]` sends ACK + context.
  - `/lightbrsettings setcontext [player] [enabled] [chunkXZ] [chunkY]` sends SET_CONTEXT with updated values.
  - `/lightbrsettings resetcache [player]` sends RESET_CACHE.

## Packet Format

The payload written to `lightbr:settings` matches the client mod:

- `varint packetType` (0 = ACK, 1 = SET_CONTEXT, 2 = RESET_CACHE)
- For ACK/SET_CONTEXT only, the render context fields follow:
  - `boolean isEnabled`
  - `varint chunkXZRadius`
  - `varint chunkYRadius`
  - `boolean renderAllWater`
  - `boolean renderAllLava`
  - `boolean unrenderBlockEntities`
  - `varint blockEntityCount`, then each `string`
  - `varint regionCount`, then each region `double ax, ay, az, bx, by, bz`

