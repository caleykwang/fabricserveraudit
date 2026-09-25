# Fabric Server Audit

Fabric Server Audit is a server-side Minecraft Fabric mod for testing audit logging on Fabric servers. The current implementation starts with connection events, but the broader goal is to help server owners audit security-relevant server state, including installed server-side mods, configuration changes, authentication settings, and player connection activity.

This project is early-stage and intended for controlled test environments while the event model, log format, mod-audit model, and privacy controls are being shaped.

## What It Logs Today

The first milestone records successful player joins and disconnects through Fabric API server events.

Current log fields:

- event type, such as `player_join` or `player_disconnect`
- player username
- player UUID
- remote socket address when available
- server online-mode status

Example log shape:

```text
player_join username=ExamplePlayer uuid=00000000-0000-0000-0000-000000000000 remoteAddress=/127.0.0.1:54321 onlineMode=true
```

## What It Should Audit Next

The next major direction is server-side mod auditing. A useful server audit tool should help answer questions like:

- Which server-side mods are installed?
- Which mod IDs, names, and versions are present at startup?
- Did the mod list change between server runs?
- Are unknown, unexpected, or locally modified mods present?
- Do any security-relevant config files change unexpectedly?
- Are authentication, whitelist, or permission settings drifting over time?

The intended path is to capture a baseline snapshot in a test environment, then compare later server starts against that baseline. That would make it easier to spot accidental changes, suspicious additions, or configuration drift.

## Why This Exists

Default Minecraft server logs are useful, but they are not always structured around security review. This mod is meant to become a lightweight audit layer for server owners who want clearer answers to questions like:

- Which account connected?
- Which UUID did the server see?
- Was the server running in online mode?
- What remote address was associated with the connection?
- Did the player successfully join or disconnect?
- Which server-side mods were loaded?
- Did the server's mod or config state change?
- Later: was a connection rejected, and why?

This is not an anti-cheat and it is not a moderation suite. It is an observability and audit tool.

## Test Environment Scope

Right now, this project should be treated as a test-environment tool. It is useful for validating Fabric hooks, log shape, server behavior, and future mod-audit snapshots before deciding what belongs in a production-ready release.

Recommended test setup:

- use a local or private Fabric server
- test with known accounts or test users
- test against a known mod set
- avoid collecting real public-player data until privacy controls are finished
- keep generated logs out of public issue reports unless sensitive fields are removed

## Privacy Note

The mod can log remote addresses, which usually include IP addresses. In a controlled test environment, that is useful for debugging and validating the audit pipeline. For public or production servers, server owners should treat IP addresses as sensitive operational data.

Before production use, this project should support and document:

- disabling remote address logging
- redacting or hashing remote addresses
- limiting log access to trusted admins
- setting log retention expectations
- disclosing audit logging in server rules or a privacy notice

Server-side mod and config auditing can also reveal operational details about a server. Treat generated audit reports as admin-only diagnostics unless they have been reviewed and sanitized.

## Target Versions

Current starter target:

- Minecraft: `1.21.1`
- Java: `21`
- Fabric Loader: `0.16.9+`
- Fabric API: `0.110.0+1.21.1`

If your server uses a different Minecraft version, update `gradle.properties` and `src/main/resources/fabric.mod.json` before building.

## Build

This repository currently expects Gradle to be available on the machine or CI runner.

```sh
gradle build
```

The compiled mod jar will be written under:

```text
build/libs/
```

## Configuration Schema

The repo includes a starter JSON schema at:

```text
schema/fabricserveraudit.schema.json
```

A starter config lives at:

```text
src/main/resources/fabricserveraudit.json
```

The schema is currently ahead of runtime config loading. It documents the intended controls while the implementation is still being built out.

## Roadmap

Near-term work:

- make config loading real instead of schema-only
- add optional remote-address redaction or hashing
- make log output more structured and stable
- add a startup snapshot of loaded server-side mods
- record mod ID, mod name, version, and source file where available
- add rejected-login diagnostics once the exact Minecraft server version is locked
- document a safe test-server workflow
- add release artifacts once CI builds cleanly

Later possibilities:

- baseline comparison for mod-list changes
- whitelist and authentication snapshots
- change detection for security-relevant server files
- JSON Lines output for downstream analysis
- admin-facing summaries for suspicious connection or mod changes
- allowlists for expected mods and config files

## Status

Experimental. The repository is being built in small milestones, with CI used as the source of truth for compatibility against the selected Fabric and Minecraft versions.