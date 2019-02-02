package cloud.waldiekiste.java.projekte.cloudnet.webinterface.mob;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;

public class MobDatabase extends DatabaseUsable
{
    public MobDatabase(final Database database) {
        super(database);
        Document document = database.getDocument("server_selector_mobs");
        if (document == null) {
            document = new DatabaseDocument("server_selector_mobs").append("mobs", new Document());
        }
        database.insert(new Document[] { document });
    }

    public void append(final ServerMob serverMob) {
        final Document document = this.database.getDocument("server_selector_mobs").getDocument("mobs").append(serverMob.getUniqueId().toString(), serverMob);
        this.database.insert(new Document[] { document });
    }

    public void remove(final ServerMob serverMob) {
        final Document document = this.database.getDocument("server_selector_mobs").getDocument("mobs").remove(serverMob.getUniqueId().toString());
        this.database.insert(new Document[] { document });
    }

    public Map<UUID, ServerMob> loadAll() {
        boolean injectable = false;
        final Map<UUID, ServerMob> mobMap = this.database.getDocument("server_selector_mobs").getObject("mobs", new TypeToken<Map<UUID, ServerMob>>() {}.getType());
        for (final ServerMob serverMob : mobMap.values()) {
            if (serverMob.getItemId() == null) {
                serverMob.setItemId(138);
                injectable = true;
            }
            if (serverMob.getAutoJoin() == null) {
                serverMob.setAutoJoin(false);
                injectable = true;
            }
        }
        if (injectable) {
            final Document document = this.database.getDocument("server_selector_mobs");
            document.append("mobs", mobMap);
            this.database.insert(new Document[] { document });
        }
        return mobMap;
    }
}
