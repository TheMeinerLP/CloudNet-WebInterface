package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import java.lang.reflect.Type;

public class WrapperInfoAdpater implements JsonDeserializer<WrapperInfo>,
    JsonSerializer<WrapperInfo> {

  @Override
  public WrapperInfo deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    String serverId = object.get("serverId").getAsString();
    String hostName = object.get("hostName").getAsString();
    boolean ready = object.get("ready").getAsBoolean();
    int availableProcessors = object.get("availableProcessors").getAsInt();
    int startPort = object.get("startPort").getAsInt();
    int process_queue_size = object.get("process_queue_size").getAsInt();
    int memory = object.get("memory").getAsInt();
    return new WrapperInfo(serverId, hostName, ready, availableProcessors, startPort,
        process_queue_size, memory);
  }

  @Override
  public JsonElement serialize(WrapperInfo wrapperInfo, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("serverId", wrapperInfo.getServerId());
    object.addProperty("hostName", wrapperInfo.getHostName());
    object.addProperty("ready", wrapperInfo.isReady());
    object.addProperty("availableProcessors", wrapperInfo.getAvailableProcessors());
    object.addProperty("startPort", wrapperInfo.getStartPort());
    object.addProperty("process_queue_size", wrapperInfo.getProcess_queue_size());
    object.addProperty("memory", wrapperInfo.getMemory());
    return object;
  }
}
