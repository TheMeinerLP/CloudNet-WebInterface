package me.madfix.cloudnet.webinterface.sign;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtils;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SignDatabase extends DatabaseUsable {

    /**
     * Initiated the sign database.
     *
     * @param database ths basic database
     */
    public SignDatabase(Database database) {
        super(database);
        Document document = database.getDocument("signs");
        if (document == null) {
            database.insert(new DatabaseDocument("signs").append("signs", new Document()));
        }
    }

    /**
     * Add a sign to config.
     *
     * @param sign sign layout
     * @return the database with all signs
     */
    public SignDatabase appendSign(Sign sign) {
        Document x = this.database.getDocument("signs");
        Document document = x.getDocument("signs");
        document.append(sign.getUniqueId().toString(), Document.GSON.toJsonTree(sign));
        this.database.insert(document);
        return this;
    }

    /**
     * Remove a sign via uuid.
     *
     * @param uniqueId the id of the sign
     * @return the database with all signs
     */
    public SignDatabase removeSign(UUID uniqueId) {
        Document x = this.database.getDocument("signs");
        Document document = x.getDocument("signs");
        document.remove(uniqueId.toString());
        this.database.insert(document);
        return this;
    }

    /**
     * Load all signs from the database.
     *
     * @return the map of signs
     */
    public Map<UUID, Sign> loadAll() {
        Document x = this.database.getDocument("signs");
        Document document = x.getDocument("signs");
        Map<UUID, Sign> signMap = document.keys().stream().collect(Collectors
                .toMap(UUID::fromString,
                        s -> JsonUtils.getGson().fromJson(document.get(s), Sign.class)));
        return signMap;
    }
}