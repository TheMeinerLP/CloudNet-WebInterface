package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import java.lang.reflect.Type;

public class ServerConfigJsonAdapter implements JsonSerializer<ServerConfig> {

  @Override
  public JsonElement serialize(ServerConfig serverConfig, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("hideServer", serverConfig.isHideServer());
    object.addProperty("extra", serverConfig.getExtra());
    object.addProperty("startup", serverConfig.getStartup());
    return object;
  }
}
