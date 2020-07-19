package me.madfix.cloudnet.webinterface.services;


import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdatePlayer;
import me.madfix.cloudnet.webinterface.WebInterface;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

final class CloudPermissionService {

    private final WebInterface webInterface;
    private boolean enable;
    private PermissionPool permissionPool;
    private final Path permissionConfigurationFile = Paths.get("local",
            "perms.yml");
    private Configuration cache;

    CloudPermissionService(WebInterface webInterface) {
        this.webInterface = webInterface;
        this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration()
                .ifPresent(interfaceConfiguration -> {
                    this.enable = interfaceConfiguration.isPermissionSystem();
                    if (this.enable) {
                        this.permissionPool =
                                this.webInterface.getCloud().getNetworkManager().getModuleProperties()
                                        .getObject("permissionPool",
                                                PermissionPool.TYPE);
                        this.loadCache();
                    }
                });
    }

    /**
     * Allows you to check if the cloud permission system is active
     *
     * @return true will return if the system is active
     */
    public CompletableFuture<Boolean> isEnabled() {
        return CompletableFuture.completedFuture(this.enable);
    }

    /**
     * Returns the permission pool
     *
     * @return the pool itself
     */
    public CompletableFuture<PermissionPool> getPermissionPool() {
        CompletableFuture<PermissionPool> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            completableFuture.complete(this.permissionPool);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * Returns a group by the name
     *
     * @return the group instance as a completable future
     */
    public CompletableFuture<PermissionGroup> getGroup(String groupName) {
        CompletableFuture<PermissionGroup> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            completableFuture.complete(this.permissionPool.getGroups().get(groupName));
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * @return returns a collection of groups from the system
     */
    public CompletableFuture<Collection<PermissionGroup>> getGroups() {
        CompletableFuture<Collection<PermissionGroup>> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            completableFuture.complete(this.permissionPool.getGroups().values());
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * Returns an offline player instance by its UUID
     *
     * @param uniquePlayerId is the unique id of mojang to identify the player
     * @return a completeable future with the offline player
     */
    public CompletableFuture<OfflinePlayer> getPlayer(UUID uniquePlayerId) {
        CompletableFuture<OfflinePlayer> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            completableFuture.complete(
                    this.webInterface.getCloud().getDbHandlers().getPlayerDatabase().getPlayer(uniquePlayerId));
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * Returns an offline player instance by its in game name
     *
     * @param playerName is the in game name of the player
     * @return a completable future with the offline player
     */
    public CompletableFuture<OfflinePlayer> getPlayer(String playerName) {
        CompletableFuture<OfflinePlayer> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            UUID uuid = this.webInterface.getCloud().getDbHandlers().getNameToUUIDDatabase().get(playerName);
            if (uuid != null) {
                completableFuture.complete(
                        this.webInterface.getCloud().getDbHandlers().getPlayerDatabase().getPlayer(uuid));
            } else {
                completableFuture.completeExceptionally(new NullPointerException("The UUID is null or was not found in the CloudNet database"));
            }
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * Creates and updates a group
     *
     * @param permissionGroup to be updated or created
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> updateGroup(PermissionGroup permissionGroup) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            updatePermissionGroup(permissionGroup);
            NetworkUtils.addAll(this.permissionPool.getGroups(),
                    loadAll());
            this.webInterface.getCloud().getNetworkManager().getModuleProperties().append("permissionPool",
                    this.permissionPool);
            this.webInterface.getCloud().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * Updates a player in the database and also online when he is online
     *
     * @param offlinePlayer to be updated
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> updatePlayer(OfflinePlayer offlinePlayer) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            this.webInterface.getCloud().getDbHandlers().getPlayerDatabase()
                    .updatePermissionEntity(offlinePlayer
                                    .getUniqueId(),
                            offlinePlayer.getPermissionEntity());

            this.webInterface.getCloud().getNetworkManager()
                    .sendAllUpdate(new PacketOutUpdateOfflinePlayer(this.webInterface.getCloud().getDbHandlers()
                            .getPlayerDatabase()
                            .getPlayer(offlinePlayer.getUniqueId())));

            CloudPlayer onlinePlayer = this.webInterface.getCloud().getNetworkManager()
                    .getOnlinePlayer(offlinePlayer.getUniqueId());
            if (onlinePlayer != null) {
                onlinePlayer.setPermissionEntity(offlinePlayer.getPermissionEntity());
                this.webInterface.getCloud().getNetworkManager()
                        .sendAllUpdate(new PacketOutUpdatePlayer(onlinePlayer));
            }
            this.webInterface.getCloud().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    /**
     * Removes a permission group from the system
     *
     * @param name identifies the name of the group
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> removeGroup(String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.enable && this.permissionPool.isAvailable()) {
            // Maybe remove from file ?
            this.permissionPool.getGroups().remove(name);
            this.webInterface.getCloud().getNetworkManager().getModuleProperties().append("permissionPool",
                    this.permissionPool);
            this.webInterface.getCloud().getNetworkManager().updateAll();
            completableFuture.complete(true);
        } else {
            completableFuture.completeExceptionally(new RuntimeException("Cloud permission is not active"));
        }
        return completableFuture;
    }

    private void updatePermissionGroup(PermissionGroup permissionGroup) {
        if (this.enable) {
            this.write(permissionGroup,
                    this.cache);
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    Files.newOutputStream(this.permissionConfigurationFile),
                    StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class)
                        .save(this.cache,
                                outputStreamWriter);
            } catch (IOException e) {
                this.webInterface.getLogger().log(Level.SEVERE,
                        "An unexpected error occurred while writing the permission file",
                        e);
            }
        }
    }

    private Map<String, PermissionGroup> loadAll() {
        return this.read(this.cache);
    }

    private void loadCache() {
        if (this.enable) {
            try (InputStream inputStream = Files.newInputStream(this.permissionConfigurationFile);
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                         StandardCharsets.UTF_8)) {
                this.cache = ConfigurationProvider.getProvider(YamlConfiguration.class)
                        .load(inputStreamReader);
            } catch (IOException e) {
                this.webInterface.getLogger().log(Level.SEVERE,
                        "An unexpected error occurred while reading the permission file ",
                        e);
            }
        }
    }

    private void write(PermissionGroup permissionGroup, Configuration configuration) {
        if (this.enable) {
            Configuration group = new Configuration();
            group.set("prefix",
                    permissionGroup.getPrefix());
            group.set("suffix",
                    permissionGroup.getSuffix());
            group.set("display",
                    permissionGroup.getDisplay());
            group.set("tagId",
                    permissionGroup.getTagId());
            group.set("joinPower",
                    permissionGroup.getJoinPower());
            group.set("defaultGroup",
                    permissionGroup.isDefaultGroup());
            group.set("permissions",
                    permissionGroup.getPermissions().entrySet().stream().map((entry) ->
                            (entry.getValue() ? "" : "-") + entry.getKey()).collect(Collectors.toList()));
            Configuration permsCfg = new Configuration();
            for (Map.Entry<String, List<String>> keys : permissionGroup.getServerGroupPermissions()
                    .entrySet()) {
                permsCfg.set(keys.getKey(),
                        keys.getValue());
            }
            group.set("serverGroupPermissions",
                    permsCfg);
            if (permissionGroup.getOptions().size() == 0) {
                permissionGroup.getOptions().put("test_option",
                        true);
            }
            group.set("options",
                    permissionGroup.getOptions());
            group.set("implements",
                    permissionGroup.getImplementGroups());
            Configuration section = configuration.getSection("groups");
            section.set(permissionGroup.getName(),
                    group);
        }
    }

    private Map<String, PermissionGroup> read(Configuration configuration) {
        Map<String, PermissionGroup> maps = new HashMap<>();
        if (this.enable) {
            Configuration section = configuration.getSection("groups");
            for (String key : section.getKeys()) {
                Configuration group = section.getSection(key);
                HashMap<String, Boolean> permissions = new HashMap<>();
                List<String> permissionSection = group.getStringList("permissions");
                for (String permissionEntry : permissionSection) {
                    permissions.put(permissionEntry.replaceFirst("-",
                            ""),
                            !permissionEntry.startsWith("-"));
                }
                HashMap<String, List<String>> permissionsGroups = new HashMap<>();
                Configuration permissionSectionGroups = group.getSection("serverGroupPermissions");
                for (String permissionGroupEntry : permissionSectionGroups.getKeys()) {
                    permissionsGroups.put(permissionGroupEntry,
                            permissionSectionGroups.getStringList(permissionGroupEntry));
                }
                PermissionGroup permissionGroup = new PermissionGroup(key,
                        group.getString("prefix"),
                        group.getString("color"),
                        group.getString("suffix"),
                        group.getString("display"),
                        group.getInt("tagId"),
                        group.getInt("joinPower"),
                        group.getBoolean("defaultGroup"),
                        permissions,
                        permissionsGroups,
                        group.getSection("options").self,
                        group.getStringList("implements"));
                maps.put(permissionGroup.getName(),
                        permissionGroup);
            }
        }
        return maps;
    }

}
