package me.madfix.cloudnet.webinterface.services;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import me.madfix.cloudnet.webinterface.model.InterfaceConfiguration;

public final class ConfigurationService {

  private Optional<InterfaceConfiguration> optionalInterfaceConfiguration;
  private final Gson gson = new Gson();

  /**
   * Loads the file(interface.json) if available
   * @return true if the file was found and loaded. If false returns no configuration file exists
   */
  public boolean loadConfigurationFile() {
    final Path configurationFile = Paths.get("interface.json");
    if (!Files.exists(configurationFile)) {
      return false;
    } else {
      try {
        this.optionalInterfaceConfiguration = Optional.of(this.gson
            .fromJson(Files.newBufferedReader(configurationFile, StandardCharsets.UTF_8),
                InterfaceConfiguration.class));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  public Optional<InterfaceConfiguration> getOptionalInterfaceConfiguration() {
    return optionalInterfaceConfiguration;
  }
}
