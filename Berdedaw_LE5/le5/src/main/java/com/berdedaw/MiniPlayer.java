package com.berdedaw;

import com.berdedaw.Libraries.PlayVideo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MiniPlayer {
    @FXML
    private AnchorPane miniPane;
    
    @FXML
    private ImageView miniMediaImage;

    @FXML
    private Slider mediaSlider;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label totalDurationLabel;

    @FXML
    private Button playButton;

    @FXML
    private Button resumeButton;

    @FXML
    private Label miniMediaTitle;

    @FXML
    private Label miniMediaAuthor;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button nextButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button returnButton; // Return button reference

    private String mediaUri; // Store the media URI
    private boolean isPlaying = false; // Variable to track playback state

    public void setMediaUri(String uri) {
        this.mediaUri = uri; // Store the media URI for reference
    }

    // Method to play media
    public void playMedia(String fileUri, Duration startTime) {
        if (mediaUri != null) { // Check if mediaUri is set
            PlayVideo.play(mediaUri, mediaView, startTime); // Play the media
        }
    }

    @FXML
    void playButtonClicked(ActionEvent event) {
        if (!isPlaying) { 
            PlayVideo.play(mediaUri, mediaView, Duration.ZERO); 
            isPlaying = true; 
            playButton.setVisible(false); 
            resumeButton.setVisible(true); 
        }
    }

    @FXML
    void resumeButtonClicked(ActionEvent event) {
        if (isPlaying) {
            PlayVideo.pause(); 
            isPlaying = false; 
            playButton.setVisible(true); 
            resumeButton.setVisible(false); 
        } else { 
            PlayVideo.resume();
            isPlaying = true;
            playButton.setVisible(false); 
            resumeButton.setVisible(true); 
        }
    }

    // Method to update media information
    public void updateMediaInfo(String name, String author, String imageUrl) {
        Platform.runLater(() -> {
            miniMediaTitle.setText(name);
            miniMediaAuthor.setText(author);
            loadMediaImage(imageUrl);
        });
    }

    // Load the media image from the URL
    private void loadMediaImage(String imageUrl) {
        new Thread(() -> {
            try {
                Image image = new Image(imageUrl, true); // Set to true to load asynchronously
                Platform.runLater(() -> {
                    miniMediaImage.setImage(image);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    miniMediaImage.setImage(null); // Set to a default image or handle error
                });
            }
        }).start();
    }

    // New method to stop playback when the MiniPlayer is closed
    public void stopMedia() {
        PlayVideo.stop(); // Call the stop method from PlayVideo
    }

    @FXML
    void nextButtonClicked(ActionEvent event) {
        // Next button functionality (not implemented)
    }

    @FXML
    void previousButtonClicked(ActionEvent event) {
        // Previous button functionality (not implemented)
    }

    @FXML
    void handleReturnButtonClicked(ActionEvent event) {
        // Show the primary stage
        Stage primaryStage = App.getPrimaryStage(); // Get the primary stage reference
        if (primaryStage != null) {
            primaryStage.show(); // Show the primary stage
        }
        
        // Close the MiniPlayer
        Stage miniPlayerStage = (Stage) miniPane.getScene().getWindow(); 
        miniPlayerStage.close();
        PlayVideo.stop();
    }
}