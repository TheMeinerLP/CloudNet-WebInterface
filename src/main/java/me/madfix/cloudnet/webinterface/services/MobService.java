package me.madfix.cloudnet.webinterface.services;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.database.MobDatabase;

final class MobService {

  private boolean enable;
  private Optional<MobDatabase> mobDatabase;
  private final WebInterface webInterface;
  private final Path mobConfigurationFile = Paths.get("local", "servermob_config.json");

  public MobService(WebInterface webInterface) {
    this.webInterface = webInterface;
    this.mobDatabase = Optional.of(new MobDatabase(
        webInterface.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg")));
  }

  public CompletableFuture<Optional<Boolean>> isEnabled() {
    return CompletableFuture.completedFuture(Optional.of(this.enable));
  }

  public CompletableFuture<Optional<MobDatabase>> getMobDatabase() {
    return CompletableFuture.completedFuture(this.mobDatabase);
  }

  public CompletableFuture<Optional<MobConfig>> getMobConfig() {
    Optional<MobConfig> optionalMobConfig = Optional.empty();
    try {
      optionalMobConfig = Optional.of(this.webInterface.getGson()
          .fromJson(Files.newBufferedReader(this.mobConfigurationFile,
              StandardCharsets.UTF_8), TypeToken.get(MobConfig.class).getType()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return CompletableFuture.completedFuture(optionalMobConfig);
  }

  public CompletableFuture<Optional<ServerMob>> getServerMob(UUID mobId) {
    Optional<ServerMob> optionalServerMob = Optional.empty();
    if (this.mobDatabase.isPresent()) {
      optionalServerMob = this.mobDatabase.get().loadAll().values().stream()
          .filter(serverMob -> serverMob.getUniqueId().equals(mobId)).findFirst();
    }
    return CompletableFuture.completedFuture(optionalServerMob);
  }

  public CompletableFuture<Optional<Collection<ServerMob>>> getMobs() {
    Optional<Collection<ServerMob>> optionalServerMobList = Optional.empty();
    if (this.mobDatabase.isPresent()) {
      optionalServerMobList = Optional.of(this.mobDatabase.get().loadAll().values());
    }
    return CompletableFuture.completedFuture(optionalServerMobList);
  }

  public CompletableFuture<Optional<Boolean>> updateMob(ServerMob serverMob) {
    Optional<Boolean> success = Optional.of(false);
    if (this.mobDatabase.isPresent()) {
      try {
        final Optional<ServerMob> oldOptionalServerMob = this.getServerMob(serverMob.getUniqueId())
            .get();
        if (oldOptionalServerMob.isPresent()) {
          
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    return CompletableFuture.completedFuture(success);
  }
}
