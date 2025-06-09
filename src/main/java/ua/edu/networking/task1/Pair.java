package ua.edu.networking.task1;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Data
@AllArgsConstructor
public class Pair {
    private Predicate<Field> predicate;
    private BiFunction<Field, Object, String> function;
}
