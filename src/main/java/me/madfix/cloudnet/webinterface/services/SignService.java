package me.madfix.cloudnet.webinterface.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
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
import java.util.List;
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
        this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().ifPresent(interfaceConfiguration -> {
            this.enable = interfaceConfiguration.isSignSystem();
            if (this.enable) this.signDatabase = new SignDatabase(
                    webInterface.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
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
    public CompletableFuture<Optional<SignDatabase>> geSignDatabase() {
        CompletableFuture<Optional<SignDatabase>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            optionalCompletableFuture.complete(Optional.ofNullable(this.signDatabase));
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    /**
     * Returns the current sign configuration
     *
     * @return contains everything about layout information
     */
    public CompletableFuture<Optional<SignLayoutConfig>> getSignConfig() {
        CompletableFuture<Optional<SignLayoutConfig>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(this.signConfigurationFile,
                    StandardCharsets.UTF_8)) {
                Optional<JsonElement> jsonConfig = Optional.empty();
                try {
                    jsonConfig = Optional.of(JsonParser.parseReader(bufferedReader));
                } catch (JsonSyntaxException e) {
                    this.enable = false;
                    CloudNet.getLogger().severe("[401] Sign service is deactivated to prevent errors. Please fix the errors and try the function again.");
                    CloudNet.getLogger().log(Level.SEVERE, "[401] An unexpected error occurred while reading the configuration file.", e);
                }
                jsonConfig.ifPresent(jsonElement -> optionalCompletableFuture.complete(Optional.of(this.webInterface.getGson()
                        .fromJson(jsonElement, TypeToken.get(SignLayoutConfig.class).getType()))));
            } catch (IOException e) {
                this.enable = false;
                CloudNet.getLogger().severe("[402] Sign service is deactivated to prevent errors. Please fix the errors and try the function again.");
                CloudNet.getLogger().log(Level.SEVERE, "[402] An unexpected error occurred while reading the configuration file.", e);
            }
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    public CompletableFuture<Optional<Sign>> getSign(UUID signId) {
        CompletableFuture<Optional<Sign>> optionalCompletableFuture = new CompletableFuture<>();
        if (this.enable && this.signDatabase != null) {
            optionalCompletableFuture.complete(this.signDatabase.loadAll().values()
                    .stream().filter(s -> s.getUniqueId().equals(signId)).findFirst());
        }
        return optionalCompletableFuture;
    }

    public CompletableFuture<Optional<Collection<Sign>>> getSigns() {
        CompletableFuture<Optional<Collection<Sign>>> collectionCompletableFuture = new CompletableFuture<>();
        return collectionCompletableFuture;
    }


}
