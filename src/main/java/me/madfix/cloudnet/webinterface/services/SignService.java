package me.madfix.cloudnet.webinterface.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.database.SignDatabase;

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

final class SignService {

    private boolean enable;
    private SignDatabase signDatabase;
    private final WebInterface webInterface;
    private final Path signConfigurationFile = Paths.get("local", "signLayout.json");

    public SignService(WebInterface webInterface) {
        this.webInterface = webInterface;
        this.webInterface.getConfigurationService()
                         .getOptionalInterfaceConfiguration()
                         .ifPresent(interfaceConfiguration -> {
                             this.enable = interfaceConfiguration.isSignSystem();
                             if (this.enable) {
                                 this.signDatabase = new SignDatabase(webInterface.getCloud()
                                                                                  .getDatabaseManager()
                                                                                  .getDatabase("cloud_internal_cfg"));
                             }
                         });
    }

    /**
     * Allows you to check if the sign system is active
     *
     * @return true will return if the system is active
     */
    public CompletableFuture<Optional<Boolean>> isEnabled() {
        return CompletableFuture.completedFuture(Optional.of(this.enable));
    }

    /**
     * Returns the sign database
     *
     * @return the database itself
     */
    public CompletableFuture<SignDatabase> geSignDatabase() {
        CompletableFuture<SignDatabase> completableFuture = new CompletableFuture<>();
        if (this.enable) {
            completableFuture.complete(this.signDatabase);
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Returns the current sign configuration
     *
     * @return contains everything about layout information
     */
    public CompletableFuture<SignLayoutConfig> getSignConfig() {
        CompletableFuture<SignLayoutConfig> completableFuture = new CompletableFuture<>();
        if (this.enable) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(this.signConfigurationFile,
                                                                         StandardCharsets.UTF_8)) {
                JsonElement jsonConfig = null;
                try {
                    jsonConfig = JsonParser.parseReader(bufferedReader);
                } catch (JsonSyntaxException e) {
                    this.enable = false;
                    CloudNet.getLogger()
                            .severe("[401] Sign service is deactivated to prevent errors. Please fix the errors and try the function again.");
                    CloudNet.getLogger()
                            .log(Level.SEVERE,
                                 "[401] An unexpected error occurred while reading the configuration file.",
                                 e);
                }
                if (jsonConfig != null) {
                    completableFuture.complete(this.webInterface.getGson()
                                                                .fromJson(jsonConfig,
                                                                          TypeToken.get(SignLayoutConfig.class)
                                                                                   .getType()));
                }
            } catch (IOException e) {
                this.enable = false;
                CloudNet.getLogger()
                        .severe("[402] Sign service is deactivated to prevent errors. Please fix the errors and try the function again.");
                CloudNet.getLogger()
                        .log(Level.SEVERE,
                             "[402] An unexpected error occurred while reading the configuration file.",
                             e);
            }
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Returns a sign based on its Id
     *
     * @param signId is the id by which the sign can be recognized
     * @return a sign instance
     */
    public CompletableFuture<Sign> getSign(UUID signId) {
        CompletableFuture<Sign> completableFuture = new CompletableFuture<>();
        if (this.enable && this.signDatabase != null) {
            this.signDatabase.loadAll()
                             .values()
                             .stream()
                             .filter(s -> s.getUniqueId().equals(signId))
                             .findFirst()
                             .ifPresent(completableFuture::complete);
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Returns a list of signs
     *
     * @return a sign list
     */
    public CompletableFuture<Collection<Sign>> getSigns() {
        CompletableFuture<Collection<Sign>> completableFuture = new CompletableFuture<>();
        if (this.enable && this.signDatabase != null) {
            completableFuture.complete(this.signDatabase.loadAll().values());
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Removes a sign by its Id
     *
     * @param signId is taken as indicator for the sign
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> removeSign(UUID signId) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.signDatabase != null) {
            this.signDatabase.removeSign(signId);
            CloudNet.getInstance().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Adds a sign to the system
     *
     * @param sign what to add
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> addSign(Sign sign) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.signDatabase != null) {
            this.signDatabase.add(sign);
            CloudNet.getInstance().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Updates a sign in the system
     *
     * @param sign what to update
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> updateSign(Sign sign) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.signDatabase != null) {
            removeSign(sign.getUniqueId()).thenAccept(success -> {
                if (success) {
                    addSign(sign).thenAccept(addSignSuccess -> {
                        if (addSignSuccess) {
                            completableFuture.complete(true);
                        } else {
                            completableFuture.cancel(true);
                        }
                    });
                } else {
                    completableFuture.cancel(true);
                }
            });
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    /**
     * Updates the configuration
     *
     * @param signLayoutConfig to be updated
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> updateSignConfig(SignLayoutConfig signLayoutConfig) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable) {
            Document document = new Document();
            document.append("layout_config",
                            this.webInterface.getGson()
                                             .toJsonTree(signLayoutConfig,
                                                         TypeToken.get(SignLayoutConfig.class).getType()));
            document.saveAsConfig(this.signConfigurationFile);
            CloudNet.getInstance().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }
}
