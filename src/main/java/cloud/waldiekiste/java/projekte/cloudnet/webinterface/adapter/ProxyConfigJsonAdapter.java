package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ProxyConfigJsonAdapter implements JsonSerializer<ProxyConfig>,JsonDeserializer<ProxyConfig> {
    @Override
    public ProxyConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final boolean enabled = object.get("enabled").getAsBoolean();
        final boolean maintenance = object.get("maintenance").getAsBoolean();
        final List<Motd> motdsLayouts = new ArrayList<>();
        object.get("motdsLayouts").getAsJsonArray().forEach(t->motdsLayouts.add(jsonDeserializationContext.deserialize(t,Motd.class)));
        final Motd maintenanceMotdLayout = jsonDeserializationContext.deserialize(object.get("maintenanceMotdLayout"),Motd.class);
        final String maintenaceProtocol = object.get("maintenaceProtocol").getAsString();
        final int maxPlayers = object.get("maxPlayers").getAsInt();
        final boolean fastConnect = object.get("fastConnect").getAsBoolean();
        final boolean customPayloadFixer = object.get("customPayloadFixer").getAsBoolean();
        final AutoSlot autoSlot = jsonDeserializationContext.deserialize(object.get("autoSlot"),AutoSlot.class);
        final TabList tabList = jsonDeserializationContext.deserialize(object.get("tabList"),TabList.class);
        final String[] playerInfos = object.get("playerInfo").toString().replace("[", "").replace("]", "").split(", ");
        final Collection<String> whitelist = new ArrayList<>();
        object.get("whitelist").getAsJsonArray().forEach(t->whitelist.add(t.getAsString()));
        final DynamicFallback dynamicFallback = jsonDeserializationContext.deserialize(object.get("dynamicFallback"),DynamicFallback.class);

        return new ProxyConfig(enabled,maintenance,motdsLayouts,maintenanceMotdLayout,maintenaceProtocol,maxPlayers,fastConnect,customPayloadFixer,autoSlot,tabList,playerInfos,whitelist,dynamicFallback);
    }

    @Override
    public JsonElement serialize(ProxyConfig proxyConfig, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("enabled", proxyConfig.isEnabled());
        object.addProperty("maintenance", proxyConfig.isMaintenance());
        JsonArray motdsLayouts = new JsonArray();
        proxyConfig.getMotdsLayouts().forEach(t->motdsLayouts.add(jsonSerializationContext.serialize(t)));
        object.add("motdsLayouts", motdsLayouts);
        object.add("maintenanceMotdLayout",jsonSerializationContext.serialize(proxyConfig.getMaintenanceMotdLayout()));
        object.addProperty("maintenaceProtocol",proxyConfig.getMaintenaceProtocol());
        object.addProperty("maxPlayers",proxyConfig.getMaxPlayers());
        object.addProperty("fastConnect",proxyConfig.isFastConnect());
        object.addProperty("customPayloadFixer",proxyConfig.getCustomPayloadFixer());
        object.add("autoSlot",jsonSerializationContext.serialize(proxyConfig.getAutoSlot()));
        object.add("tabList",jsonSerializationContext.serialize(proxyConfig.getTabList()));
        object.addProperty("playerInfo", Arrays.toString(proxyConfig.getPlayerInfo()));
        JsonArray whitelist = new JsonArray();
        proxyConfig.getWhitelist().forEach(whitelist::add);
        object.add("whitelist",whitelist);
        object.add("dynamicFallback",jsonSerializationContext.serialize(proxyConfig.getDynamicFallback()));
        return object;
    }
}
