package com.berdedaw.Libraries;

import java.io.IOException;

import com.berdedaw.PrimaryController;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CustomListCell extends ListCell<String> {
    private YouTubeInfoExtractor extractor = new YouTubeInfoExtractor("AIzaSyBOdjUVG0KzSPPmN4pvdy9C_k3cexUEqMo");
    private ModifyPlaylistJson modifyPlaylistJson = new ModifyPlaylistJson("C:\\Users\\Janxen\\Documents\\GitHub\\BerdeDaw_FINALPROJECT\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\playlist.json");

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            ImageView imageView = new ImageView();
            String[] parts = item.split(" by ");
            String duration = null;

            try {
                String youtubeURL = modifyPlaylistJson.getYouTubeUrl(parts[0], parts[1]);
                String[] videoInfo = extractor.extractInfo(youtubeURL);
                String thumbnailUrl = videoInfo[2];
                Image image = new Image(thumbnailUrl);
                duration = PrimaryController.formatISO8601Duration(videoInfo[3]);
                imageView.setImage(image);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            imageView.setPreserveRatio(false);

            Rectangle clip = new Rectangle(30, 30);
            clip.setArcWidth(10);
            clip.setArcHeight(10);

            imageView.setClip(clip);

            HBox hBox = new HBox(10);
            hBox.setPadding(new Insets(5));
            hBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            hBox.prefWidthProperty().bind(getListView().widthProperty().subtract(20));

            Label durationLabel = new Label();
            HBox timeContainer = new HBox();
            timeContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            timeContainer.setHgrow(timeContainer, Priority.ALWAYS);
            durationLabel.setText(duration);
            durationLabel.setTextFill(Color.GRAY); // Default text color is gray
            durationLabel.setStyle("-fx-font-family: 'Franklin Gothic Demi Cond'; -fx-font-size: 14px;");
            timeContainer.getChildren().add(durationLabel);

            VBox vBox = new VBox();

            String titleText = parts[0];
            if (titleText.contains(" - ")) {
                titleText = titleText.split(" - ")[1];
            }
            if (titleText.contains(" ( ")) {
                titleText = titleText.split(" \\( ")[0];
            }
            if (titleText.contains(" [ ")) {
                titleText = titleText.split(" \\[ ")[0];
            }
            
            // Truncate title to 20 characters and add ellipsis if necessary
            if (titleText.length() > 25) {
                titleText = titleText.substring(0, 25) + "...";
            }
            
            Label title = new Label(titleText);
            Label author = new Label(parts[1]);

            vBox.getChildren().addAll(title, author);

            title.setTextFill(Color.WHITE); // Default text color is white
            title.setStyle("-fx-font-family: 'Franklin Gothic Demi Cond'; -fx-font-size: 14px;");
            author.setTextFill(Color.GRAY); // Default text color is gray
            author.setStyle("-fx-font-family: 'Franklin Gothic Demi Cond'; -fx-font-size: 14px;");

            hBox.setPrefHeight(40);

            // Background color for each item
            hBox.setBackground(new Background(new BackgroundFill(Color.rgb(30, 33, 38), null, null)));

            Timeline hoverInTimeline = new Timeline(
                new KeyFrame(Duration.millis(80), 
                    new KeyValue(author.textFillProperty(), Color.WHITE), 
                    new KeyValue(hBox.backgroundProperty(), new Background(new BackgroundFill(Color.rgb(44, 48, 54), null, null))) 
                )
            );

            Timeline hoverOutTimeline = new Timeline(
                new KeyFrame(Duration.millis(80), 
                    new KeyValue(author.textFillProperty(), Color.GRAY),  
                    new KeyValue(hBox.backgroundProperty(), new Background(new BackgroundFill(Color.rgb(30, 33, 38), null, null))) 
                )
            );


            hBox.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                hoverOutTimeline.stop(); 
                hoverInTimeline.play(); 
            });

            hBox.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                hoverInTimeline.stop(); 
                hoverOutTimeline.play(); 
            });

            if(isSelected()) {
                title.setTextFill(Color.rgb(74, 223, 146));
                title.setStyle("-fx-font-family: 'Franklin Gothic Demi Cond'; -fx-font-size: 16px;");
            }

            hBox.getChildren().addAll(imageView, vBox, timeContainer);
            setGraphic(hBox); 
        }
    }
}
