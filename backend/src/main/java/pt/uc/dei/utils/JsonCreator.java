package pt.uc.dei.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.json.*;
import java.io.StringReader;


/**
 * Utility class for creating JSON objects using Jackson and Jakarta JSON-P.
 * <p>
 * Provides methods to serialize Java objects to JSON and build custom JSON structures.
 */
public class JsonCreator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }

    private static JsonObject parseJsonString(String jsonString) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))) {
            return jsonReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a JSON object with a specified type and named object field.
     * <p>
     * If the object is an Integer or String, it is added directly. Otherwise, it is serialized with Jackson and parsed.
     *
     * @param type       the type string to include in the JSON
     * @param objectName the name of the object field
     * @param object     the object to serialize and include
     * @return the constructed JsonObject, or null if serialization fails
     */
    public static JsonObject createJson(String type, String objectName, Object object) {
        try {
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder().add("type", type);
            if (object instanceof Integer) {
                jsonBuilder.add(objectName, (Integer) object);
            } else if (object instanceof String) {
                jsonBuilder.add(objectName, (String) object);
            } else {
                String jsonString = objectMapper.writeValueAsString(object);
                JsonObject jsonObject = parseJsonString(jsonString);
                jsonBuilder.add(objectName, jsonObject);
            }
            return jsonBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}