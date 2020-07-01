package me.madfix.cloudnet.webinterface.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MobDatabase extends DatabaseUsable {

    private Document selectorMobs;
    private final Gson gson = new Gson();

    /**
     * Initiated mob database.
     *
     * @param database the basic database
     */
    public MobDatabase(Database database) {
        super(database);
        this.selectorMobs = database.getDocument("server_selector_mobs");
        if (selectorMobs == null) {
            this.selectorMobs = new Document();
            this.selectorMobs.append("mobs", new Document());
            database.insert(selectorMobs);
        }
    }

    /**
     * Add a server mob to the database.
     *
     * @param serverMob the mob to add
     */
    public void add(ServerMob serverMob) {
        this.selectorMobs
                .append(serverMob.getUniqueId().toString(), Document.GSON.toJsonTree(serverMob));
        this.database.insert(this.selectorMobs);
    }

    /**
     * Remove serverMob from the database.
     *
     * @param mobId the mob to remove from the database
     */
    public void remove(UUID mobId) {
        selectorMobs.remove(mobId.toString());
        this.database.insert(selectorMobs);
    }

    /**
     * Load all mobs from the database.
     *
     * @return get a map of mobs with uuids
     */
    public Map<UUID, ServerMob> loadAll() {
        HashMap<UUID, ServerMob> mobMap = gson.fromJson(this.selectorMobs.get("mobs"),
                TypeToken.getParameterized(HashMap.class, UUID.class, ServerMob.class).getType());
        mobMap.values().stream().filter(serverMob -> serverMob.getItemId() == null)
                .forEach(serverMob -> serverMob.setItemId(138));
        mobMap.values().stream().filter(serverMob -> serverMob.getAutoJoin() == null)
                .forEach(serverMob -> serverMob.setAutoJoin(false));
        this.selectorMobs.append("mobs", Document.GSON.toJsonTree(mobMap));
        this.database.insert(this.selectorMobs);
        return mobMap;
    }
}