module com.berdedaw {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;
    requires javafx.web;
    requires com.google.gson;
    requires java.net.http;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires com.google.firebase.auth;

    opens com.berdedaw to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.berdedaw;
}
