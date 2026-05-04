# LightBR Settings Protocol Specification

This document specifies the custom payload protocol used to control LightBR rendering settings over the network.

## Overview

- Channel: `lightbr:settings`
- Direction: client-to-server (ACK only) and server-to-client (ACK, SET_CONTEXT, RESET_CACHE)
- Transport: Minecraft Custom Payload using the same binary layout as `PacketByteBuf`
- Packet type is always the first field and is encoded as a VarInt

## Packet Types

| ID | Name | Direction | Payload |
| --- | --- | --- | --- |
| 0 | ACK | C2S + S2C | Render context (S2C only), none in C2S |
| 1 | SET_CONTEXT | S2C | Render context |
| 2 | RESET_CACHE | S2C | No additional fields |

Notes:
- The client sends `ACK` once on join (C2S). This packet contains only the packet type.
- The server replies with `ACK` (S2C) containing a full render context. This puts the client into server-controlled mode.
- The server may send `SET_CONTEXT` at any time to update the render context.
- The server may send `RESET_CACHE` at any time to force client cache invalidation.

## Encoding Rules

All fields use the same encoding as `PacketByteBuf`:

- `boolean`: 1 byte (`0x00` or `0x01`)
- `varint`: Minecraft VarInt (7-bit continuation)
- `string`: `varint` length in bytes + UTF-8 bytes
- `double`: 8-byte IEEE 754, big-endian

When using `DataOutputStream`, remember:
- `writeBoolean` matches `PacketByteBuf#writeBoolean`
- `writeDouble` matches `PacketByteBuf#writeDouble`
- `writeUTF` is NOT compatible; use the VarInt length + UTF-8 bytes as described above

## Packet Layouts

### ACK (client -> server)

```
varint packetType = 0
```

### ACK (server -> client)

```
varint packetType = 0
RenderContext context
```

### SET_CONTEXT (server -> client)

```
varint packetType = 1
RenderContext context
```

### RESET_CACHE (server -> client)

```
varint packetType = 2
```

## RenderContext Layout

The render context is a full snapshot of all render settings. Field order and types are fixed and must match exactly.

```
boolean isEnabled
varint chunkXZRadius
varint chunkYRadius
boolean renderAllWater
boolean renderAllLava
boolean unrenderBlockEntities
varint alwaysRenderBlockEntitiesCount
string alwaysRenderBlockEntityId * count
varint alwaysRenderRegionsCount
Region * count
```

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

- On client join, send C2S `ACK`. The server should respond with S2C `ACK` containing the initial render context.
- When a client receives S2C `ACK` or `SET_CONTEXT`, it must:
  - apply the new render context
  - clear render caches
  - reload the world renderer
- When a client receives `RESET_CACHE`, it must:
  - clear render caches
  - reload the world renderer
- Server-controlled mode is active once S2C `ACK` is received; local config should be considered read-only until disconnect.

## Implementation References

- Server encoder: `server-plugin/src/main/java/org/wengdev/lightbr/server/LightBRSettingsCodec.java`
- Client decoder: `src/main/java/org/wengdev/lightbr/RenderContext.java`
- Client handler: `src/main/java/org/wengdev/lightbr/LightBR.java`
- Payload wrapper: `src/main/java/org/wengdev/lightbr/network/SettingsPayload.java`

