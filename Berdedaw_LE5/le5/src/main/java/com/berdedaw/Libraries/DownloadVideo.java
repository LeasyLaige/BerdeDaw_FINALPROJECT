package com.berdedaw.Libraries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

@SuppressWarnings("unused")
public class DownloadVideo {
    static String outputDir = "C:\\Users\\Janxen\\Documents\\GitHub\\BerdeDaw_FINALPROJECT\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\Downloads"; // Ensure this directory exists
    static String apiKey = "AIzaSyBOdjUVG0KzSPPmN4pvdy9C_k3cexUEqMo"; // Replace with your API key

    public static void main(String[] args) throws InterruptedException {
        // Read YouTube URL from console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter YouTube video URL: ");
        try {
            String videoUrl = reader.readLine();
            String[] paths = downloadVideoAndThumbnail(videoUrl);
            System.out.println("Video downloaded to: " + paths[0]);
            System.out.println("Thumbnail downloaded to: " + paths[1]);
            
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }

    public static String[] downloadVideoAndThumbnail(String videoUrl) throws IOException, InterruptedException {
        // Create the command to download the video
        String[] command = {
            "yt-dlp",
            "--no-playlist",
            "-f", "bestaudio[ext=m4a]", // Download the best audio quality in M4A format
            "-o", outputDir + "/%(title)s.%(ext)s", // Output format
            videoUrl
        };
        
        
        
    
        // Start the process for video download
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
    
        // Capture and print the output of the command
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String downloadedFilePath = null; // To store the actual downloaded file path
        String title = null; // Variable to store the extracted title
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // Output progress of the download
            if (line.contains("[download] Destination:")) {
                // Extract the actual file path
                downloadedFilePath = line.split("Destination: ")[1].trim();
            } else if (line.contains("[info]")) {
                // Extract title from the [info] line
                title = line.split(" ")[2]; // Adjust this based on the actual output format
            }
        }
    
        // Wait for the process to finish
        process.waitFor();
    
        if (downloadedFilePath == null) {
            throw new IOException("Failed to download video.");
        }
    
        // Check if title was successfully extracted
        if (title == null) {
            throw new IOException("Failed to extract video title from yt-dlp output.");
        }
    
        // Now download the thumbnail using YouTubeInfoExtractor
        YouTubeInfoExtractor infoExtractor = new YouTubeInfoExtractor(apiKey);
        String[] videoInfo = infoExtractor.extractInfo(videoUrl); // Get title, artist, and thumbnail URL
        String thumbnailUrl = videoInfo[2];
    
        // Download the thumbnail
        String localThumbnailPath = downloadThumbnail(thumbnailUrl, title);
    
        return new String[]{downloadedFilePath, localThumbnailPath}; // Return both the video and thumbnail paths
    }
    
    
    
    
    
    private static String downloadThumbnail(String thumbnailUrl, String videoTitle) throws IOException {
        // Create a URL object for the thumbnail
        URL url = new URL(thumbnailUrl);
        
        // Define the local path to save the thumbnail
        String thumbnailDirectory = "C:\\BerdeDaw_FINALPROJECT\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\Downloads\\thumbnails";
        File directory = new File(thumbnailDirectory);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it doesn't exist
        }
    
        // Check for null or empty title
        if (videoTitle == null || videoTitle.isEmpty()) {
            throw new IOException("Video title is null or empty.");
        }
    
        // Sanitize the video title to create a valid filename
        String sanitizedTitle = videoTitle.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_");
        String localThumbnailPath = thumbnailDirectory + File.separator + sanitizedTitle + "_" + System.currentTimeMillis() + ".jpg"; // Create path for thumbnail
    
        // Open input and output streams to download the image
        try (InputStream in = url.openStream()) {
            Files.copy(in, new File(localThumbnailPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    
        System.out.println("Thumbnail downloaded: " + localThumbnailPath);
        return localThumbnailPath;
    }
    
    
    
}
