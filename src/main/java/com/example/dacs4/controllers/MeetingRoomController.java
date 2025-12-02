package com.example.dacs4.controllers;

import com.example.dacs4.models.ChatMessage;
import com.example.dacs4.models.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MeetingRoomController {

    // ===== HEADER UI =====
    @FXML private Label meetingTitleLabel;
    @FXML private Label participantsCountLabel;

    @FXML private Button chatButton;
    @FXML private Button participantsButton;
    @FXML private Button fileSharingButton;

    // ===== SIDE PANEL =====
    @FXML private VBox sidePanel;
    @FXML private StackPane chatPanelRoot;
    @FXML private StackPane participantsPanelRoot;
    @FXML private StackPane fileSharingPanelRoot;

    // ===== VIDEO GRID (đã include) =====
    @FXML private VideoGridController videoGridController;

    // ===== MEETING CONTROLS (đã include) =====
    @FXML private MeetingControlsController meetingControlsController;

    // ===== STATE =====
    private String currentUserId;
    private String currentUserName;
    private String currentUserAvatar;
    private String meetingId;

    private boolean showChat = false;
    private boolean showParticipants = false;
    private boolean showFileSharing = false;

    private boolean mockInitialized = false;

    private final List<Participant> participants = new ArrayList<>();
    private final List<ChatMessage> messages = new ArrayList<>();

    private Runnable leaveMeetingCallback;


    // ============================================================
    // INITIALIZE — JavaFX GỌI SAU KHI TẤT CẢ @FXML được inject
    // ============================================================
    @FXML
    private void initialize() {

        // Side panel ẩn mặc định
        sidePanel.setVisible(false);
        sidePanel.setManaged(false);

        // ===== MeetingControls Controller =====
        if (meetingControlsController != null) {
            meetingControlsController.audioOnProperty().addListener((o, oldV, newV) -> {
                updateCurrentUserAudio(newV);
            });

            meetingControlsController.videoOnProperty().addListener((o, oldV, newV) -> {
                updateCurrentUserVideo(newV);
            });

            meetingControlsController.screenSharingProperty().addListener((o, oldV, newV) -> {
                updateCurrentUserScreenSharing(newV);
            });

            meetingControlsController.setLeaveMeetingCallback(this::handleLeaveMeeting);
        }

        // videoGridController sẽ được inject tự động — KHÔNG load tay!!!
    }


    // ============================================================
    // PUBLIC API: được App gọi sau khi load FXML
    // ============================================================
    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
        meetingTitleLabel.setText("Cuộc họp " + meetingId);
        initMockDataIfReady();
    }

    public void setUser(String id, String name, String avatar) {
        this.currentUserId = id;
        this.currentUserName = name;
        this.currentUserAvatar = avatar;
        initMockDataIfReady();
    }

    public void setOnLeaveMeeting(Runnable callback) {
        this.leaveMeetingCallback = callback;
    }


    // ============================================================
    // MOCK DATA — giống React MeetingRoom
    // ============================================================
    private void initMockDataIfReady() {
        if (mockInitialized) return;
        if (currentUserId == null || meetingId == null) return;

        participants.clear();

        participants.add(new Participant(currentUserId, currentUserName,
                currentUserAvatar, true, true, false, false));

        participants.add(new Participant("2", "Trần Thị B",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=user2",
                true, true, false, false));

        participants.add(new Participant("3", "Lê Văn C",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=user3",
                false, true, false, false));

        participants.add(new Participant("4", "Phạm Thị D",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=user4",
                true, false, false, false));

        // Messages
        messages.clear();
        messages.add(new ChatMessage("1", "2", "Trần Thị B",
                "Chào mọi người!", LocalDateTime.now().minusMinutes(5),
                ChatMessage.Type.TEXT, null));

        messages.add(new ChatMessage("2", currentUserId, currentUserName,
                "Xin chào!", LocalDateTime.now().minusMinutes(4),
                ChatMessage.Type.TEXT, null));

        mockInitialized = true;

        refreshParticipantsUI();
    }


    // ============================================================
    // HEADER BUTTONS
    // ============================================================
    @FXML
    private void onToggleChat() {
        showChat = !showChat;
        showParticipants = false;
        showFileSharing = false;
        updateSidePanel();
    }

    @FXML
    private void onToggleParticipants() {
        showParticipants = !showParticipants;
        showChat = false;
        showFileSharing = false;
        updateSidePanel();
    }

    @FXML
    private void onToggleFileSharing() {
        showFileSharing = !showFileSharing;
        showChat = false;
        showParticipants = false;
        updateSidePanel();
    }

    @FXML
    private void onLeaveButtonClicked() {
        handleLeaveMeeting();
    }


    private void handleLeaveMeeting() {
        if (leaveMeetingCallback != null) leaveMeetingCallback.run();
    }


    // ============================================================
    // SHOW / HIDE SIDE PANEL
    // ============================================================
    private void updateSidePanel() {
        boolean any = showChat || showParticipants || showFileSharing;

        sidePanel.setVisible(any);
        sidePanel.setManaged(any);

        chatPanelRoot.setVisible(showChat);
        chatPanelRoot.setManaged(showChat);

        participantsPanelRoot.setVisible(showParticipants);
        participantsPanelRoot.setManaged(showParticipants);

        fileSharingPanelRoot.setVisible(showFileSharing);
        fileSharingPanelRoot.setManaged(showFileSharing);
    }


    // ============================================================
    // UPDATE AUDIO / VIDEO / SHARE – giống React logic
    // ============================================================
    private void updateCurrentUserAudio(boolean isOn) {
        participants.forEach(p -> {
            if (p.getId().equals(currentUserId)) p.setAudioOn(isOn);
        });
        refreshParticipantsUI();
    }

    private void updateCurrentUserVideo(boolean isOn) {
        participants.forEach(p -> {
            if (p.getId().equals(currentUserId)) p.setVideoOn(isOn);
        });
        refreshParticipantsUI();
    }

    private void updateCurrentUserScreenSharing(boolean isOn) {
        if (isOn) participants.forEach(p -> p.setScreenSharing(false));
        participants.forEach(p -> {
            if (p.getId().equals(currentUserId)) p.setScreenSharing(isOn);
        });
        refreshParticipantsUI();
    }


    // ============================================================
    // SYNC UI
    // ============================================================
    private void refreshParticipantsUI() {

        if (videoGridController != null) {
            videoGridController.setCurrentUserId(currentUserId);
            videoGridController.setParticipants(new ArrayList<>(participants));
        }

        participantsCountLabel.setText(participants.size() + " người tham gia");
    }
}
