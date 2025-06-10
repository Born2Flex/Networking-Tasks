package ua.edu.networking.task2;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Main {
    public static void main(String[] args) {
        RequestSender requestSender = new RequestSender("localhost", 8080, true);
        log.info(Arrays.toString(requestSender.getRequest()));
        log.info(requestSender.postRequest(new Note("Some notes", "Danylo")).toString());
    }
}
