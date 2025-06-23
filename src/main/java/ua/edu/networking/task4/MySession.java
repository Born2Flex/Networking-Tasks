package ua.edu.networking.task4;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MySession {
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        attributes.put(key, value);
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(attributes.get(key));
    }
}
