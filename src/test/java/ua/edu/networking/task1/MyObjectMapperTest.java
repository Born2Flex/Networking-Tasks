package ua.edu.networking.task1;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.edu.networking.task1.samples.enumeration.Order;
import ua.edu.networking.task1.samples.enumeration.Status;
import ua.edu.networking.task1.samples.inheritance.Child;
import ua.edu.networking.task1.samples.map.Journal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    void shouldSerializeChildObjectIncludingAllParentFields() {
        Child child = new Child(1, "John", "Snow", "Jogging");
        String expected = """
                {"id":"1","firstName":"John","lastName":"Snow","hobby":"Jogging"}""";

        String result = mapper.serialize(child);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeClassWithEnumFields() {
        Order order = new Order(1, "New order", LocalDate.of(2025, 6, 9), Status.PENDING);
        String expected = """
                {"id":"1","description":"New order","date":"2025-06-09","status":"PENDING"}""";

        String result = mapper.serialize(order);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeClassWithMapFields() {
//        Map<String, String> bookDescriptions = Map.of("Effective Java", "Book about effective usage of Java programming language", //TODO Different ordering from time to time
//                "Clean Code", "The basic book for new programmers");
        Map<String, String> bookDescriptions = new HashMap<>();
        bookDescriptions.put("Effective Java", "Book about effective usage of Java programming language");
        bookDescriptions.put("Clean Code", "The basic book for new programmers");
        SimpleUser user = new SimpleUser("John Doe", 25);
        Map<String, Object> visitors = Map.of("John Doe", user);
        Journal journal = new Journal("Library visitors journal", bookDescriptions, visitors);
        String expected = """
                {"description":"Library visitors journal","books":{"Clean Code":"The basic book for new programmers","Effective Java":"Book about effective usage of Java programming language"},"visitors":{"John Doe":{"name":"John Doe","age":"25"}}}""";

        String result = mapper.serialize(journal);

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeClassWithArrayField() {
        String[] programmingLanguages = {"Java", "Kotlin", "Groovy", "Skala"};
        ArrayWrapper wrapper = new ArrayWrapper(programmingLanguages);
        String expected = """
                {"array":["Java","Kotlin","Groovy","Skala"]}""";

        String result = mapper.serialize(wrapper);

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

    @AllArgsConstructor
    static class ArrayWrapper {
        Object[] array;
    }
}