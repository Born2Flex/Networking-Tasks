package ua.edu.networking.task2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RequestSender {
    private final String host;
    private final int port;
    private final ObjectMapper mapper = new ObjectMapper();

    public Object[] getRequest() {
        String request = buildHttpGetRequest();
        return processHttpRequest(request, Object[].class);
    }

    private String buildHttpGetRequest() {
        return "GET / HTTP/1.1\n" +
               "Host: " + host + ":" + port + "\n" +
               "Connection: close\n\n";
    }

    public Object postRequest(Object object) {
        String requestBody = serializeToJson(object);
        String request = buildHttpPostRequest(requestBody);
        return processHttpRequest(request, Object.class);
    }

    private String serializeToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            log.error("Error occurred while serializing object to JSON", exception);
            throw new RuntimeException(exception);
        }
    }

    private String buildHttpPostRequest(String requestBody) {
        return "POST / HTTP/1.1\n" +
               "Host: " + host + ":" + port + "\n" +
               "Content-Type: application/json\n" +
               "Content-Length: " + requestBody.length() + "\n" +
               "Connection: close\n\n" +
               requestBody;
    }

    private <T> T processHttpRequest(String request, Class<T> clazz) {
        List<String> response = sendHttpRequestWithSocket(request);
        logResponse(response);
        return parseResponseBody(response, clazz);
    }

    private List<String> sendHttpRequestWithSocket(String request) {
        try (Socket socket = new Socket(host, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.write(request);
            writer.flush();
            return reader.lines().toList();
        } catch (IOException exception) {
            log.error("HTTP request failed", exception);
            throw new RuntimeException(exception);
        }
    }

    private void logResponse(List<String> response) {
        log.info("\n{}", String.join("\n", response));
    }

    private <T> T parseResponseBody(List<String> response, Class<T> type) {
        try {
            if (response.size() < 8) {
                throw new IllegalStateException("Unexpected response structure");
            }
            return mapper.readValue(response.get(7), type);
        } catch (IOException e) {
            log.error("Failed to parse response", e);
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
