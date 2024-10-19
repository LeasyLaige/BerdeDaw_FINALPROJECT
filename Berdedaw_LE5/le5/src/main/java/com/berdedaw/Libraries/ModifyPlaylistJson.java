package com.berdedaw.Libraries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;

public class ModifyPlaylistJson {

    private String filePath;
    private ObjectMapper objectMapper;

    public ModifyPlaylistJson(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("exports")
    public JsonNode readJsonFile() throws IOException {
        return objectMapper.readTree(new File(filePath));
    }

    public void addConfiguration(String title, String author, String link, String mediaFilePath, String thumbnailPath) throws IOException {
        JsonNode root = readJsonFile();

        ArrayNode configurations;
        if (root.has("configurations") && root.get("configurations").isArray()) {
            configurations = (ArrayNode) root.get("configurations");
        } else {
            configurations = objectMapper.createArrayNode();
            ((ObjectNode) root).set("configurations", configurations);
        }

        ObjectNode newConfig = objectMapper.createObjectNode();
        newConfig.put("mediaTitle", title);
        newConfig.put("mediaAuthor", author);
        newConfig.put("request", "launch");
        newConfig.put("mediaLink", link);
        newConfig.put("filePath", mediaFilePath);
        newConfig.put("thumbnailPath", thumbnailPath); // Save thumbnail path

        configurations.add(newConfig);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(this.filePath), root);
        System.out.println("playlist.json modified successfully");
    }

    public void modifyPlaylist(String title, String author, String link, String mediaFilePath, String thumbnailPath) {
        try {
            addConfiguration(title, author, link, mediaFilePath, thumbnailPath);
        } catch (IOException e) {
            System.err.println("Error modifying playlist.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getYouTubeUrl(String title, String author) throws IOException {
        JsonNode root = readJsonFile();
        if (root.has("configurations")) {
            ArrayNode configurations = (ArrayNode) root.get("configurations");
            for (JsonNode config : configurations) {
                if (config.get("mediaTitle").asText().equals(title) && config.get("mediaAuthor").asText().equals(author)) {
                    return config.get("mediaLink").asText();
                }
            }
        }
        return null;
    }

    public String getLocalPath(String mediaTitle, String mediaAuthor) {
        try {
            JsonNode root = readJsonFile();
            if (root.has("configurations")) {
                ArrayNode configurations = (ArrayNode) root.get("configurations");
                for (JsonNode config : configurations) {
                    String title = config.get("mediaTitle").asText();
                    String author = config.get("mediaAuthor").asText();
                    String location = config.get("filePath").asText();
    
                    // Debug logging
                    System.out.println("Checking title: " + title + ", author: " + author);
                    if (title.equals(mediaTitle) && author.equals(mediaAuthor)) {
                        System.out.println("Found filePath: " + location); // Log found path
                        return location;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("No local path found for title: " + mediaTitle + " and author: " + mediaAuthor);
        return null;
    }
    
    

    public String getThumbnailPath(String mediaTitle, String mediaAuthor) {
        try {
            JsonNode root = readJsonFile();
            if (root.has("configurations")) {
                ArrayNode configurations = (ArrayNode) root.get("configurations");
                for (JsonNode config : configurations) {
                    String title = config.get("mediaTitle").asText();
                    String author = config.get("mediaAuthor").asText();
    
                    // Debug logging
                    System.out.println("Checking title: " + title + ", author: " + author);
    
                    if (title.equals(mediaTitle) && author.equals(mediaAuthor)) {
                        String thumbnailPath = config.get("thumbnailPath").asText();
                        System.out.println("Found thumbnailPath: " + thumbnailPath); // Log the found thumbnailPath
                        return thumbnailPath; // Return the thumbnail path
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("No thumbnail path found for title: " + mediaTitle + " and author: " + mediaAuthor);
        return null;
    }
    
}
