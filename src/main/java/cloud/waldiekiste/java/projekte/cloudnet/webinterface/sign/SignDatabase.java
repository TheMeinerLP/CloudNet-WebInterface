/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUseable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
@SuppressWarnings("deprecation")
public class SignDatabase extends DatabaseUseable {
    public SignDatabase(final Database database) {
        super(database);
        final Document document = database.getDocument("signs");
        if (document == null) {
            database.insert(new DatabaseDocument("signs").append("signs", new Document()));
        }
    }

    public SignDatabase appendSign(final Sign sign) {
        final Document x = this.database.getDocument("signs");
        final Document document = x.getDocument("signs");
        document.append(sign.getUniqueId().toString(), sign);
        this.database.insert(document);
        return this;
    }

    public SignDatabase removeSign(final UUID uniqueId) {
        final Document x = this.database.getDocument("signs");
        final Document document = x.getDocument("signs");
        document.remove(uniqueId.toString());
        this.database.insert(new Document[] { document });
        return this;
    }

    public Map<UUID, Sign> loadAll() {
        final Document x = this.database.getDocument("signs");
        final Document document = x.getDocument("signs");
        final Type typeToken = new TypeToken<Sign>() {}.getType();
        final Map<UUID, Sign> signs = new LinkedHashMap<UUID, Sign>();
        for (final String key : document.keys()) {
            signs.put(UUID.fromString(key), document.getObject(key, typeToken));
        }
        return signs;
    }
}
