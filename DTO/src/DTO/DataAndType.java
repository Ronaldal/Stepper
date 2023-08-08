package DTO;

import com.google.gson.*;

import java.lang.reflect.Type;

public class DataAndType {
    private final Object data;
    private final Class<?> classType;

    public DataAndType(Object data, Class<?> classType) {
        this.data = data;
        this.classType = classType;
    }

    public Object getData() {
        return data;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public static class DataAndTypeAdapter implements JsonSerializer<DataAndType>, JsonDeserializer<DataAndType> {
        @Override
        public JsonElement serialize(DataAndType src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("data", context.serialize(src.getData()));
            jsonObject.addProperty("classType", src.getClassType().getName());
            return jsonObject;
        }

        @Override
        public DataAndType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement dataElement = jsonObject.get("data");
            JsonElement classTypeElement = jsonObject.get("classType");

            Object data = context.deserialize(dataElement, Object.class);
            Class<?> classType;
            try {
                classType = Class.forName(classTypeElement.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Failed to deserialize classType", e);
            }

            return new DataAndType(data, classType);
        }
    }
}
