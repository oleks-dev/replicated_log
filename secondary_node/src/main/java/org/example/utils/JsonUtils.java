package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T getMassage(InputStream requestBody, Class<T> valueType){
        try {
            return OBJECT_MAPPER.readValue(requestBody, valueType);
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    public static String listToJson(Collection<?> list) {
        return Optional.ofNullable(valueToJson(list)).orElse(List.of().toString());
    }

    public static String valueToJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            //ignore
        }
        return null;
    }
}
