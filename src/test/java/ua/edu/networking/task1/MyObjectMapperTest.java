package ua.edu.networking.task1;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class MyObjectMapperTest {

    private MyObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MyObjectMapper();
    }

    @Test
    void shouldReturnValidJsonForSimpleObject() {
        SimpleUser user = new SimpleUser("Alice", 30);
        String expected = """
                {"name":"Alice","age":"30"}""";

        String result = mapper.serialize(user);

        assertEquals(expected, result);
    }

    @Test
    void shouldHandleNullFields() {
        SimpleUser user = new SimpleUser(null, 25);
        String expected = """
                {"name":"null","age":"25"}""";

        String result = mapper.serialize(user);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeRecursivelyNestedObjects() {
        Address address = new Address("New York", "10001");
        UserWithAddress user = new UserWithAddress("Bob", address);
        String expected = """
                {"name":"Bob","address":{"city":"New York","zip":"10001"}}""";

        String result = mapper.serialize(user);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeObjectWithNestedCollection() {
        UserWithTags user = new UserWithTags("John", List.of("admin", "user"));
        String expected = """
                {"name":"John","tags":["admin","user"]}""";

        String result = mapper.serialize(user);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeObjectWithNestedCollectionOfObjects() {
        List<Address> addresses = List.of(new Address("LA", "90001"), new Address("SF", "94101"));
        UserWithAddresses user = new UserWithAddresses("John", addresses);
        String expected = """
                {"name":"John","addresses":[{"city":"LA","zip":"90001"},{"city":"SF","zip":"94101"}]}""";

        String result = mapper.serialize(user);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeObjectWithDateAndDecimal() {
        ExtendedUser user = new ExtendedUser("Paul", new BigDecimal("12345.67"), LocalDate.of(1990, 1, 1));
        String expected = """
                {"name":"Paul","salary":"12345.67","birthday":"1990-01-01"}""";

        String result = mapper.serialize(user);

        assertEquals(expected, result);
    }


    @AllArgsConstructor
    static class SimpleUser {
        String name;
        int age;
    }

    @AllArgsConstructor
    static class Address {
        String city;
        String zip;
    }

    @AllArgsConstructor
    static class UserWithAddress {
        String name;
        Address address;
    }

    @AllArgsConstructor
    static class UserWithTags {
        String name;
        List<String> tags;
    }

    @AllArgsConstructor
    static class UserWithAddresses {
        String name;
        List<Address> addresses;
    }

    @AllArgsConstructor
    static class ExtendedUser {
        String name;
        BigDecimal salary;
        LocalDate birthday;
    }
}