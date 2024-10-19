package com.berdedaw;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class FirebaseLogin extends Application {

    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize Firebase
        initializeFirebase();

        // Create UI elements
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label headerLabel = new Label("Firebase Login");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        grid.add(headerLabel, 0, 0, 2, 1);

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 1);
        emailField = new TextField();
        grid.add(emailField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);
        passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> login());
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 4);

        messageLabel = new Label("");
        messageLabel.setTextFill(Color.RED);
        grid.add(messageLabel, 1, 5);

        // Create the scene and show the stage
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setTitle("Firebase Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeFirebase() throws IOException {
        // Replace with your Firebase configuration file path
        FileInputStream serviceAccount = new FileInputStream("C:\\BerdeDaw_FINALPROJECT\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\Firebase\\gplay-1918f-firebase-adminsdk-e8ocs-de9fdc72a4.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://gplay-1918f-default-rtdb.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);
    }

    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        messageLabel.setText("Login successful!");
                        // You can access user data here
                        // Example: user.getEmail(), user.getUid()
                    } else {
                        messageLabel.setText("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
