package com.berdedaw;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class PrimaryController {

    @FXML
    private MediaView mediaView;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private ListView<String> listView;

    @FXML
    private Label mediaAuthor;

    @FXML
    private Slider mediaSlider;

    @FXML
    private Label mediaTitle;

    @FXML
    private Button playBackButton;

    @FXML
    private Button playFastForwardButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalDurationLabel;

    @FXML
    private Button playButton;

    @FXML
    private ImageView mediaImage;

    @FXML
    private ProgressBar loadingIndicator;

    @FXML
    private Button resumeButton;

    private int currentVideoDuration = 0, maxValueDuration = 0;
    Duration startTime = Duration.seconds(currentVideoDuration);

    // Instantiate ModifyPlaylistJson with the correct file path
    private ModifyPlaylistJson modifyPlaylistJson = new ModifyPlaylistJson("C:\\Users\\john matthew\\Downloads\\Berdedaw_LE5 - Copy(2) (1)\\Berdedaw_LE5 - Copy(5) (1)\\Berdedaw_LE5 - Copy(2)\\Berdedaw_LE5 - Copy\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\playlist.json");

    // Instantiate YouTubeInfoExtractor with your API key
    private YouTubeInfoExtractor extractor = new YouTubeInfoExtractor("AIzaSyBOdjUVG0KzSPPmN4pvdy9C_k3cexUEqMo");

    private LoadingIndicatorManager loadingIndicatorManager;

    @FXML
    public void initialize() {
        listView.setCellFactory(lv -> new CustomListCell());
        listView.getStylesheets().add(getClass().getResource("/com/berdedaw/listView.css").toExternalForm());
        loadItemsFromJson();
        loadingIndicatorManager = new LoadingIndicatorManager(loadingIndicator);

        // Add a ChangeListener to handle selection changes
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateMediaInfo(newValue);
            }
        });
    }
    


    @FXML
    void searchButtonClicked(MouseEvent event) throws InterruptedException {
        handleYouTubeSearch();
    }

    @FXML
    void searchButtonHoverEntered(MouseEvent event) {
        searchButton.setStyle("-fx-background-color:lightgray; -fx-text-fill: black;");
    }

    @FXML
    void searchButtonHoverExited(MouseEvent event) {
        searchButton.setStyle("-fx-background-color:black;-fx-text-fill: white;");
    }

    @FXML
    void searchFieldEntered(KeyEvent event) throws InterruptedException {
        if (event.getCode() == KeyCode.ENTER) {
            handleYouTubeSearch();
        }
    }

    private void loadItemsFromJson() {
        try {
            JsonNode root = modifyPlaylistJson.readJsonFile();
            if (root.has("configurations")) {
                ArrayNode configurations = (ArrayNode) root.get("configurations");
                for (JsonNode config : configurations) {
                    String title = config.get("mediaTitle").asText();
                    String author = config.get("mediaAuthor").asText();
                    listView.getItems().add(title + " by " + author);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void nextSong() throws IOException {
    int currentIndex = 0;
        if(listView.getSelectionModel().getSelectedIndex() >= 0 && currentIndex < listView.getItems().size()) {
            currentIndex = listView.getSelectionModel().getSelectedIndex();
            listView.getSelectionModel().select(currentIndex+1);
            currentVideoDuration = 0;
            mediaSlider.setValue(0);
            currentTimeLabel.setText("0:00");
            playButtonClicked();   
        }
    }

    @FXML
    public void previousSong() throws IOException {
        int currentIndex = 0;
        if(listView.getSelectionModel().getSelectedIndex() > 0 && currentIndex < listView.getItems().size()) {
            currentIndex = listView.getSelectionModel().getSelectedIndex();
            listView.getSelectionModel().select(currentIndex-1); 
            currentVideoDuration = 0;
            mediaSlider.setValue(0);
            currentTimeLabel.setText("0:00");
            playButtonClicked();    
        }
    }

    @FXML
    private void handleYouTubeSearch() {
        String youtubeUrl = searchField.getText().trim();
        if (!youtubeUrl.isEmpty()) {
            loadingIndicator.setVisible(true);
            new Thread(() -> {
                try {
                    // Download the video and thumbnail
                    String[] localPaths = DownloadVideo.downloadVideoAndThumbnail(youtubeUrl);
                    String videoPath = localPaths[0];
                    String thumbnailPath = localPaths[1];
    
                    // Extract title, artist, and thumbnail URL
                    String[] info = extractor.extractInfo(youtubeUrl);
                    String title = info[0];
                    String artist = info[1];
                    @SuppressWarnings("unused")
                    String thumbnailUrl = info[2];
    
                    // Update the JSON with the new media
                    modifyPlaylistJson.modifyPlaylist(title, artist, youtubeUrl, videoPath, thumbnailPath);
    
                    // Add the new item to the ListView
                    String newItem = title + " by " + artist;
                    Platform.runLater(() -> {
                        listView.getItems().add(newItem);
                        listView.getSelectionModel().select(newItem); // Optionally select the new item
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> loadingIndicator.setVisible(false));
                }
            }).start();
        }
    }
    
    
    private void updateMediaInfo(String selectedItem) {
        if (selectedItem != null && !selectedItem.isEmpty()) {
            String[] parts = selectedItem.split(" by ");
            if (parts.length == 2) {
                String title = parts[0].trim();
                String artist = parts[1].trim();
    
                // Debugging output to ensure correct values
                System.out.println("Searching for title: " + title + ", artist: " + artist);
    
                // Load media info for the selected item
                mediaImage.setVisible(false);
                mediaTitle.setVisible(false);
                mediaAuthor.setVisible(false);
                totalDurationLabel.setVisible(false);
                playButton.setVisible(false);
                playBackButton.setVisible(false);
                playFastForwardButton.setVisible(false);
                currentTimeLabel.setVisible(false);
                mediaSlider.setVisible(false);
                resumeButton.setVisible(false);
                
    
                loadingIndicatorManager.startLoading(2, () -> {
                    loadMediaInfo(title, artist); // Load media info including thumbnail
                });
            } else {
                System.out.println("Selected item does not contain valid title and artist.");
            }
        } else {
            System.out.println("No item selected or the item is invalid.");
        }
    }
    
    
    public static String formatISO8601Duration(String isoDuration) {
        String duration = isoDuration.substring(2); // Remove "PT"
        int hours = 0, minutes = 0, seconds = 0;

        if (duration.contains("H")) {
            hours = Integer.parseInt(duration.split("H")[0]);
            duration = duration.split("H")[1];
        }
        if (duration.contains("M")) {
            minutes = Integer.parseInt(duration.split("M")[0]);
            duration = duration.split("M")[1];
        }
        if (duration.contains("S")) {
            seconds = Integer.parseInt(duration.split("S")[0]);
        }

        if (hours == 0) {
            return String.format("%2d:%02d", minutes, seconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);    
        }
    }

    private void loadMediaInfo(String title, String artist) {
        // Show media title and artist first
        Platform.runLater(() -> {
            mediaTitle.setText(title);
            mediaAuthor.setText(artist);
            mediaTitle.setVisible(true);
            mediaAuthor.setVisible(true);
    
            // Show other controls again immediately
            totalDurationLabel.setVisible(true);
            playButton.setVisible(true);
            playBackButton.setVisible(true);
            playFastForwardButton.setVisible(true);
            currentTimeLabel.setVisible(true);
            mediaSlider.setVisible(true);
            resumeButton.setVisible(true);
   
        });
    
        // Now load the image in the background
        try {
            String youtubeUrl = modifyPlaylistJson.getYouTubeUrl(title, artist);
            if (youtubeUrl != null) {
                new Thread(() -> {
                    try {
                        String[] videoInfo = extractor.extractInfo(youtubeUrl);
                        String thumbnailUrl = videoInfo[2];
                        Image image = new Image(thumbnailUrl);

                        // Update the ImageView on the JavaFX Application Thread
                        Platform.runLater(() -> {
                            mediaImage.setImage(image);
                            mediaImage.setFitHeight(257);
                            mediaImage.setFitWidth(350);
                            mediaImage.setLayoutX(100);
                            mediaImage.setLayoutY(23);
                            mediaImage.setVisible(true);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false); // Hide loading
                        });
                    }
                }).start();
            } else {
                System.err.println("YouTube URL not found for the selected item.");
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false); // Hide loading on failure
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    private boolean isPlaying = false; // Track the playback state
    @FXML
    private boolean isPaused = false; // Track if the media is paused
    
    KeyFrame keyframe = new KeyFrame(Duration.seconds(1), ev -> {incrementLabel();});
    Timeline timeline = new Timeline(keyframe);
    
    @FXML
    void playButtonClicked() throws IOException {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        currentVideoDuration = (int) mediaSlider.getValue();
        if (selectedItem != null) {
            String[] parts = selectedItem.split(" by ");
            if (parts.length == 2) {
                String youtubeURL = modifyPlaylistJson.getYouTubeUrl(parts[0], parts[1]);
                String[] videoInfo = extractor.extractInfo(youtubeURL);
                String title = parts[0].trim();
                String artist = parts[1].trim();

                String duration = PrimaryController.formatISO8601Duration(videoInfo[3]);
                String localPath = modifyPlaylistJson.getLocalPath(title, artist);
                System.out.println("Local path retrieved: " + localPath); // Debugging line

                if (localPath != null) {
                    File file = new File(localPath);
                    System.out.println("Checking file: " + file.getAbsolutePath()); // Print the full path

                    if (file.exists()) {
                        System.out.println("File exists: " + file.getAbsolutePath());

                        // Check for supported media formats
                        String lowerPath = localPath.toLowerCase();
                        if (lowerPath.endsWith(".mp4") || 
                            lowerPath.endsWith(".m4a") || 
                            lowerPath.endsWith(".webm") || 
                            lowerPath.endsWith(".mp3") || 
                            lowerPath.endsWith(".wav")) {

                            try {
                                String fileUri = file.toURI().toString();
                                System.out.println("File URI: " + fileUri); // Debugging line

                                double playWidth = playButton.getWidth(), playHeight = playButton.getHeight();
                                maxValueDuration = (Integer.parseInt(duration.split(":")[0].trim()) * 60 + Integer.parseInt(duration.split(":")[1].trim()));

                                mediaSlider.setMax(maxValueDuration);                              

                                timeline.setCycleCount(Timeline.INDEFINITE);
                                
                                totalDurationLabel.setText(duration);

                                if (isPlaying) {
                                    PlayVideo.pause(); // Pause the media if it's already playing
                                    playButton.setMinWidth(playWidth);
                                    playButton.setMinHeight(playHeight);
                                    playButton.setVisible(true); // Show the play button on pause
                                    resumeButton.setVisible(false); // Show resume button when paused
                                    isPlaying = false;
                                    timeline.pause();
                                    
                                } else {
                                    PlayVideo.play(fileUri, mediaView, startTime); // Play the media initially
                                    isPlaying = true; // Set the state as playing
                                    playButton.setVisible(false);
                                    playButton.setMinWidth(0);
                                    playButton.setMinHeight(0); // Hide play button during playback
                                    resumeButton.setVisible(true); // Show resume button while playing
                                    timeline.play();
                                }
                    

                            } catch (MediaException e) {
                                System.err.println("Error playing media: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("The selected file is not a supported media file.");
                        }
                    } else {
                        System.err.println("File does not exist: " + file.getAbsolutePath());
                    }
                } else {
                    System.err.println("Local path is null for the selected media.");
                }
            } else {
                System.out.println("Selected item does not contain valid title and artist.");
            }
        } else {
            System.out.println("No item selected.");
        }
    }

    public void incrementLabel() {
        if (currentVideoDuration < maxValueDuration) {
            Integer.toString(currentVideoDuration);
            int minutes = currentVideoDuration/60;
            int seconds = currentVideoDuration - (minutes * 60);

            currentTimeLabel.setText(String.format("%2d:%02d", minutes, seconds));     
            mediaSlider.setValue(currentVideoDuration);
            currentVideoDuration++; 
        } 
    }

    public void setVideoSlider() throws IOException {
        startTime = Duration.seconds(mediaSlider.getValue());
        timeline.pause();
        playButtonClicked();
    }
}