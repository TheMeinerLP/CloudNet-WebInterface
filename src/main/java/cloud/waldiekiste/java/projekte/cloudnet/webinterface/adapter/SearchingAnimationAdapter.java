package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.serverselectors.sign.SearchingAnimation;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayout;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchingAnimationAdapter implements JsonSerializer<SearchingAnimation>, JsonDeserializer<SearchingAnimation> {
    @Override
    public SearchingAnimation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int animations = object.get("animations").getAsInt();
        int animationsPerSecond = object.get("animationsPerSecond").getAsInt();
        ArrayList<SignLayout> searchingLayouts = new ArrayList<>();
        object.get("searchingLayouts").getAsJsonArray().forEach(t->searchingLayouts.add(jsonDeserializationContext.deserialize(t,SignLayout.class)));
        return new SearchingAnimation(animations,animationsPerSecond,searchingLayouts);
    }

    @Override
    public JsonElement serialize(SearchingAnimation searchingAnimation, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("animations",searchingAnimation.getAnimations());
        object.addProperty("animationsPerSecond", searchingAnimation.getAnimationsPerSecond());
        JsonArray array = new JsonArray();
        searchingAnimation.getSearchingLayouts().forEach(t->array.add(jsonSerializationContext.serialize(t)));
        object.add("searchingLayouts",array);
        return object;
    }
}
