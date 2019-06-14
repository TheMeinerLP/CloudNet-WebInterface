/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission;

import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConfigPermissions {

    private final Path path;
    private Configuration cache;

    @SuppressWarnings("unchecked")
    public ConfigPermissions() {
        this.path = Paths.get("local/perms.yml");
        this.loadCache();
    }

    public void updatePermissionGroup(final PermissionGroup permissionGroup) {
        this.write(permissionGroup, this.cache);
        try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(this.path), StandardCharsets.UTF_8)) {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.cache, outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PermissionGroup> loadAll0() {
        return this.read(this.cache);
    }

    private void loadCache() {
        try (final InputStream inputStream = Files.newInputStream(this.path);
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            this.cache = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(final PermissionGroup permissionGroup, final Configuration configuration) {
        final Configuration section = configuration.getSection("groups");
        final Configuration group = new Configuration();
        group.set("prefix", permissionGroup.getPrefix());
        group.set("suffix", permissionGroup.getSuffix());
        group.set("display",permissionGroup.getDisplay());
        group.set("tagId", permissionGroup.getTagId());
        group.set("joinPower",permissionGroup.getJoinPower());
        group.set("defaultGroup",permissionGroup.isDefaultGroup());
        final Collection<String> perms = new CopyOnWriteArrayList<>();
        for (final Map.Entry<String, Boolean> entry : permissionGroup.getPermissions().entrySet()) {
            perms.add((entry.getValue() ? "" : "-") + entry.getKey());
        }
        group.set("permissions", perms);
        final Configuration permsCfg = new Configuration();
        for (final Map.Entry<String, List<String>> keys : permissionGroup.getServerGroupPermissions().entrySet()) {
            permsCfg.set(keys.getKey(), keys.getValue());
        }
        group.set("serverGroupPermissions", permsCfg);
        if (permissionGroup.getOptions().size() == 0) {
            permissionGroup.getOptions().put("test_option", true);
        }
        group.set("options", permissionGroup.getOptions());
        group.set("implements", permissionGroup.getImplementGroups());
        section.set(permissionGroup.getName(), null);
        section.set(permissionGroup.getName(), group);
    }

    private Map<String, PermissionGroup> read(final Configuration configuration) {
        final Map<String, PermissionGroup> maps = new LinkedHashMap<>();
        final Configuration section = configuration.getSection("groups");
        for (final String key : section.getKeys()) {
            final Configuration group = section.getSection(key);
            final HashMap<String, Boolean> permissions = new HashMap<>();
            final List<String> permissionSection = group.getStringList("permissions");
            for (final String entry : permissionSection) {
                permissions.put(entry.replaceFirst("-", ""), !entry.startsWith("-"));
            }
            final HashMap<String, List<String>> permissionsGroups = new HashMap<>();
            final Configuration permissionSectionGroups = group.getSection("serverGroupPermissions");
            for (final String entry2 : permissionSectionGroups.getKeys()) {
                permissionsGroups.put(entry2, permissionSectionGroups.getStringList(entry2));
            }
            final PermissionGroup permissionGroup = new PermissionGroup(key, group.getString("prefix"),group.getString("color"), group.getString("suffix"), group.getString("display"), group.getInt("tagId"), group.getInt("joinPower"), group.getBoolean("defaultGroup"), permissions, permissionsGroups, group.getSection("options").self, group.getStringList("implements"));
            maps.put(permissionGroup.getName(), permissionGroup);
        }
        return maps;
    }

    public boolean isEnabled() {
        return this.cache.getBoolean("enabled");
    }
}