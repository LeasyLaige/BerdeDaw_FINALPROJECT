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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
            new FileInputStream("C:\\BerdeDaw_FINALPROJECT\\Berdedaw_LE5\\le5\\src\\main\\resources\\com\\berdedaw\\Firebase\\gplay-1918f-firebase-adminsdk-e8ocs-31f67cfa38.json");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://gplay-1918f-default-rtdb.firebaseio.com")
            .build();

        FirebaseApp.initializeApp(options);
    }

    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            String idToken = signInWithEmailAndPassword(email, password);
            messageLabel.setText("Login successful! ID Token: " + idToken);
            fetchUserData(idToken); // Fetch user data after successful login
        } catch (IOException e) {
            messageLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String signInWithEmailAndPassword(String email, String password) throws IOException {
        String apiKey = "AIzaSyATVnKiLc45c-d6yii2EhhtFvpfw6CJBAU"; // Replace with your actual API key
        String urlString = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // JSON payload
        String jsonInputString = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.flush();
            os.close();
        }

       // Check for HTTP response code
       int responseCode = connection.getResponseCode();
       
       if (responseCode == HttpURLConnection.HTTP_OK) {
           // Read response
           try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
               StringBuilder response = new StringBuilder();
               String responseLine;

               while ((responseLine = br.readLine()) != null) {
                   response.append(responseLine.trim());
               }
               return extractIdToken(response.toString());
           }
       } else {
           // Handle error response
           throw new IOException("HTTP error code: " + responseCode + ", Message: " + connection.getResponseMessage());
       }
   }

   private String extractIdToken(String jsonResponse) {
       // Simple extraction logic (you might want to use a JSON library like Gson or Jackson)
       String tokenPrefix = "\"idToken\":\"";
       int startIndex = jsonResponse.indexOf(tokenPrefix) + tokenPrefix.length();
       int endIndex = jsonResponse.indexOf("\"", startIndex);
       
       return jsonResponse.substring(startIndex, endIndex); 
   }

   private void fetchUserData(String idToken) {
       // Use the ID token to authenticate requests to the Realtime Database
       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(idToken); // Adjust based on your data structure

       ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                   // Process user data here
                   String userData = dataSnapshot.getValue(String.class); // Adjust based on your data structure
                   System.out.println("User Data: " + userData);
               } else {
                   System.out.println("No user data found.");
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