package pt.uc.dei.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.json.*;
import java.io.StringReader;


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