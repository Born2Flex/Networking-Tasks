package ua.edu.networking.task1.samples;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class Person {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Address address;
    private List<String> interests;
    private List<Address> visitedAddresses;
    private int favoriteNumber;
}
