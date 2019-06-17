package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobPosition;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;
import java.lang.reflect.Type;
import java.util.UUID;

public class ServerMobJsonAdapter implements JsonSerializer<ServerMob>,
    JsonDeserializer<ServerMob> {

  @Override
  public ServerMob deserialize(JsonElement jsonElement, Type type1,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    UUID uniqueId = UUID.fromString(object.get("uniqueId").getAsString());
    String display = object.get("display").getAsString();
    String name = object.get("name").getAsString();
    String itemName = object.get("itemName").getAsString();
    String type = object.get("type").getAsString();
    String targetGroup = object.get("targetGroup").getAsString();
    int itemId = object.get("itemId").getAsInt();
    boolean autoJoin = object.get("autoJoin").getAsBoolean();
    MobPosition position = jsonDeserializationContext
        .deserialize(object.get("position"), MobPosition.class);
    String displayMessage = object.get("displayMessage").getAsString();
    Document metaDataDoc = new Document(object.get("metaDataDoc").getAsJsonObject().toString());
    return new ServerMob(uniqueId, display, name, type, targetGroup, itemId, itemName, autoJoin,
        position, displayMessage, metaDataDoc);
  }

  @Override
  public JsonElement serialize(ServerMob serverMob, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("uniqueId", serverMob.getUniqueId().toString());
    object.addProperty("display", serverMob.getDisplay());
    object.addProperty("itemName", serverMob.getItemName());
    object.addProperty("name", serverMob.getName());
    object.addProperty("type", serverMob.getType());
    object.addProperty("targetGroup", serverMob.getTargetGroup());
    object.addProperty("itemId", serverMob.getItemId());
    object.addProperty("autoJoin", serverMob.getAutoJoin());
    object.add("position", jsonSerializationContext.serialize(serverMob.getPosition()));
    object.addProperty("displayMessage", serverMob.getDisplayMessage());
    object.add("metaDataDoc", serverMob.getMetaDataDoc().obj());
    return object;
  }
}
