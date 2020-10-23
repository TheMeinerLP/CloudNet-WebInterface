package me.madfix.cloudnet.webinterface.database;

import com.google.gson.Gson;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SignDatabase extends DatabaseUsable {

    private Document selectorSigns;
    private final Gson gson = new Gson();

    /**
     * Initiated the sign database.
     *
     * @param database ths basic database
     */
    public SignDatabase(Database database) {
        super(database);
        this.selectorSigns = database.getDocument("signs");
        if (this.selectorSigns == null) {
            this.selectorSigns = new Document();
            this.selectorSigns.append("signs", new Document());
            database.insert(this.selectorSigns);
        }
    }

    /**
     * Add a sign to config.
     *
     * @param sign sign layout
     * @return the database with all signs
     */
    public SignDatabase add(Sign sign) {
        this.selectorSigns.append(sign.getUniqueId().toString(), this.gson.toJsonTree(sign));
        this.database.insert(this.selectorSigns);
        return this;
    }

    /**
     * Remove a sign via uuid.
     *
     * @param uniqueId the id of the sign
     * @return the database with all signs
     */
    public SignDatabase removeSign(UUID uniqueId) {
        this.selectorSigns.remove(uniqueId.toString());
        this.database.insert(this.selectorSigns);
        return this;
    }

    /**
     * Load all signs from the database.
     *
     * @return the map of signs
     */
    public Map<UUID, Sign> loadAll() {
        return this.selectorSigns.keys()
                                 .stream()
                                 .collect(Collectors.toMap(UUID::fromString,
                                                           s -> this.gson.fromJson(this.selectorSigns.get(s),
                                                                                   Sign.class)));
    }

}
