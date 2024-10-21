package com.berdedaw;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        FileInputStream serviceAccount =
        new FileInputStream("C:\\BerdeDaw_FINALPROJECT\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\Firebase\\gplay-1918f-firebase-adminsdk-e8ocs-c6ec38a53d.json");

        @SuppressWarnings("deprecation")
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://gplay-1918f-default-rtdb.firebaseio.com")
            .build();

        FirebaseApp.initializeApp(options);
    }

    private void login() {
    String email = emailField.getText();
    String password = passwordField.getText();

    // Reference to the Firebase Realtime Database
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    // Check if user exists and validate password
    ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String storedPassword = userSnapshot.child("password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        messageLabel.setText("Login successful!");
                        return; // Exit after successful login
                    }
                }
                messageLabel.setText("Login failed: Incorrect password.");
            } else {
                messageLabel.setText("Login failed: User does not exist.");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            messageLabel.setText("Database error: " + databaseError.getMessage());
        }
    });
}

    public static void main(String[] args) {
        launch(args);
    }
}
