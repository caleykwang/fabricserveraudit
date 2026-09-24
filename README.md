# Fabric Server Audit

Fabric Server Audit is a server-side Minecraft Fabric mod for recording authentication and connection events in a format that can be audited later.

The first milestone logs:

- player username
- UUID
- remote address when Fabric exposes it
- server online-mode status
- successful joins
- disconnects

The next milestones are planned around rejected connection diagnostics, whitelist/authentication snapshots, and structured log output for downstream analysis.

## Target

This starter build targets Minecraft `1.21.1` with Java `21`. If the production server uses a different Minecraft version, update `gradle.properties` and `fabric.mod.json` before building.

## Build

```sh
gradle build
```

The compiled mod jar will be written under `build/libs`.

## Configuration Schema

The repo includes `schema/fabricserveraudit.schema.json` for editor validation and future config generation. A starter config lives at `src/main/resources/fabricserveraudit.json`.

## Status

This is an early implementation skeleton. It records successful joins and disconnects through Fabric API events. Rejected-login logging usually needs mixins around Minecraft's login handler, which should be added once the exact server Minecraft version is locked.