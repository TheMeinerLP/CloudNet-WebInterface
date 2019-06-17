package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import java.lang.reflect.Type;
import java.util.Locale;

public class ServerInfoJsonAdapter implements JsonSerializer<ServerInfo> {

  @Override
  public JsonElement serialize(ServerInfo simpleServerInfo, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.add("serviceId", jsonSerializationContext.serialize(simpleServerInfo.getServiceId()));
    object.addProperty("host", simpleServerInfo.getHost());
    object.addProperty("port", simpleServerInfo.getPort());
    object.addProperty("maxplayers", simpleServerInfo.getMaxPlayers());
    object.addProperty("onlineCount", simpleServerInfo.getOnlineCount());
    object.addProperty("serverState",
        simpleServerInfo.getServerState().name().toUpperCase(Locale.ENGLISH));
    object.add("serverConfig",
        jsonSerializationContext.serialize(simpleServerInfo.getServerConfig()));
    object.add("template", jsonSerializationContext.serialize(simpleServerInfo.getTemplate()));
    object.addProperty("memory", simpleServerInfo.getMemory());
    object.addProperty("motd", simpleServerInfo.getMotd());
    JsonArray players = new JsonArray();
    simpleServerInfo.getPlayers().forEach(players::add);
    object.add("players", players);
    return object;
  }
}
