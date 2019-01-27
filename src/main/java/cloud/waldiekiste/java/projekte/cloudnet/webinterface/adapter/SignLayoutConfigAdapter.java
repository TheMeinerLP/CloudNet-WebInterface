package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.serverselectors.sign.SearchingAnimation;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignGroupLayouts;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SignLayoutConfigAdapter implements JsonSerializer<SignLayoutConfig>, JsonDeserializer<SignLayoutConfig> {
    @Override
    public SignLayoutConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        boolean fullServerHide = object.get("fullServerHide").getAsBoolean();
        boolean knockbackOnSmallDistance = object.get("knockbackOnSmallDistance").getAsBoolean();
        double distance = object.get("distance").getAsDouble();
        double strength = object.get("strength").getAsDouble();
        ArrayList<SignGroupLayouts> groupLayouts = new ArrayList<>();
        object.get("groupLayouts").getAsJsonArray().forEach(t->groupLayouts.add(jsonDeserializationContext.deserialize(t,SignGroupLayouts.class)));
        SearchingAnimation searchingAnimation = jsonDeserializationContext.deserialize(object.get("searchingAnimation"), SearchingAnimation.class);
        return new SignLayoutConfig(fullServerHide,knockbackOnSmallDistance,distance,strength,groupLayouts,searchingAnimation);
    }

    @Override
    public JsonElement serialize(SignLayoutConfig signLayoutConfig, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("fullServerHide",signLayoutConfig.isFullServerHide());
        object.addProperty("knockbackOnSmallDistance",signLayoutConfig.isKnockbackOnSmallDistance());
        object.addProperty("distance",signLayoutConfig.getDistance());
        object.addProperty("strength", signLayoutConfig.getStrength());
        JsonArray groupLayouts = new JsonArray();
        signLayoutConfig.getGroupLayouts().forEach(t->groupLayouts.add(jsonSerializationContext.serialize(t)));
        object.add("groupLayouts",groupLayouts);
        object.add("searchingAnimation",jsonSerializationContext.serialize(signLayoutConfig.getSearchingAnimation()));
        return object;
    }
}
