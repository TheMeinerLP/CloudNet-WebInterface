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
        this.webInterface.getConfigurationService()
                         .getOptionalInterfaceConfiguration()
                         .ifPresent(interfaceConfiguration -> {
                             this.enable = interfaceConfiguration.isMobSystem();
                             if (this.enable) {
                                 this.mobDatabase = new MobDatabase(webInterface.getCloud()
                                                                                .getDatabaseManager()
                                                                                .getDatabase("cloud_internal_cfg"));
                             }
                         });
    }

    /**
     * Allows you to check if the mob system is active
     *
     * @return true will return if the system is active
     */
    public CompletableFuture<Boolean> isEnabled() {
        return CompletableFuture.completedFuture(this.enable);
    }

    /**
     * Returns the mob database
     *
     * @return the database itself
     */
    public CompletableFuture<MobDatabase> getMobDatabase() {
        CompletableFuture<MobDatabase> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            optionalCompletableFuture.complete(this.mobDatabase);
        } else {
            optionalCompletableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return optionalCompletableFuture;
    }

    /**
     * Returns the current mob configuration
     *
     * @return contains everything about inventory information
     */
    public CompletableFuture<MobConfig> getMobConfig() {
        CompletableFuture<MobConfig> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(this.mobConfigurationFile,
                                                                         StandardCharsets.UTF_8)) {
                JsonElement jsonConfig = null;
                try {
                    jsonConfig = JsonParser.parseReader(bufferedReader);
                } catch (JsonSyntaxException e) {
                    this.enable = false;
                    CloudNet.getLogger()
                            .severe("[301] Mob service is deactivated to prevent errors. Please fix the errors and try the function again.");
                    CloudNet.getLogger()
                            .log(Level.SEVERE,
                                 "[301] An unexpected error occurred while reading the configuration file.",
                                 e);
                }
                if (jsonConfig != null) {
                    optionalCompletableFuture.complete(this.webInterface.getGson()
                                                                        .fromJson(jsonConfig,
                                                                                  TypeToken.get(MobConfig.class)
                                                                                           .getType()));
                }
            } catch (IOException e) {
                this.enable = false;
                CloudNet.getLogger()
                        .severe("[302] Mob service is deactivated to prevent errors. Please fix the errors and try the function again.");
                CloudNet.getLogger()
                        .log(Level.SEVERE,
                             "[302] An unexpected error occurred while reading the configuration file.",
                             e);
            }
        } else {
            optionalCompletableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return optionalCompletableFuture;
    }

    /**
     * Returns the mob from the database using an id
     *
     * @param mobId is used to identify the mob
     * @return is the mob object class with all necessary information about the mob
     */
    public CompletableFuture<ServerMob> getServerMob(UUID mobId) {
        CompletableFuture<ServerMob> completableFuture = new CompletableFuture<>();
        if (this.enable && this.mobDatabase != null) {
            this.mobDatabase.loadAll()
                            .values()
                            .stream()
                            .filter(serverMob -> serverMob.getUniqueId().equals(mobId))
                            .findFirst()
                            .ifPresent(completableFuture::complete);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return completableFuture;
    }

    /**
     * Returns a list of mobs that are set on the server.
     *
     * @return the list with all mob objects including their information
     */
    public CompletableFuture<Collection<ServerMob>> getMobs() {
        CompletableFuture<Collection<ServerMob>> completableFuture = new CompletableFuture<>();
        if (this.enable && this.mobDatabase != null) {
            completableFuture.complete(this.mobDatabase.loadAll().values());
        } else {
            completableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return completableFuture;
    }


    /**
     * Removes a mob from the system
     *
     * @param mobId is used as an indicator for the mob
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> removeMob(UUID mobId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable && this.mobDatabase != null) {
            this.mobDatabase.remove(mobId);
            CloudNet.getInstance().getNetworkManager().updateAll();
            optionalCompletableFuture.complete(true);
        } else {
            optionalCompletableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return optionalCompletableFuture;
    }

    /**
     * Adds a mob to the system
     *
     * @param serverMob is the specified mob to be added
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> addMob(ServerMob serverMob) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.mobDatabase != null) {
            this.mobDatabase.add(serverMob);
            CloudNet.getInstance().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return completableFuture;
    }

    /**
     * Updates a mob in the system
     *
     * @param serverMob is the one to be updated
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> updateMob(ServerMob serverMob) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            if (this.mobDatabase != null) {
                removeMob(serverMob.getUniqueId()).thenAccept(success -> {
                    if (success) {
                        addMob(serverMob).thenAccept(addMobSuccess -> {
                            if (addMobSuccess) {
                                optionalCompletableFuture.complete(true);
                            } else {
                                optionalCompletableFuture.cancel(true);
                            }
                        });
                    } else {
                        optionalCompletableFuture.cancel(true);
                    }
                });
            } else {
                optionalCompletableFuture.cancel(true);
            }
        } else {
            optionalCompletableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return optionalCompletableFuture;
    }

    /**
     * Updates the mob configuration
     *
     * @param mobConfig is updated
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> updateMobConfig(MobConfig mobConfig) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable) {
            Document document = new Document();
            document.append("mobConfig",
                            this.webInterface.getGson()
                                             .toJsonTree(mobConfig, TypeToken.get(MobConfig.class).getType()));
            document.saveAsConfig(this.mobConfigurationFile);
            CloudNet.getInstance().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("The Mob System is not active!"));
        }
        return completableFuture;
    }
}
