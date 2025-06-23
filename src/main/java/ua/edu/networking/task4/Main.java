package ua.edu.networking.task4;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.write(buildHttpGetRequest());
            writer.flush();
            reader.lines().forEach(log::info);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private static String buildHttpGetRequest() {
        return """
                GET /hello HTTP/1.1
                Host: localhost:8080
                Connection: close
                
                """;
    }
}