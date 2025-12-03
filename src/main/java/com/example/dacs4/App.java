package com.example.dacs4;

import com.example.dacs4.controllers.ServerConfigController;
import com.example.dacs4.controllers.LoginController;
import com.example.dacs4.controllers.DashboardController;
import com.example.dacs4.controllers.MeetingRoomController;
import com.example.dacs4.network.SocketClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    private static Stage mainStage;

    // ===============================
    // GLOBAL SOCKET CLIENT
    // ===============================
    private static SocketClient socketClient;

    public static void setSocketClient(SocketClient client) {
        socketClient = client;
    }

    public static SocketClient getSocketClient() {
        return socketClient;
    }

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        goToServerConfig();   // 🔥 mở màn hình nhập IP đầu tiên
    }

    private URL getFXML(String name) {
        return getClass().getResource("/fxml/" + name);
    }

    // ==========================
    // SERVER CONFIG SCREEN
    // ==========================
    public static void goToServerConfig() {
        try {
            URL url = App.class.getResource("/fxml/serverConfig.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent ui = loader.load();

            Scene scene = new Scene(ui, 600, 400);
            mainStage.setScene(scene);
            mainStage.setTitle("Kết nối server");
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // ==========================
    // LOGIN SCREEN
    // ==========================
    public static void goToLogin() {
        try {
            URL url = App.class.getResource("/fxml/login.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent ui = loader.load();

            Scene scene = new Scene(ui, 1200, 800);
            mainStage.setScene(scene);
            mainStage.setTitle("MeetHub - Login");
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ==========================
    // DASHBOARD
    // ==========================
    public void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getFXML("dashboard.fxml"));
            Parent ui = loader.load();

            DashboardController controller = loader.getController();

            controller.setOnJoinMeeting(meetingId -> {
                goToMeetingRoom(meetingId);
            });

            Scene scene = new Scene(ui, 1200, 800);
            mainStage.setScene(scene);
            mainStage.setTitle("Dashboard");
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // MEETING ROOM
    // ==========================
    public void goToMeetingRoom(String meetingId) {
        try {
            FXMLLoader loader = new FXMLLoader(getFXML("meetingRoom.fxml"));
            Parent ui = loader.load();

            MeetingRoomController controller = loader.getController();

            controller.setMeetingId(meetingId);
            controller.setSocketClient(socketClient);

            Scene scene = new Scene(ui, 1200, 800);
            mainStage.setScene(scene);
            mainStage.setTitle("Room " + meetingId);
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
