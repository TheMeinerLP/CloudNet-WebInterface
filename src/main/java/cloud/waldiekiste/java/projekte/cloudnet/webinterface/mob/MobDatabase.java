package cloud.waldiekiste.java.projekte.cloudnet.webinterface.mob;

import com.google.gson.Gson;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MobDatabase extends DatabaseUsable {

  /**
   * Initiated mob database.
   * @param database the basic database
   */
  public MobDatabase(Database database) {
    super(database);
    Document document = database.getDocument("server_selector_mobs");
    if (document == null) {
      document = new DatabaseDocument("server_selector_mobs").append("mobs", new Document());
    }
    database.insert(document);
  }

  /**
   * Add a server mob to the database.
   * @param serverMob the mob to add
   */
  public void append(ServerMob serverMob) {
    Document document = this.database.getDocument("server_selector_mobs").getDocument("mobs")
        .append(serverMob.getUniqueId().toString(), Document.GSON.toJsonTree(serverMob));
    this.database.insert(document);
  }

  /**
   * Remove serverMob from the database.
   * @param serverMob the mob to remove from the database
   */
  public void remove(ServerMob serverMob) {
    Document document = this.database.getDocument("server_selector_mobs").getDocument("mobs")
        .remove(serverMob.getUniqueId().toString());
    this.database.insert(document);
  }

  /**
   * Load all mobs from the database.
   * @return get a map of mobs with uuids
   */
  public Map<UUID, ServerMob> loadAll() {
    Gson gson = new Gson();
    HashMap<UUID, ServerMob> mobMap = (HashMap<UUID, ServerMob>)
        gson.fromJson(this.database.getDocument("server_selector_mobs")
            .get("mobs"), HashMap.class);
    mobMap.values().stream().filter(serverMob -> serverMob.getItemId() == null)
        .forEach(serverMob -> serverMob.setItemId(138));
    mobMap.values().stream().filter(serverMob -> serverMob.getAutoJoin() == null)
        .forEach(serverMob -> serverMob.setAutoJoin(false));
    Document document = this.database.getDocument("server_selector_mobs");
    document.append("mobs", Document.GSON.toJsonTree(mobMap));
    this.database.insert(document);
    return mobMap;
  }
}