package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnetcore.network.NetworkInfo;
import java.lang.reflect.Type;

public class NetworkInfoJsonAdapter implements JsonSerializer<NetworkInfo> {

  @Override
  public JsonElement serialize(NetworkInfo networkInfo, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("serverId", networkInfo.getServerId());
    object.addProperty("hostName", networkInfo.getHostName());
    object.addProperty("port", networkInfo.getPort());
    return object;
  }
}
