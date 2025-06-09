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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
        return WRAPPER_TYPES.contains(type) || type.isPrimitive();
    }
}
