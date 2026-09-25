package dev.caley.fabricserveraudit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AuditConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = FabricServerAudit.MOD_ID + ".json";

    boolean enabled = true;
    boolean logSuccessfulJoins = true;
    boolean logDisconnects = true;
    boolean logRemoteAddress = true;
    boolean logOnlineMode = true;
    boolean logUuid = true;
    boolean logLoadedMods = true;

    public static AuditConfig load(Logger logger) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        AuditConfig defaults = new AuditConfig();

        if (Files.notExists(path)) {
            writeDefaults(path, defaults, logger);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            AuditConfig config = GSON.fromJson(reader, AuditConfig.class);
            return config == null ? defaults : config;
        } catch (IOException | JsonSyntaxException exception) {
            logger.warn("Unable to load {}; using defaults", path, exception);
            return defaults;
        }
    }

    private static void writeDefaults(Path path, AuditConfig defaults, Logger logger) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(defaults, writer);
            }
            logger.info("Created default audit config at {}", path);
        } catch (IOException exception) {
            logger.warn("Unable to write default audit config at {}; using in-memory defaults", path, exception);
        }
    }
}
