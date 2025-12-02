package com.example.dacs4.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class LoginController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private VBox nameFieldContainer;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button submitButton;
    @FXML private Hyperlink toggleModeLink;

    private boolean isSignUp = false;
    // ====== CALLBACK: để App.java bắt sự kiện login ======
    public interface OnLoginListener {
        void onLogin(String id, String name, String email, String avatar);
    }

    private OnLoginListener loginCallback;

    public void setOnLogin(OnLoginListener cb) {
        this.loginCallback = cb;
    }


    @FXML
    private void initialize() {
        updateModeUI();
    }

    @FXML
    private void onToggleMode() {
        isSignUp = !isSignUp;
        updateModeUI();
    }
    @FXML
    private void onSubmit() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ email và mật khẩu.");
            return;
        }

        String name = nameField.getText().trim();
        if (!isSignUp && name.isEmpty()) {
            int atIndex = email.indexOf('@');
            name = atIndex > 0 ? email.substring(0, atIndex) : email;
        } else if (isSignUp && name.isEmpty()) {
            showAlert("Vui lòng nhập họ tên.");
            return;
        }

        // Mock user
        String id = UUID.randomUUID().toString().substring(0, 9);
        String avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=" + email;

        System.out.println("=== LOGIN SUCCESS ===");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Avatar: " + avatarUrl);

        // 🔥 Nếu App.java đã set callback -> dùng callback để navigate
        if (loginCallback != null) {
            loginCallback.onLogin(id, name, email, avatarUrl);
            return; // dừng luôn, KHÔNG load FXML nữa
        }

        // ❗ Nếu không có callback -> fallback load Dashboard trực tiếp
        try {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            goToDashboard(stage, id, name, email, avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở Dashboard. Vui lòng thử lại.");
        }
    }

    /** Chuyển sang màn Dashboard và truyền thông tin user */
    private void goToDashboard(Stage stage,
                               String userId,
                               String name,
                               String email,
                               String avatarUrl) throws IOException {

        // 1. thử load từ classpath
        URL url = getClass().getResource("/fxml/dashboard.fxml");
        System.out.println(">>> DASHBOARD FXML (classpath) = " + url);

        // 2. nếu null thì fallback sang đường dẫn file (giống App)
        if (url == null) {
            File fxmlFile = new File("frontend/src/main/resources/fxml/dashboard.fxml");
            System.out.println(">>> DASHBOARD FXML file exists = " + fxmlFile.exists()
                    + ", path = " + fxmlFile.getAbsolutePath());
            url = fxmlFile.toURI().toURL();
        }

        FXMLLoader loader = new FXMLLoader(url);
        Scene scene = new Scene(loader.load(), 1200, 800);

        // truyền user sang DashboardController
        DashboardController controller = loader.getController();
        controller.setUser(userId, name, email, avatarUrl);

        stage.setScene(scene);
        stage.setTitle("MeetHub - Dashboard");
        stage.show();
    }


    private void updateModeUI() {
        if (isSignUp) {
            titleLabel.setText("Tạo tài khoản");
            descriptionLabel.setText("Điền thông tin để tạo tài khoản mới");
            submitButton.setText("Tạo tài khoản");
            toggleModeLink.setText("Đã có tài khoản? Đăng nhập");
            nameFieldContainer.setVisible(true);
            nameFieldContainer.setManaged(true);
        } else {
            titleLabel.setText("Đăng nhập");
            descriptionLabel.setText("Đăng nhập để tham gia cuộc họp");
            submitButton.setText("Đăng nhập");
            toggleModeLink.setText("Chưa có tài khoản? Đăng ký");
            nameFieldContainer.setVisible(false);
            nameFieldContainer.setManaged(false);
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
