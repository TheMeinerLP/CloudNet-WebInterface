package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import java.lang.reflect.Type;

/**
 * Translate the the AdvancedServerConfig into a Json String and back.
 */
public class AdvanceServerConfigJsonAdapter implements JsonSerializer<AdvancedServerConfig>,
    JsonDeserializer<AdvancedServerConfig> {

  /**
   * Translate into Class from Json String.
   *
   * @param jsonElement The input json to translate
   * @param type The type of the translate class
   * @param jsonDeserializationContext The context to use other JsonAdpater
   * @return The AdvancedServerConfig with all Json values
   * @throws JsonParseException Throws a Exception is the Json string incorrect
   */
  @Override
  public AdvancedServerConfig deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    final boolean notifyPlayerUpdatesFromNoCurrentPlayer = object
        .get("notifyPlayerUpdatesFromNoCurrentPlayer").getAsBoolean();
    final boolean notifyProxyUpdates = object.get("notifyProxyUpdates").getAsBoolean();
    final boolean notifyServerUpdates = object.get("notifyServerUpdates").getAsBoolean();
    final boolean disableAutoSavingForWorlds = object.get("disableAutoSavingForWorlds")
        .getAsBoolean();
    return new AdvancedServerConfig(notifyPlayerUpdatesFromNoCurrentPlayer, notifyProxyUpdates,
        notifyServerUpdates, disableAutoSavingForWorlds);
  }

  /**
   * Translate Java class into Json String.
   *
   * @param advancedServerConfig The input class to translate into Json
   * @param type The type of the translate class
   * @param jsonSerializationContext The context to use other Json Adapter
   * @return The JsonElement with values of the java class
   */
  @Override
  public JsonElement serialize(AdvancedServerConfig advancedServerConfig, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("notifyPlayerUpdatesFromNoCurrentPlayer",
        advancedServerConfig.isNotifyPlayerUpdatesFromNoCurrentPlayer());
    object.addProperty("notifyProxyUpdates", advancedServerConfig.isNotifyProxyUpdates());
    object.addProperty("notifyServerUpdates", advancedServerConfig.isNotifyServerUpdates());
    object.addProperty("disableAutoSavingForWorlds",
        advancedServerConfig.isDisableAutoSavingForWorlds());
    return object;
  }
}
