package ua.edu.networking.task1;

import lombok.extern.slf4j.Slf4j;
import ua.edu.networking.task1.exceptions.ObjectSerializationError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MyObjectMapper {
    private static final Set<Class<?>> WRAPPER_TYPES;

    static {
        WRAPPER_TYPES = new HashSet<>();
        WRAPPER_TYPES.add(Byte.class);
        WRAPPER_TYPES.add(Short.class);
        WRAPPER_TYPES.add(Integer.class);
        WRAPPER_TYPES.add(Long.class);
        WRAPPER_TYPES.add(Float.class);
        WRAPPER_TYPES.add(Double.class);
        WRAPPER_TYPES.add(Boolean.class);
        WRAPPER_TYPES.add(Character.class);
    }

    public String serialize(Object obj) {
        return Stream.of(obj.getClass())
                .flatMap(MyObjectMapper::getAllDeclaredFields)
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
                .map(field -> serializeField(obj, field))
                .collect(Collectors.joining(",", "{", "}"));

    }

    private static Stream<Field> getAllDeclaredFields(Class<?> clazz) {
        if (clazz.getSuperclass() == Object.class) {
            return Stream.of(clazz.getDeclaredFields());
        }
        return Stream.concat(getAllDeclaredFields(clazz.getSuperclass()), Stream.of(clazz.getDeclaredFields()));
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
            Class<?> collectionType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            return "\"" + field.getName() + "\":" + serializeCollection(collectionType, collection);
        } else if (field.get(obj) instanceof Map<?, ?> map) {
            return "\"" + field.getName() + "\":" + serializeMap(map);
        } else if (field.get(obj).getClass().isArray()) {
            Class<?> arrayType = field.get(obj).getClass().getComponentType();
            return "\"" + field.getName() + "\":" + serializeCollection(arrayType, field.get(obj));
        } else {
            return "\"" + field.getName() + "\":" + serialize(field.get(obj));
        }
    }

    private String serializeMap(Map<?, ?> map) {
        return map.keySet().stream()
                .map(key -> serializePair(key, map.get(key)))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private String serializePair(Object key, Object value) {
        String serializedValue = isSimpleType(value.getClass()) ? "\"" + value + "\"" : serialize(value);
        return "\"" + key + "\":" + serializedValue;
    }

    private String serializeCollection(Class<?> collectionType, Object object) {
        if (!object.getClass().isArray()) {
            throw new IllegalArgumentException("Object is not an array");
        }
        return serializeCollection(collectionType, Arrays.asList((Object[]) object));
    }

    private String serializeCollection(Class<?> collectionType, Collection<?> collection) {
        return collection.stream()
                .map(elem -> {
                    if (isSimpleType(collectionType)) {
                        return "\"" + elem.toString() + "\"";
                    } else {
                        return serialize(elem);
                    }
                })
                .collect(Collectors.joining(",", "[", "]"));
    }

    private boolean isSimpleType(Class<?> type) {
        return isPrimitiveOrWrapper(type)
                || type.isEnum()
                || type == String.class
                || type == Date.class
                || type == LocalDate.class
                || type == LocalDateTime.class
                || type == BigDecimal.class
                || type == BigInteger.class;
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return WRAPPER_TYPES.contains(type) || type.isPrimitive();
    }
}
