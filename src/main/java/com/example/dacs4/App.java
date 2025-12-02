package com.example.dacs4;

import com.example.dacs4.controllers.DashboardController;
import com.example.dacs4.controllers.LoginController;
import com.example.dacs4.controllers.MeetingRoomController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class App extends Application {

    private Stage mainStage;

    private String currentUserId;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserAvatar;

    // ======================================================
    //  FIND FXML (CHUẨN MỘT MODULE)
    // ======================================================
    private URL findFXML(String path) {
        // 1. tìm trong classpath chuẩn Maven
        URL url = App.class.getResource("/" + path);
        if (url != null) return url;

        // 2. fallback: load từ file hệ thống khi run từ IDE
        File f = new File("src/main/resources/" + path);
        System.out.println("[DEBUG] File absolute = " + f.getAbsolutePath() + ", exists = " + f.exists());
        if (f.exists()) {
            try {
                return f.toURI().toURL();
            } catch (Exception ignored) {}
        }

        System.err.println("[ERROR] Không tìm thấy FXML: " + path);
        return null;
    }

    // ======================================================
    //  FIND CSS
    // ======================================================
    private URL findCSS(String path) {
        URL url = App.class.getResource("/" + path);
        if (url != null) return url;

        File f = new File("src/main/resources/" + path);
        if (f.exists()) {
            try {
                return f.toURI().toURL();
            } catch (Exception ignored) {}
        }

        System.err.println("[WARN] Không tìm thấy CSS: " + path);
        return null;
    }

    private void applyCSS(Scene scene, String cssPath) {
        URL css = findCSS(cssPath);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        goToLogin();
    }

    // ======================================================
    //  LOGIN SCREEN
    // ======================================================
    private void goToLogin() {
        try {
            URL url = findFXML("fxml/login.fxml");
            if (url == null) return;

            FXMLLoader loader = new FXMLLoader(url);
            Parent ui = loader.load();

            LoginController controller = loader.getController();
            controller.setOnLogin((id, name, email, avatar) -> {
                currentUserId = id;
                currentUserName = name;
                currentUserEmail = email;
                currentUserAvatar = avatar;
                goToDashboard();
            });

            Scene scene = new Scene(ui, 1200, 800);
            applyCSS(scene, "css/style.css");

            mainStage.setScene(scene);
            mainStage.setTitle("MeetHub - Login");
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    //  DASHBOARD
    // ======================================================
    public void goToDashboard() {
        try {
            URL url = findFXML("fxml/dashboard.fxml");
            if (url == null) return;

            FXMLLoader loader = new FXMLLoader(url);
            Parent ui = loader.load();

            DashboardController controller = loader.getController();
            controller.setUser(currentUserId, currentUserName, currentUserEmail, currentUserAvatar);

            controller.setOnJoinMeeting(this::goToMeetingRoom);

            controller.setOnLogout(this::goToLogin);

            Scene scene = new Scene(ui, 1200, 800);
            applyCSS(scene, "css/dashboard.css");

            mainStage.setScene(scene);
            mainStage.setTitle("MeetHub - Dashboard");
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    //  MEETING ROOM
    // ======================================================
    public void goToMeetingRoom(String meetingId) {
        try {
            URL url = findFXML("fxml/meetingRoom.fxml");
            if (url == null) return;

            FXMLLoader loader = new FXMLLoader(url);
            Parent ui = loader.load();

            MeetingRoomController controller = loader.getController();
            controller.setMeetingId(meetingId);
            controller.setUser(currentUserId, currentUserName, currentUserAvatar);
            controller.setOnLeaveMeeting(this::goToDashboard);

            Scene scene = new Scene(ui, 1200, 800);
            applyCSS(scene, "css/meetingroom.css");

            mainStage.setScene(scene);
            mainStage.setTitle("MeetHub - Room " + meetingId);
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    public static void main(String[] args) {
        System.out.println(">>> ROOT = " + App.class.getResource("/"));
        launch();
    }
}
