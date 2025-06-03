package ua.edu.networking.task1;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        StringBuilder sb = new StringBuilder("{");
        try {
            for (var field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                log.info("{}: {}", field.getName(), field.get(obj));
                if (isSimpleType(field.getType())) {
                    sb.append("\"")
                            .append(field.getName())
                            .append("\":\"")
                            .append(field.get(obj))
                            .append("\"")
                            .append(",");
                } else if (field.get(obj) instanceof Collection<?> collection) {
                    sb.append("\"")
                            .append(field.getName())
                            .append("\":")
                            .append(serializeCollection(field, collection))
                            .append(",");
                } else {
                    sb.append("\"")
                            .append(field.getName())
                            .append("\":")
                            .append(serialize(field.get(obj)))
                            .append(",");
                }
            }
        } catch (IllegalAccessException e) {
            log.error("An error occurred during accessing field of object: {}", e.getMessage(), e);
        }
        sb.append("}");
        return sb.toString();
    }

    private String serializeCollection(Field collectionField, Collection<?> collection) {
        StringBuilder sb = new StringBuilder("[");
        for (var obj : collection) {
            if (isSimpleType((Class<?>) ((ParameterizedType) collectionField.getGenericType()).getActualTypeArguments()[0])) {
                sb.append(obj);
            } else {
                sb.append(serialize(obj));
            }
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
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
