package me.madfix.projects.java.cloudnet.webinterface.adapter;

import me.madfix.projects.java.cloudnet.webinterface.utils.UpdateData;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class UpdateDataJsonAdapter implements JsonDeserializer<UpdateData> {

  @Override
  public UpdateData deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    String filePath = object.get("FilePath").getAsString();
    String fileName = object.get("FileName").getAsString();
    String name = object.get("name").getAsString();
    long version = Long.parseLong(object.get("version").getAsString());
    String extension = object.get("extension").getAsString();
    return new UpdateData(filePath, fileName, name, version, extension);
  }
}
