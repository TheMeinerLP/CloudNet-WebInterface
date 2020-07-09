package me.madfix.cloudnet.webinterface.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.database.MobDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Mob Service is a class used to manage CloudNet Mobs. With this class it should be possible to manage all functions CloudNet provides internally
 */
final class MobService {

    private boolean enable;
    private MobDatabase mobDatabase;
    private final WebInterface webInterface;
    private final Path mobConfigurationFile = Paths.get("local", "servermob_config.json");

    public MobService(WebInterface webInterface) {
        this.webInterface = webInterface;
        this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().ifPresent(interfaceConfiguration -> {
            this.enable = interfaceConfiguration.isMobSystem();
            if (this.enable) this.mobDatabase = new MobDatabase(
                    webInterface.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
        });
    }

    /**
     * Allows you to check if the mob system is active
     *
     * @return true will return if the system is active
     */
    public CompletableFuture<Optional<Boolean>> isEnabled() {
        return CompletableFuture.completedFuture(Optional.of(this.enable));
    }

    /**
     * Returns the mob database
     *
     * @return the database itself
     */
    public CompletableFuture<Optional<MobDatabase>> getMobDatabase() {
        CompletableFuture<Optional<MobDatabase>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            optionalCompletableFuture.complete(Optional.ofNullable(this.mobDatabase));
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Returns the current mob configuration
     *
     * @return contains everything about inventory information
     */
    public CompletableFuture<Optional<MobConfig>> getMobConfig() {
        CompletableFuture<Optional<MobConfig>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(this.mobConfigurationFile,
                    StandardCharsets.UTF_8)) {
                Optional<JsonElement> jsonConfig = Optional.empty();
                try {
                    jsonConfig = Optional.of(JsonParser.parseReader(bufferedReader));
                } catch (JsonSyntaxException e) {
                    this.enable = false;
                    CloudNet.getLogger().severe("[301] Mob service is deactivated to prevent errors. Please fix the errors and try the function again.");
                    CloudNet.getLogger().log(Level.SEVERE, "[301] An unexpected error occurred while reading the configuration file.", e);
                }
                jsonConfig.ifPresent(jsonElement -> optionalCompletableFuture.complete(Optional.of(this.webInterface.getGson()
                        .fromJson(jsonElement, TypeToken.get(MobConfig.class).getType()))));
            } catch (IOException e) {
                this.enable = false;
                CloudNet.getLogger().severe("[302] Mob service is deactivated to prevent errors. Please fix the errors and try the function again.");
                CloudNet.getLogger().log(Level.SEVERE, "[302] An unexpected error occurred while reading the configuration file.", e);
            }
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Returns the mob from the database using an id
     *
     * @param mobId is used to identify the mob
     * @return is the mob object class with all necessary information about the mob
     */
    public CompletableFuture<Optional<ServerMob>> getServerMob(UUID mobId) {
        CompletableFuture<Optional<ServerMob>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable && this.mobDatabase != null) {
            optionalCompletableFuture.complete(this.mobDatabase.loadAll().values().stream()
                    .filter(serverMob -> serverMob.getUniqueId().equals(mobId)).findFirst());
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Returns a list of mobs that are set on the server.
     *
     * @return the list with all mob objects including their information
     */
    public CompletableFuture<Optional<Collection<ServerMob>>> getMobs() {
        CompletableFuture<Optional<Collection<ServerMob>>> collectionCompletableFuture = new CompletableFuture<>();
        if (this.enable && this.mobDatabase != null) {
            collectionCompletableFuture.complete(Optional.of(this.mobDatabase.loadAll().values()));
        } else collectionCompletableFuture.cancel(true);
        return collectionCompletableFuture;
    }


    /**
     * Removes a mob from the system
     *
     * @param mobId is used as an indicator for the mob
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Optional<Boolean>> removeMob(UUID mobId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            if (this.mobDatabase != null) {
                this.mobDatabase.remove(mobId);
                CloudNet.getInstance().getNetworkManager().updateAll();
                optionalCompletableFuture.complete(Optional.of(true));
            } else optionalCompletableFuture.cancel(true);
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Adds a mob to the system
     *
     * @param serverMob is the specified mob to be added
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Optional<Boolean>> addMob(ServerMob serverMob) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            if (this.mobDatabase != null) {
                this.mobDatabase.add(serverMob);
                CloudNet.getInstance().getNetworkManager().updateAll();
                optionalCompletableFuture.complete(Optional.of(true));
            } else optionalCompletableFuture.cancel(true);
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Updates a mob in the system
     *
     * @param serverMob is the one to be updated
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Optional<Boolean>> updateMob(ServerMob serverMob) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            if (this.mobDatabase != null) {
                this.getServerMob(serverMob.getUniqueId())
                        .thenAccept(result -> result.ifPresent(mob -> removeMob(mob.getUniqueId()).thenAccept(success -> {
                            if (success.isPresent() && success.get()) {
                                addMob(serverMob).thenAccept(addMobSuccess -> {
                                    if (addMobSuccess.isPresent() && addMobSuccess.get()) {
                                        optionalCompletableFuture.complete(Optional.of(true));
                                    } else optionalCompletableFuture.cancel(true);
                                });
                            } else optionalCompletableFuture.cancel(true);
                        })));
            } else optionalCompletableFuture.cancel(true);
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Updates the mob configuration
     *
     * @param mobConfig is updated
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Optional<Boolean>> updateMobConfig(MobConfig mobConfig) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            Document document = new Document();
            document.append("mobConfig",
                    this.webInterface.getGson().toJsonTree(mobConfig, TypeToken.get(MobConfig.class).getType()));
            document.saveAsConfig(this.mobConfigurationFile);
            CloudNet.getInstance().getNetworkManager().updateAll();
            optionalCompletableFuture.complete(Optional.of(true));
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }
}
