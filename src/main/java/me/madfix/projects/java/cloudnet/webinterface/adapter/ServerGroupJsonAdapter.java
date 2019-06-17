package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityService;
import de.dytanic.cloudnet.lib.server.template.Template;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerGroupJsonAdapter implements JsonSerializer<ServerGroup>,
    JsonDeserializer<ServerGroup> {

  @Override
  public ServerGroup deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    final String name = object.get("name").getAsString();
    final List<String> wrappers = new ArrayList<>();
    object.get("wrapper").getAsJsonArray().forEach(t -> wrappers.add(t.getAsString()));
    final boolean kickedForceFallback = object.get("kickedForceFallback").getAsBoolean();
    final ServerGroupType serverType = ServerGroupType
        .valueOf(object.get("serverType").getAsString());
    final ServerGroupMode groupMode = ServerGroupMode
        .valueOf(object.get("groupMode").getAsString());
    final Template globalTemplate = jsonDeserializationContext
        .deserialize(object.get("globalTemplate"), Template.class);
    final Collection<Template> templates = new ArrayList<>();
    object.get("templates").getAsJsonArray()
        .forEach(t -> templates.add(jsonDeserializationContext.deserialize(t, Template.class)));
    final int memory = object.get("memory").getAsInt();
    final int dynamicMemory = object.get("dynamicMemory").getAsInt();
    final int joinPower = object.get("joinPower").getAsInt();
    final boolean maintenance = object.get("maintenance").getAsBoolean();
    final int minOnlineServers = object.get("minOnlineServers").getAsInt();
    final int maxOnlineServers = object.get("maxOnlineServers").getAsInt();
    final AdvancedServerConfig advancedServerConfig = jsonDeserializationContext
        .deserialize(object.get("advancedServerConfig"), AdvancedServerConfig.class);
    final int percentForNewServerAutomatically = object.get("percentForNewServerAutomatically")
        .getAsInt();
    final PriorityService priorityService = jsonDeserializationContext
        .deserialize(object.get("priorityService"), PriorityService.class);
    ServerGroup group = new ServerGroup(name, wrappers, kickedForceFallback, memory, dynamicMemory,
        joinPower, maintenance, minOnlineServers, priorityService.getGlobal().getOnlineServers(),
        priorityService.getGroup().getOnlineServers(), priorityService.getStopTimeInSeconds(),
        priorityService.getGlobal().getOnlineCount(), priorityService.getGroup().getOnlineCount(),
        percentForNewServerAutomatically, serverType, groupMode, templates, advancedServerConfig);
    group.setMaxOnlineServers(maxOnlineServers);
    group.setGlobalTemplate(globalTemplate);
    group.setTemplates(templates);
    return group;
  }

  @Override
  public JsonElement serialize(ServerGroup serverGroup, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("name", serverGroup.getName());
    JsonArray wrappers = new JsonArray();
    serverGroup.getWrapper().forEach(wrappers::add);
    object.add("wrapper", wrappers);
    object.addProperty("kickedForceFallback", serverGroup.isKickedForceFallback());
    object.addProperty("serverType", serverGroup.getServerType().name());
    object.addProperty("groupMode", serverGroup.getGroupMode().name());
    object
        .add("globalTemplate", jsonSerializationContext.serialize(serverGroup.getGlobalTemplate()));
    JsonArray templates = new JsonArray();
    serverGroup.getTemplates().forEach(t -> templates.add(jsonSerializationContext.serialize(t)));
    object.add("templates", templates);
    object.addProperty("memory", serverGroup.getMemory());
    object.addProperty("dynamicMemory", serverGroup.getDynamicMemory());
    object.addProperty("joinPower", serverGroup.getJoinPower());
    object.addProperty("maintenance", serverGroup.isMaintenance());
    object.addProperty("minOnlineServers", serverGroup.getMinOnlineServers());
    object.addProperty("maxOnlineServers", serverGroup.getMaxOnlineServers());
    object.add("advancedServerConfig",
        jsonSerializationContext.serialize(serverGroup.getAdvancedServerConfig()));
    object.addProperty("percentForNewServerAutomatically",
        serverGroup.getPercentForNewServerAutomatically());
    object.add("priorityService",
        jsonSerializationContext.serialize(serverGroup.getPriorityService()));
    return object;
  }
}
