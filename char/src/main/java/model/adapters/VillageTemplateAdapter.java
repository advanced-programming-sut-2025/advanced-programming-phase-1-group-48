package model.adapters;

import com.google.gson.*;
import model.village.ClassicVillageTemplate;
import model.village.VillageTemplate;

import java.lang.reflect.Type;

public class VillageTemplateAdapter implements JsonSerializer<VillageTemplate>, JsonDeserializer<VillageTemplate> {

    @Override
    public JsonElement serialize(VillageTemplate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = context.serialize(src).getAsJsonObject();
        obj.addProperty("villageType", src.getClass().getSimpleName());
        return obj;
    }

    @Override
    public VillageTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("villageType").getAsString();

        switch (type) {
            case "ClassicVillageTemplate":
                return context.deserialize(json, ClassicVillageTemplate.class);
            default:
                throw new JsonParseException("Unknown villageType: " + type);
        }
    }
}
