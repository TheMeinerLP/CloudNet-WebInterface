package me.madfix.cloudnet.webinterface.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.model.InterfaceConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;

public final class ConfigurationService {

    private InterfaceConfiguration optionalInterfaceConfiguration;
    private final Gson gson = new Gson();
    private final WebInterface webInterface;

    public ConfigurationService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    /**
     * Loads the file(interface.json) if available
     *
     * @return true if the file was found and loaded. If false returns no configuration file exists
     */
    public boolean loadConfigurationFile() {
        final Path configurationFile = Paths.get("interface.json");
        if (!Files.exists(configurationFile)) {
            return false;
        } else {
            try (BufferedReader configurationReader = Files.newBufferedReader(configurationFile, StandardCharsets.UTF_8)) { // Is needed to close the reader properly after reading the file
                Optional<JsonElement> jsonConfig;
                try {
                    jsonConfig = Optional.of(JsonParser.parseReader(configurationReader));
                } catch (JsonSyntaxException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "[101] An unexpected error occurred while reading the configuration file ", e);
                    return false;
                }
                jsonConfig.ifPresent(jsonElement -> this.optionalInterfaceConfiguration = this.gson.fromJson(jsonElement, InterfaceConfiguration.class));
            } catch (IOException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "[102] An unexpected error occurred while reading the configuration file ", e);
                return false;
            }
        }
        return true;
    }

    public Optional<InterfaceConfiguration> getOptionalInterfaceConfiguration() {
        return Optional.ofNullable(optionalInterfaceConfiguration);
    }
}
