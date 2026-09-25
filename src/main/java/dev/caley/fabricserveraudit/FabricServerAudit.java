package dev.caley.fabricserveraudit;

import dev.caley.fabricserveraudit.mixin.ServerCommonNetworkHandlerAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Comparator;

public final class FabricServerAudit implements DedicatedServerModInitializer {
    public static final String MOD_ID = "fabricserveraudit";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MinecraftServer server;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(startedServer -> {
            server = startedServer;
            LOGGER.info("Fabric Server Audit enabled; onlineMode={}", startedServer.isOnlineMode());
            auditLoadedMods();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, joinedServer) -> {
            server = joinedServer;
            ServerPlayerEntity player = handler.getPlayer();
            LOGGER.info("player_join username={} uuid={} remoteAddress={} onlineMode={}",
                    player.getGameProfile().getName(),
                    player.getUuidAsString(),
                    remoteAddress(handler),
                    joinedServer.isOnlineMode());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, disconnectedServer) -> {
            ServerPlayerEntity player = handler.getPlayer();
            LOGGER.info("player_disconnect username={} uuid={} remoteAddress={} onlineMode={}",
                    player.getGameProfile().getName(),
                    player.getUuidAsString(),
                    remoteAddress(handler),
                    disconnectedServer.isOnlineMode());
        });
    }

    private static void auditLoadedMods() {
        var mods = FabricLoader.getInstance().getAllMods().stream()
                .sorted(Comparator.comparing(mod -> mod.getMetadata().getId()))
                .toList();

        LOGGER.info("mod_audit_start loadedModCount={}", mods.size());
        for (ModContainer mod : mods) {
            ModMetadata metadata = mod.getMetadata();
            LOGGER.info("mod_loaded id={} name={} version={}",
                    metadata.getId(),
                    quote(metadata.getName()),
                    metadata.getVersion().getFriendlyString());
        }
        LOGGER.info("mod_audit_end loadedModCount={}", mods.size());
    }

    private static String remoteAddress(ServerPlayNetworkHandler handler) {
        ClientConnection connection = ((ServerCommonNetworkHandlerAccessor) handler).fabricserveraudit$getConnection();
        SocketAddress address = connection.getAddress();
        return address == null ? "unknown" : address.toString();
    }

    private static String quote(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
