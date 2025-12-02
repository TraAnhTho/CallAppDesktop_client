package com.example.dacs4.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class DashboardController {

    @FXML private Label avatarInitialLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private VBox upcomingMeetingsContainer;

    // callback giống props TSX
    private Consumer<String> joinMeetingHandler;
    private Runnable logoutHandler;

    private final List<Meeting> upcomingMeetings = new ArrayList<>();
    private String lastCopiedMeetingId = null;

    // ----- Model Meeting (giống type Meeting TS) -----
    private static class Meeting {
        String id;
        String title;
        String time;
        int participants;
        Status status;

        enum Status { UPCOMING, LIVE, ENDED }

        Meeting(String id, String title, String time, int participants, Status status) {
            this.id = id;
            this.title = title;
            this.time = time;
            this.participants = participants;
            this.status = status;
        }
    }

    private String userId;
    private String userName;
    private String userEmail;
    private String userAvatar;

    public void setUser(String id, String name, String email, String avatarUrl) {
        this.userId = id;
        this.userName = name;
        this.userEmail = email;
        this.userAvatar = avatarUrl;

        // TODO: cập nhật UI (label tên, email, avatar...) nếu cần
        System.out.println("Dashboard nhận user: " + name);
    }


    @FXML
    private void initialize() {
        // Mock data giống TSX
        upcomingMeetings.add(new Meeting("MTG-001",
                "Họp nhóm dự án ABC",
                "14:00 - Hôm nay",
                5,
                Meeting.Status.UPCOMING));
        upcomingMeetings.add(new Meeting("MTG-002",
                "Review Sprint Planning",
                "16:30 - Hôm nay",
                8,
                Meeting.Status.UPCOMING));
        upcomingMeetings.add(new Meeting("MTG-003",
                "Gặp khách hàng XYZ",
                "09:00 - Ngày mai",
                3,
                Meeting.Status.UPCOMING));

        renderUpcomingMeetings();
    }

    // ----- API giống props Dashboard -----

    public void setOnJoinMeeting(Consumer<String> handler) {
        this.joinMeetingHandler = handler;
    }

    public void setOnLogout(Runnable handler) {
        this.logoutHandler = handler;
    }

    public void setUser(String name, String email) {
        userNameLabel.setText(name);
        userEmailLabel.setText(email);
        if (name != null && !name.isEmpty()) {
            avatarInitialLabel.setText(name.substring(0, 1).toUpperCase());
        }
    }

    // Nếu bạn có class User riêng:
    // public void setUser(User user) { ... }

    // ----- Handlers cho FXML -----

    @FXML
    private void onCreateNewMeeting() {
        // Tạo ID ngẫu nhiên cho cuộc họp
        String newMeetingId = "MTG-" + new Random().nextInt(100000000);

        // Nếu có handler joinMeeting, truyền ID cuộc họp và điều hướng đến MeetingRoom
        if (joinMeetingHandler != null) {
            joinMeetingHandler.accept(newMeetingId);  // Truyền ID cuộc họp vào hàm callback
        }
    }

    @FXML
    private void onLogoutClicked() {
        if (logoutHandler != null) {
            logoutHandler.run();
        }
    }

    // ----- Render list cuộc họp sắp tới -----

    private void renderUpcomingMeetings() {
        upcomingMeetingsContainer.getChildren().clear();

        for (Meeting meeting : upcomingMeetings) {
            upcomingMeetingsContainer.getChildren().add(createMeetingCard(meeting));
        }
    }

    private Pane createMeetingCard(Meeting meeting) {
        VBox card = new VBox();
        card.getStyleClass().add("meeting-card");
        card.setSpacing(8);

        Label titleLabel = new Label(meeting.title);
        titleLabel.getStyleClass().add("meeting-title");

        Label timeLabel = new Label("⏰ " + meeting.time);
        Label participantsLabel = new Label("👥 " + meeting.participants + " người tham gia");

        HBox infoRow = new HBox(16, timeLabel, participantsLabel);
        infoRow.getStyleClass().add("meeting-info-row");

        // Copy ID button
        Button copyButton = new Button(meeting.id);
        copyButton.getStyleClass().add("copy-id-button");
        copyButton.setOnAction(e -> copyMeetingId(meeting.id, copyButton));

        // Join button
        // Join button trong DashboardController
        Button joinButton = new Button("Tham gia");
        joinButton.getStyleClass().add("primary-button");
        joinButton.setOnAction(e -> {
            if (joinMeetingHandler != null) {
                joinMeetingHandler.accept(meeting.id);  // Điều hướng tới Meeting Room khi tham gia
            }
        });

        HBox bottomRow = new HBox(16);
        bottomRow.getChildren().addAll(copyButton, joinButton);
        bottomRow.getStyleClass().add("meeting-bottom-row");

        card.getChildren().addAll(titleLabel, infoRow, bottomRow);
        return card;
    }

    // ----- Copy meeting ID + hiệu ứng "copied" 2s -----

    private void copyMeetingId(String id, Button button) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(id);
        clipboard.setContent(content);

        lastCopiedMeetingId = id;
        String originalText = button.getText();
        button.setText("Đã copy ✔");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            if (id.equals(lastCopiedMeetingId)) {
                button.setText(originalText);
                lastCopiedMeetingId = null;
            }
        });
        pause.play();
    }

    @FXML
    private void onOpenJoinMeetingDialog() {
        try {
            // Tìm joinMeetingDialog.fxml theo nhiều cách giống App
            java.net.URL url = DashboardController.class.getResource("/fxml/joinMeetingDialog.fxml");
            if (url == null) {
                url = DashboardController.class.getResource("fxml/joinMeetingDialog.fxml");
            }

            if (url == null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    url = cl.getResource("fxml/joinMeetingDialog.fxml");
                }
            }

            if (url == null) {
                System.err.println("[WARN] joinMeetingDialog.fxml không có trên classpath, thử đường dẫn file tuyệt đối.");

                // TH1: chạy từ root module frontend (working dir = frontend)
                java.io.File f1 = new java.io.File("src/main/resources/fxml/joinMeetingDialog.fxml");
                System.err.println("[DEBUG] JD TH1 ABS = " + f1.getAbsolutePath() + ", exists = " + f1.exists());
                if (f1.exists()) {
                    url = f1.toURI().toURL();
                }

                // TH2: chạy từ root project (working dir = CallAppDesktop_dacs4)
                if (url == null) {
                    java.io.File f2 = new java.io.File("frontend/src/main/resources/fxml/joinMeetingDialog.fxml");
                    System.err.println("[DEBUG] JD TH2 ABS = " + f2.getAbsolutePath() + ", exists = " + f2.exists());
                    if (f2.exists()) {
                        url = f2.toURI().toURL();
                    }
                }
            }

            if (url == null) {
                System.err.println("[ERROR] Vẫn không tìm được joinMeetingDialog.fxml. Không thể mở dialog tham gia cuộc họp.");
                return;
            }

            // Load dialog từ FXML và ủy quyền join cho callback
            FXMLLoader loader = new FXMLLoader(url);
            DialogPane dialogPane = loader.load();

            JoinMeetingDialogController controller = loader.getController();
            controller.setOnJoin(meetingId -> {
                if (joinMeetingHandler != null) {
                    // Gửi mã cuộc họp về App qua callback
                    joinMeetingHandler.accept(meetingId);
                }
            });

            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Tham gia cuộc họp");
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
