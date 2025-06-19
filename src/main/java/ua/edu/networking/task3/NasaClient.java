package ua.edu.networking.task3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class NasaClient {
    private static final String NASA_API_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=300&api_key=";
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final String apiKey;

    public NasaClient(String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }

    public void findLargestPhoto() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(NASA_API_URL + apiKey, String.class);
        JsonNode rootNode = objectMapper.readTree(response.getBody());

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
        ResponseEntity<String> response = restTemplate.getForEntity(photoUrl, String.class);
        if (response.getStatusCode().is3xxRedirection()) {
            return getPhotoSize(response.getHeaders().getLocation().toString());
        }
        return (int) response.getHeaders().getContentLength();
    }
}
