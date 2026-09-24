package dev.caley.fabricserveraudit;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public final class FabricServerAudit implements DedicatedServerModInitializer {
    public static final String MOD_ID = "fabricserveraudit";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MinecraftServer server;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(startedServer -> {
            server = startedServer;
            LOGGER.info("Fabric Server Audit enabled; onlineMode={}", startedServer.isOnlineMode());
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

    private static String remoteAddress(ServerPlayNetworkHandler handler) {
        SocketAddress address = handler.getConnection().getAddress();
        return address == null ? "unknown" : address.toString();
    }

    public static MinecraftServer getServer() {
        return server;
    }
}