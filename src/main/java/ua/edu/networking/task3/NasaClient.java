package ua.edu.networking.task3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class NasaClient {
    private static final String NASA_API_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=300&api_key=";
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;

    public NasaClient(String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public void findLargestPhoto() throws Exception {
        HttpRequest request = buildGetRequest(NASA_API_URL + apiKey);

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = objectMapper.readTree(response.body());

        String largestPhotoUrl = findLargestPhoto(rootNode);
        log.info("Largest photo URL: {}", largestPhotoUrl);
        log.info("Size: {} bytes", getPhotoSize(largestPhotoUrl));
    }

    private String findLargestPhoto(JsonNode rootNode) {
        String largestPhotoUrl = "";
        int maxSize = 0;
        for (JsonNode photo : rootNode.get("photos")) {
            String photoUrl = photo.path("img_src").asText();
            int size = getPhotoSize(photoUrl);
            log.debug("Photo size: {} bytes", size);
            if (size > maxSize) {
                maxSize = size;
                largestPhotoUrl = photoUrl;
            }
        }
        return largestPhotoUrl;
    }

    private int getPhotoSize(String photoUrl) {
        try {
            HttpRequest request = buildGetRequest(photoUrl);

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            return response.headers().firstValue("Content-Length").map(Integer::parseInt).orElse(0);
        } catch (IOException ex) {
            log.error("Error checking size for {}: {}", photoUrl, ex.getMessage(), ex);
            return 0;
        } catch (InterruptedException ex) {
            log.error("Thread was interrupted", ex);
            throw new RuntimeException(ex);
        }
    }

    private HttpRequest buildGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }
}
