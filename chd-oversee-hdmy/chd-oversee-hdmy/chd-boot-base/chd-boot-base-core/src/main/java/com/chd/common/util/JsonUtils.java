package com.chd.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chd.common.exception.ArgumentException;



public class JsonUtils {
    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    }
    public static ObjectMapper getDefaultObjectMapper() {
        return objectMapper;
    }

    private JsonUtils() {}

    @SneakyThrows
    public static String toJsonStr(Object object) {
        try {
			return getDefaultObjectMapper().writeValueAsString(object);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw new ArgumentException( e);
		}
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return getDefaultObjectMapper().readValue(jsonString, clazz);
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw new ArgumentException( e);
		}
    }

    public static <T> T fromJson(String jsonString, TypeReference<T> tTypeReference) {
        try {
            ObjectMapper objectMapper =getDefaultObjectMapper();
            return objectMapper.readValue(jsonString, tTypeReference);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ArgumentException( e);
        }
    }

}

     

 
