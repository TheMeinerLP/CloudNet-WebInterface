package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TemplateJsonAdapter implements JsonSerializer<Template>, JsonDeserializer<Template> {

  @Override
  public Template deserialize(JsonElement templateelement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject templatejson = templateelement.getAsJsonObject();
    String name = templatejson.get("name").getAsString();
    TemplateResource backend = TemplateResource.valueOf(templatejson.get("backend").getAsString());
    String url = templatejson.get("url").getAsString();
    ArrayList<String> processPreParameters = new ArrayList<>();
    templatejson.get("processPreParameters").getAsJsonArray()
        .forEach(t -> processPreParameters.add(t.getAsString()));
    Collection<ServerInstallablePlugin> serverInstallablePlugins = new ArrayList<>();
    if (templatejson.has("installablePlugins")) {
      templatejson.get("installablePlugins").getAsJsonArray().forEach(t -> serverInstallablePlugins
          .add(jsonDeserializationContext.deserialize(t, ServerInstallablePlugin.class)));
    }
    return new Template(name, backend, url,
        processPreParameters.toArray(new String[0]),
        serverInstallablePlugins);
  }

  @Override
  public JsonElement serialize(Template templateclass, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject templatejson = new JsonObject();
    templatejson.addProperty("name", templateclass.getName());
    templatejson.addProperty("backend", templateclass.getBackend().toString());
    if (templateclass.getUrl() == null) {
      templatejson.addProperty("url", "NULL");
    } else {
      templatejson.addProperty("url", templateclass.getUrl());
    }
    JsonArray processPreParameters = new JsonArray();
    Arrays.asList(templateclass.getProcessPreParameters()).forEach(processPreParameters::add);
    templatejson.add("processPreParameters", processPreParameters);
    JsonArray plugins = new JsonArray();
    templateclass.getInstallablePlugins()
        .forEach(t -> plugins.add(jsonSerializationContext.serialize(t)));
    templatejson.add("installablePlugins", plugins);
    return templatejson;
  }
}
