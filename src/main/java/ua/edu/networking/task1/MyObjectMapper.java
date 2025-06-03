package ua.edu.networking.task1;

import lombok.extern.slf4j.Slf4j;
import ua.edu.networking.task1.exceptions.ObjectSerializationError;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// TODO Handling of an ENUM? Arrays?

@Slf4j
public class MyObjectMapper {
    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;

    static {
        WRAPPER_TYPE_MAP = new HashMap<>();
        WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        WRAPPER_TYPE_MAP.put(Character.class, char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class, double.class);
        WRAPPER_TYPE_MAP.put(Float.class, float.class);
        WRAPPER_TYPE_MAP.put(Long.class, long.class);
        WRAPPER_TYPE_MAP.put(Short.class, short.class);
    }

    public String serialize(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .map(field -> serializeField(obj, field))
                .collect(Collectors.joining(",", "{", "}"));

    }

    private String serializeField(Object obj, Field field) {
        try {
            return serializeFieldAccordingToType(obj, field);
        } catch (IllegalAccessException e) {
            log.error("An error occurred during accessing field: {}", e.getMessage(), e);
            throw new ObjectSerializationError(e);
        }
    }

    private String serializeFieldAccordingToType(Object obj, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        log.debug("{}: {}", field.getName(), field.get(obj));
        if (isSimpleType(field.getType())) {
            return "\"" + field.getName() + "\":\"" + field.get(obj) + "\"";
        } else if (field.get(obj) instanceof Collection<?> collection) {
            return "\"" + field.getName() + "\":[" + serializeCollectionElements(field, collection) + "]";
        } else {
            return "\"" + field.getName() + "\":" + serialize(field.get(obj));
        }
    }

    private String serializeCollectionElements(Field collectionField, Collection<?> collection) {
        return collection.stream()
                .map(elem -> {
                    if (isSimpleType((Class<?>) ((ParameterizedType) collectionField.getGenericType()).getActualTypeArguments()[0])) {
                        return "\"" + elem.toString() + "\"";
                    } else {
                        return serialize(elem);
                    }
                })
                .collect(Collectors.joining(","));
    }


    private boolean isSimpleType(Class<?> type) {
        return isPrimitiveOrWrapper(type)
                || type == String.class
                || type == Date.class
                || type == LocalDate.class
                || type == LocalDateTime.class
                || type == BigDecimal.class
                || type == BigInteger.class;
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return WRAPPER_TYPE_MAP.containsKey(type) || type.isPrimitive();
    }
}
