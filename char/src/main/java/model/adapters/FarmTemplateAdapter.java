package model.adapters;

import com.google.gson.*;
import model.farm.DefaultFarmTemplate;
import model.farm.FarmTemplate;
import model.farm.LakeFarmTemplate;

import java.lang.reflect.Type;

public class FarmTemplateAdapter implements JsonDeserializer<FarmTemplate>, JsonSerializer<FarmTemplate> {

    @Override
    public JsonElement serialize(FarmTemplate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = context.serialize(src).getAsJsonObject();
        obj.addProperty("type", src.getClass().getSimpleName());
        return obj;
    }

    @Override
    public FarmTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();

        switch (type) {
            case "LakeFarmTemplate":
                return context.deserialize(obj, LakeFarmTemplate.class);
            case "DefaultFarmTemplate":
                return context.deserialize(obj, DefaultFarmTemplate.class);
            default:
                throw new JsonParseException("Unknown farm template type: " + type);
        }
    }
}
