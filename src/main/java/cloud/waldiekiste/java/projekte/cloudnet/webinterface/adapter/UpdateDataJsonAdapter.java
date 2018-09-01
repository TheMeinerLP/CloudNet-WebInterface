/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.UpdateData;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UpdateDataJsonAdapter implements JsonDeserializer<UpdateData> {
    @Override
    public UpdateData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String version = object.get("version").getAsString();
        final String path = object.get("path").getAsString();
        final VersionType type1 = VersionType.valueOf(object.get("type").getAsString());
        return new UpdateData(version,path,type1);
    }
}
