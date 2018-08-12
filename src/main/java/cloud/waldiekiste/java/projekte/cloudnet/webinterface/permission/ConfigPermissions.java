package cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.Return;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigPermissions {private final Path path;
    private Configuration cache;

    public ConfigPermissions() throws Exception {
        this.path = Paths.get("local/perms.yml");
        if (!Files.exists(this.path)) {
            Files.createFile(this.path, (FileAttribute<?>[])new FileAttribute[0]);
            final Configuration configuration = new Configuration();
            configuration.set("enabled", true);
            configuration.set("groups", new Configuration());
            if (!Files.exists(Paths.get("local/permissions.yml", new String[0]), new LinkOption[0])) {
                final PermissionGroup member = new PermissionGroup("default", "§eMember §7\u258e ", "§f", "§e", 9999, 0, true, new HashMap(), MapWrapper.valueableHashMap(new Return[] { new Return((Object)"Lobby", (Object) Collections.singletonList("test.permission.for.group.Lobby")) }), (Map)new HashMap(), (List)new ArrayList());
                this.write(member, configuration);
                final PermissionGroup admin = new PermissionGroup("Admin", "§cAdmin §7\u258e ", "§f", "§c", 0, 100, false, (HashMap)MapWrapper.valueableHashMap(new Return[] { new Return((Object)"*", (Object)true) }), MapWrapper.valueableHashMap(new Return[] { new Return((Object)"Lobby", (Object)Arrays.asList("test.permission.for.group.Lobby")) }), (Map)new HashMap(), (List)new ArrayList());
                this.write(admin, configuration);
            }
            else {
                final Document document = Document.loadDocument(Paths.get("local/permissions.yml", new String[0]));
                final Collection<PermissionGroup> groups = (Collection<PermissionGroup>)document.getObject("groups", new TypeToken<Collection<PermissionGroup>>() {}.getType());
                final Map<String, PermissionGroup> maps = (Map<String, PermissionGroup>)MapWrapper.collectionCatcherHashMap((Collection)groups, (Catcher) (Catcher<String, PermissionGroup>) PermissionGroup::getName);
                configuration.set("enabled", (Object)document.getBoolean("enabled"));
                for (final PermissionGroup value : maps.values()) {
                    this.write(value, configuration);
                }
                Files.deleteIfExists(Paths.get("local/permissions.yml", new String[0]));
            }
            try (final OutputStream outputStream = Files.newOutputStream(this.path, new OpenOption[0]);
                 final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider((Class)YamlConfiguration.class).save(configuration, (Writer)outputStreamWriter);
            }
        }
        this.loadCache();
    }

    public void updatePermissionGroup(final PermissionGroup permissionGroup) {
        if (this.cache == null) {
            this.loadCache();
        }
        this.write(permissionGroup, this.cache);
        try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(this.path, new OpenOption[0]), StandardCharsets.UTF_8)) {
            ConfigurationProvider.getProvider((Class)YamlConfiguration.class).save(this.cache, (Writer)outputStreamWriter);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PermissionGroup> loadAll0() {
        this.loadCache();
        return this.read(this.cache);
    }

    public Map<String, PermissionGroup> loadAll() {
        if (this.cache == null) {
            this.loadCache();
        }
        return this.read(this.cache);
    }

    private void loadCache() {
        try (final InputStream inputStream = Files.newInputStream(this.path, new OpenOption[0]);
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            this.cache = ConfigurationProvider.getProvider((Class)YamlConfiguration.class).load((Reader)inputStreamReader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(final PermissionGroup permissionGroup, final Configuration configuration) {
        final Configuration section = configuration.getSection("groups");
        final Configuration group = new Configuration();
        group.set("prefix", (Object)permissionGroup.getPrefix());
        group.set("suffix", (Object)permissionGroup.getSuffix());
        group.set("display", (Object)permissionGroup.getDisplay());
        group.set("tagId", (Object)permissionGroup.getTagId());
        group.set("joinPower", (Object)permissionGroup.getJoinPower());
        group.set("defaultGroup", (Object)permissionGroup.isDefaultGroup());
        final Collection<String> perms = new CopyOnWriteArrayList<String>();
        for (final Map.Entry<String, Boolean> entry : permissionGroup.getPermissions().entrySet()) {
            perms.add((entry.getValue() ? "" : "-") + entry.getKey());
        }
        group.set("permissions", (Object)perms);
        final Configuration permsCfg = new Configuration();
        for (final Map.Entry<String, List<String>> keys : permissionGroup.getServerGroupPermissions().entrySet()) {
            permsCfg.set((String)keys.getKey(), (Object)keys.getValue());
        }
        group.set("serverGroupPermissions", (Object)permsCfg);
        if (permissionGroup.getOptions().size() == 0) {
            permissionGroup.getOptions().put("test_option", true);
        }
        group.set("options", (Object)permissionGroup.getOptions());
        group.set("implements", (Object)permissionGroup.getImplementGroups());
        section.set(permissionGroup.getName(), (Object)null);
        section.set(permissionGroup.getName(), (Object)group);
    }

    private Map<String, PermissionGroup> read(final Configuration configuration) {
        final Map<String, PermissionGroup> maps = new LinkedHashMap<String, PermissionGroup>();
        final Configuration section = configuration.getSection("groups");
        for (final String key : section.getKeys()) {
            final Configuration group = section.getSection(key);
            final HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
            final List<String> permissionSection = (List<String>)group.getStringList("permissions");
            for (final String entry : permissionSection) {
                permissions.put(entry.replaceFirst("-", ""), !entry.startsWith("-"));
            }
            final HashMap<String, List<String>> permissionsGroups = new HashMap<String, List<String>>();
            final Configuration permissionSectionGroups = group.getSection("serverGroupPermissions");
            for (final String entry2 : permissionSectionGroups.getKeys()) {
                permissionsGroups.put(entry2, permissionSectionGroups.getStringList(entry2));
            }
            final PermissionGroup permissionGroup = new PermissionGroup(key, group.getString("prefix"), group.getString("suffix"), group.getString("display"), group.getInt("tagId"), group.getInt("joinPower"), group.getBoolean("defaultGroup"), (HashMap)permissions, (Map)permissionsGroups, group.getSection("options").self, group.getStringList("implements"));
            maps.put(permissionGroup.getName(), permissionGroup);
        }
        return maps;
    }

    public boolean isEnabled() {
        this.loadCache();
        return this.cache.getBoolean("enabled");
    }

    public boolean isEnabled0() {
        if (this.cache == null) {
            this.loadCache();
        }
        return this.cache.getBoolean("enabled");
    }
}
