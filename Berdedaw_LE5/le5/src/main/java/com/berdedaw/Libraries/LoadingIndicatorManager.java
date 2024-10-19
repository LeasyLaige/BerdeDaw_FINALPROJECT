package com.berdedaw.Libraries;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

@SuppressWarnings("unused")
public class LoadingIndicatorManager {
    private final ProgressBar loadingIndicator;

    @SuppressWarnings("exports")
    public LoadingIndicatorManager(ProgressBar loadingIndicator) {
        this.loadingIndicator = loadingIndicator;
        loadingIndicator.setVisible(false);
    }

    public void startLoading(double durationInSeconds, Runnable onComplete) {
        loadingIndicator.setVisible(true);
        loadingIndicator.setProgress(0); // Start with 0 progress

        // Create a Timeline to simulate loading
        Timeline loadingTimeline = new Timeline();

        // Set the duration and increment based on the total loading time
        double increment = 1.0 / (durationInSeconds * 10); // 10 updates per second
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), event -> {
            double progress = loadingIndicator.getProgress();
            if (progress < 1.0) {
                loadingIndicator.setProgress(progress + increment); // Increment progress
            }
        });

        loadingTimeline.getKeyFrames().add(keyFrame);
        loadingTimeline.setCycleCount((int) (durationInSeconds * 10)); // 10 updates per second

        // Stop loading and call onComplete when done
        loadingTimeline.setOnFinished(event -> {
            loadingIndicator.setVisible(false);
            if (onComplete != null) {
                onComplete.run();
            }
        });

        // Start the loading animation
        loadingTimeline.play();
    }
}
