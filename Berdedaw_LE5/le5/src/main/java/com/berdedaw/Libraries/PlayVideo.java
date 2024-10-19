package com.berdedaw.Libraries;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class PlayVideo {

    private static MediaPlayer mediaPlayer;

    // Accept an external MediaView from PrimaryController
    public static void play(String fileUri, MediaView mediaView, Duration startTime) {
        // Create a new Media object with the provided file URI
        Media media = new Media(fileUri);
        
        // If there's already an active mediaPlayer, stop and dispose of it
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        // Create a MediaPlayer for the Media
        mediaPlayer = new MediaPlayer(media);

        // Set the MediaPlayer to the MediaView so it can display the video
        mediaView.setMediaPlayer(mediaPlayer);

        // Set the start time using the seek method
        mediaPlayer.setOnReady(() -> {
            // Seek to the provided start time in the video
            mediaPlayer.seek(startTime);
            mediaPlayer.play();
        });

        // Add listeners or other configurations as needed
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause(); // Pause the media playback
        }
    }

    public static void resume() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play(); // Resume playback if it was paused
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}


