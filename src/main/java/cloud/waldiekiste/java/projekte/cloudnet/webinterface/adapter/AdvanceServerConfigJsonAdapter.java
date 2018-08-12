package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;

import java.lang.reflect.Type;

public class AdvanceServerConfigJsonAdapter implements JsonSerializer<AdvancedServerConfig>,JsonDeserializer<AdvancedServerConfig> {
    @Override
    public AdvancedServerConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final boolean notifyPlayerUpdatesFromNoCurrentPlayer = object.get("notifyPlayerUpdatesFromNoCurrentPlayer").getAsBoolean();
        final boolean notifyProxyUpdates = object.get("notifyProxyUpdates").getAsBoolean();
        final boolean notifyServerUpdates = object.get("notifyServerUpdates").getAsBoolean();
        final boolean disableAutoSavingForWorlds = object.get("disableAutoSavingForWorlds").getAsBoolean();
        return new AdvancedServerConfig(notifyPlayerUpdatesFromNoCurrentPlayer,notifyProxyUpdates,notifyServerUpdates,disableAutoSavingForWorlds);
    }

    @Override
    public JsonElement serialize(AdvancedServerConfig advancedServerConfig, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("notifyPlayerUpdatesFromNoCurrentPlayer",advancedServerConfig.isNotifyPlayerUpdatesFromNoCurrentPlayer());
        object.addProperty("notifyProxyUpdates",advancedServerConfig.isNotifyProxyUpdates());
        object.addProperty("notifyServerUpdates",advancedServerConfig.isNotifyServerUpdates());
        object.addProperty("disableAutoSavingForWorlds",advancedServerConfig.isDisableAutoSavingForWorlds());
        return object;
    }
}
