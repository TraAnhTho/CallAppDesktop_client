package com.example.dacs4.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MeetingControlsController {

    @FXML private Button audioButton;
    @FXML private Button videoButton;
    @FXML private Button screenShareButton;
    @FXML private Button settingsButton;
    @FXML private Button moreButton;
    @FXML private Button leaveButton;

    @FXML private Label audioIcon;
    @FXML private Label videoIcon;
    @FXML private Label screenShareIcon;

    @FXML private Label audioLabel;
    @FXML private Label videoLabel;
    @FXML private Label screenShareLabel;

    private final BooleanProperty audioOn = new SimpleBooleanProperty(true);
    private final BooleanProperty videoOn = new SimpleBooleanProperty(true);
    private final BooleanProperty screenSharing = new SimpleBooleanProperty(false);

    private Runnable leaveMeetingCallback;

    @FXML
    private void initialize() {
        audioOn.addListener((o, oldV, newV) -> updateAudioUI());
        videoOn.addListener((o, oldV, newV) -> updateVideoUI());
        screenSharing.addListener((o, oldV, newV) -> updateScreenShareUI());

        updateAudioUI();
        updateVideoUI();
        updateScreenShareUI();
    }

    // ===== UI Updates =====
    private void updateAudioUI() {
        if (audioOn.get()) {
            audioIcon.setText("🎤");
            audioLabel.setText("Tắt mic");
        } else {
            audioIcon.setText("🔇");
            audioLabel.setText("Bật mic");
        }
    }

    private void updateVideoUI() {
        if (videoOn.get()) {
            videoIcon.setText("📹");
            videoLabel.setText("Tắt camera");
        } else {
            videoIcon.setText("🚫📹");
            videoLabel.setText("Bật camera");
        }
    }

    private void updateScreenShareUI() {
        if (screenSharing.get()) {
            screenShareIcon.setText("🛑🖥");
            screenShareLabel.setText("Dừng chia sẻ");
        } else {
            screenShareIcon.setText("🖥");
            screenShareLabel.setText("Chia sẻ");
        }
    }

    // ===== Events =====
    @FXML
    private void onToggleAudio() {
        audioOn.set(!audioOn.get());
    }

    @FXML
    private void onToggleVideo() {
        videoOn.set(!videoOn.get());
    }

    @FXML
    private void onToggleScreenShare() {
        screenSharing.set(!screenSharing.get());
    }

    @FXML
    private void onOpenSettings() {
        System.out.println("Open settings clicked");
    }

    @FXML
    private void onMoreOptions() {
        System.out.println("More options clicked");
    }

    @FXML
    private void onLeaveMeeting() {
        if (leaveMeetingCallback != null) leaveMeetingCallback.run();
    }

    // ===== Public API =====
    public BooleanProperty audioOnProperty() { return audioOn; }

    public BooleanProperty videoOnProperty() { return videoOn; }

    public BooleanProperty screenSharingProperty() { return screenSharing; }

    public void setLeaveMeetingCallback(Runnable callback) {
        this.leaveMeetingCallback = callback;
    }
}
