package quickml.supervised;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import quickml.data.instances.InstanceWithAttributesMap;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.Charset.defaultCharset;

public class JsonInstanceLoader {

    private static List<InstanceWithAttributesMap> loadInstanceData(final String resourceName) throws IOException {
        Gson gson = createGson();
        JsonReader reader = new JsonReader(asCharSource(getResource(resourceName), defaultCharset()).openStream());
        List<InstanceWithAttributesMap> instances = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            instances.add(gson.<InstanceWithAttributesMap>fromJson(reader, InstanceWithAttributesMap.class));
        }
        reader.endArray();
        reader.close();
        return instances;
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Serializable.class, new SerializableDeserializer());
        return gsonBuilder.create();
    }


    private static class SerializableDeserializer implements JsonDeserializer<Serializable> {
        public Serializable deserialize(JsonElement json, Type Serializable, JsonDeserializationContext context) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean())
                return primitive.getAsBoolean();
            else if (primitive.isNumber()) {
                return getNumber(primitive);
            } else if (primitive.isString())
                return primitive.getAsString();

            throw new RuntimeException("Unexpected type when parsing Instance json " + json.toString());
        }

        private Serializable getNumber(JsonPrimitive primitive) {
            double value = primitive.getAsDouble();
            if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
                return (int) value;
            } else {
                return value;
            }
        }
    }

}
