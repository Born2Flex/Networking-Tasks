package ua.edu.networking.task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RequestSender {
    private final String host;
    private final int port;

    public Note[] getRequest() {
        ObjectMapper mapper = new ObjectMapper();
        try (Socket socket = new Socket(host, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.write("GET / HTTP/1.1\n");
            writer.write("Host: " + host + ":" + port + "\n");
            writer.write("Connection: close\n\n");
            writer.flush();

            List<String> response = reader.lines().toList();
            String responseCode = response.getFirst().split(" ")[1];
            Note[] responseBody = mapper.readValue(response.get(7), Note[].class);
            log.info(responseCode);
            log.info(Arrays.toString(responseBody));
            return responseBody;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Note postRequest(Note note) {
        ObjectMapper mapper = new ObjectMapper();
        try (Socket socket = new Socket(host, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String body = mapper.writeValueAsString(note);

            writer.write("POST / HTTP/1.1\n");
            writer.write("Host: " + host + ":" + port + "\n");
            writer.write("Content-Type: application/json\n");
            writer.write("Content-Length: " + body.length() + "\n");
            writer.write("Connection: close\n\n");
            writer.write(body);
            writer.flush();

            List<String> response = reader.lines().toList();
            String responseCode = response.getFirst().split(" ")[1];
            Note responseBody = mapper.readValue(response.get(7), Note.class);
            log.info(responseCode);
            log.info(responseBody.toString());
            return responseBody;
        } catch (UnknownHostException e) {
            log.error("Unknown host", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Error occurred", e);
            throw new RuntimeException(e);
        }
    }
}
