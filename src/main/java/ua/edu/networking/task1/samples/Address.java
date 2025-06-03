package ua.edu.networking.task1.samples;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Address {
    private String country;
    private String city;
    private String street;
}
