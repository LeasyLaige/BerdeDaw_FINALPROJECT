package com.berdedaw.Libraries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class YouTubeInfoExtractor {
    private String apiKey;

    public YouTubeInfoExtractor(String apiKey) {
        this.apiKey = apiKey; // Store the API key
    }

    public String[] extractInfo(String youtubeUrl) throws IOException {
        String videoId = extractVideoId(youtubeUrl);
        String apiUrl = "https://www.googleapis.com/youtube/v3/videos?id=" + videoId + "&key=" + apiKey + "&part=snippet,contentDetails";

        // Create a URL object
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Get the response
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to get response from API");
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(url.openStream())) {
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
        }

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response.toString());

        // Extract title and artist from the JSON response
        String title = root.path("items").get(0).path("snippet").path("title").asText();
        String artist = root.path("items").get(0).path("snippet").path("channelTitle").asText();
        String thumbnailUrl = root.path("items").get(0).path("snippet").path("thumbnails").path("high").path("url").asText();
        String duration = root.path("items").get(0).path("contentDetails").path("duration").asText();

        return new String[] { title, artist, thumbnailUrl, duration};
    }

    private String extractVideoId(String youtubeUrl) {
        String videoId = null;
        try {
            // Extract the video ID from the URL (example URL format)
            if (youtubeUrl.contains("v=")) {
                videoId = youtubeUrl.split("v=")[1];
                int ampersandPosition = videoId.indexOf("&");
                if (ampersandPosition != -1) {
                    videoId = videoId.substring(0, ampersandPosition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoId;
    }
}
