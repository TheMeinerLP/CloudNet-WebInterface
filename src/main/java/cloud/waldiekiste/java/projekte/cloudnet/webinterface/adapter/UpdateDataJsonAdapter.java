/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.UpdateData;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UpdateDataJsonAdapter implements JsonDeserializer<UpdateData> {

    @Override
    public UpdateData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        /**
         * {
         * UrlPath: "http://update.madfix.me/projects/MaterialDesignWebInterface/files/CLOUDNET",
         * FilePath: "http://update.madfix.me/projects/MaterialDesignWebInterface/files/CLOUDNET/CloudNet-Service-Websocket-Extension-20181228121456012-RELEASE.jar",
         * FileName: "CloudNet-Service-Websocket-Extension-20181228121456012-RELEASE.jar",
         * name: "CloudNet-Service-Websocket-Extension",
         * version: "20181228121456012",
         * branch: "RELEASE",
         * extension: "jar",
         * ENVIRONMENT: "CLOUDNET",
         * SYSTEM: null
         * }
         */
        String filePath = object.get("FilePath").getAsString();
        String fileName = object.get("FileName").getAsString();
        String name = object.get("name").getAsString();
        long version = Long.parseLong(object.get("version").getAsString());
        String extension = object.get("extension").getAsString();
        return new UpdateData(filePath,fileName,name,version,extension);
    }
}
