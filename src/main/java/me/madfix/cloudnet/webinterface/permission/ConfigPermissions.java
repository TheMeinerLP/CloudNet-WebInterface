package me.madfix.cloudnet.webinterface.permission;

import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class ConfigPermissions {

  private final Path path;
  private Configuration cache;

  public ConfigPermissions() {
    this.path = Paths.get("local/perms.yml");
    this.loadCache();
  }

  /**
   * Write the update of the permission group into the file.
   * @param permissionGroup The permission group to update
   */
  public void updatePermissionGroup(PermissionGroup permissionGroup) {
    this.write(permissionGroup, this.cache);
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
        Files.newOutputStream(this.path),
        StandardCharsets.UTF_8)) {
      ConfigurationProvider.getProvider(YamlConfiguration.class)
          .save(this.cache, outputStreamWriter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Map<String, PermissionGroup> loadAll() {
    return this.read(this.cache);
  }

  private void loadCache() {
    try (final InputStream inputStream = Files.newInputStream(this.path);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
            StandardCharsets.UTF_8)) {
      this.cache = ConfigurationProvider.getProvider(YamlConfiguration.class)
          .load(inputStreamReader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void write(PermissionGroup permissionGroup, Configuration configuration) {
    Configuration group = new Configuration();
    group.set("prefix", permissionGroup.getPrefix());
    group.set("suffix", permissionGroup.getSuffix());
    group.set("display", permissionGroup.getDisplay());
    group.set("tagId", permissionGroup.getTagId());
    group.set("joinPower", permissionGroup.getJoinPower());
    group.set("defaultGroup", permissionGroup.isDefaultGroup());
    group.set("permissions",
        permissionGroup.getPermissions().entrySet().stream().map((entry) ->
            (entry.getValue() ? "" : "-") + entry.getKey()).collect(Collectors.toList()));
    Configuration permsCfg = new Configuration();
    for (Map.Entry<String, List<String>> keys : permissionGroup.getServerGroupPermissions()
        .entrySet()) {
      permsCfg.set(keys.getKey(), keys.getValue());
    }
    group.set("serverGroupPermissions", permsCfg);
    if (permissionGroup.getOptions().size() == 0) {
      permissionGroup.getOptions().put("test_option", true);
    }
    group.set("options", permissionGroup.getOptions());
    group.set("implements", permissionGroup.getImplementGroups());
    Configuration section = configuration.getSection("groups");
    section.set(permissionGroup.getName(), group);
  }

  private Map<String, PermissionGroup> read(Configuration configuration) {
    Map<String, PermissionGroup> maps = new LinkedHashMap<>();
    Configuration section = configuration.getSection("groups");
    for (String key : section.getKeys()) {
      Configuration group = section.getSection(key);
      HashMap<String, Boolean> permissions = new HashMap<>();
      List<String> permissionSection = group.getStringList("permissions");
      for (String entry : permissionSection) {
        permissions.put(entry.replaceFirst("-", ""), !entry.startsWith("-"));
      }
      HashMap<String, List<String>> permissionsGroups = new HashMap<>();
      Configuration permissionSectionGroups = group.getSection("serverGroupPermissions");
      for (String entry2 : permissionSectionGroups.getKeys()) {
        permissionsGroups.put(entry2, permissionSectionGroups.getStringList(entry2));
      }
      PermissionGroup permissionGroup = new PermissionGroup(key, group.getString("prefix"),
          group.getString("color"), group.getString("suffix"), group.getString("display"),
          group.getInt("tagId"), group.getInt("joinPower"), group.getBoolean("defaultGroup"),
          permissions, permissionsGroups, group.getSection("options").self,
          group.getStringList("implements"));
      maps.put(permissionGroup.getName(), permissionGroup);
    }
    return maps;
  }

  public boolean isEnabled() {
    return this.cache.getBoolean("enabled");
  }
}