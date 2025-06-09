package ua.edu.networking.task1.samples.map;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Journal {
    private String description;
    private Map<String, String> books;
    private Map<String, Object> visitors;
}
