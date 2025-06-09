package ua.edu.networking.task1.samples.enumeration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Order {
    private int id;
    private String description;
    private LocalDate date;
    private Status status;
}
