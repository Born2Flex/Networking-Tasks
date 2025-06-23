package ua.edu.networking;

import lombok.extern.slf4j.Slf4j;
import ua.edu.networking.task1.MyObjectMapper;
import ua.edu.networking.task1.samples.Address;
import ua.edu.networking.task1.samples.Person;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        MyObjectMapper mapper = new MyObjectMapper();

        Address address = new Address("Ukraine", "Kyiv", "Khreschatyk");
        Person person = new Person("John", "Doe", LocalDate.of(1990, 1, 1), address, List.of("soccer", "cycling"), List.of(address, address), 6);

        log.info(mapper.serialize(person));
    }
}
