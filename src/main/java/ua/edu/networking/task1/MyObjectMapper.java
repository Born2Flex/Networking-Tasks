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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MyObjectMapper {
    private final Set<Class<?>> wrapperTypes;
    private final List<Pair> processors;

    public MyObjectMapper() {
        wrapperTypes = new HashSet<>();
        wrapperTypes.add(Byte.class);
        wrapperTypes.add(Short.class);
        wrapperTypes.add(Integer.class);
        wrapperTypes.add(Long.class);
        wrapperTypes.add(Float.class);
        wrapperTypes.add(Double.class);
        wrapperTypes.add(Boolean.class);
        wrapperTypes.add(Character.class);

        processors = new ArrayList<>();

        processors.add(new Pair(field -> isSimpleType(field.getType()), (field, object) -> "\"" + field.getName() + "\":\"" + accessFieldValue(field, object) + "\""));

        processors.add(new Pair(field -> Collection.class.isAssignableFrom(field.getType()), (field, object) -> {
            Collection<?> collection = (Collection<?>) accessFieldValue(field, object);
            Class<?> collectionType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            return "\"" + field.getName() + "\":" + serializeCollection(collectionType, collection);
        }));

        processors.add(new Pair(field -> Map.class.isAssignableFrom(field.getType()), (field, object) -> {
            Map<?, ?> map = (Map<?, ?>) accessFieldValue(field, object);
            return "\"" + field.getName() + "\":" + serializeMap(map);
        }));

        processors.add(new Pair(field -> field.getType().isArray(), (field, object) -> {
            Class<?> arrayType = accessFieldValue(field, object).getClass().getComponentType();
            return "\"" + field.getName() + "\":" + serializeCollection(arrayType, accessFieldValue(field, object));
        }));

        processors.add(new Pair(field -> true, ((field, object) -> "\"" + field.getName() + "\":" + serialize(accessFieldValue(field, object)))));
    }

    public String serialize(Object obj) {
        return Stream.of(obj.getClass())
                .flatMap(MyObjectMapper::getAllDeclaredFields)
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
                .map(field -> serializeFieldAccordingToType(field, obj))
                .collect(Collectors.joining(",", "{", "}"));

    }

    private static Stream<Field> getAllDeclaredFields(Class<?> clazz) {
        if (clazz.getSuperclass() == Object.class) {
            return Stream.of(clazz.getDeclaredFields());
        }
        return Stream.concat(getAllDeclaredFields(clazz.getSuperclass()), Stream.of(clazz.getDeclaredFields()));
    }

    private String serializeFieldAccordingToType(Field field, Object object) {
        var processor = processors.stream()
                .filter(pair -> pair.getPredicate().test(field))
                .map(Pair::getFunction)
                .findFirst();
        if (processor.isEmpty()) {
            throw new UnsupportedOperationException("Unsupported field type: " + field.getType());
        }
        return processor.get().apply(field, object);
    }

    private Object accessFieldValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException ex) {
            throw new ObjectSerializationError(ex);
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
        return wrapperTypes.contains(type) || type.isPrimitive();
    }
}
