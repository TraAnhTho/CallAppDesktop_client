package com.example.dacs4.controllers;

import com.example.dacs4.models.Participant;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class ParticipantsListController {

    @FXML
    private BorderPane root;

    @FXML
    private Label titleLabel;

    @FXML
    private ListView<Participant> participantsListView;

    private ObservableList<Participant> participants;
    private String currentUserId;

    // callback từ MeetingRoomController
    private Consumer<String> onToggleAudio;
    private Consumer<String> onToggleVideo;
    private Consumer<String> onRemoveParticipant;

    @FXML
    private void initialize() {
        // custom cell giống React
        participantsListView.setCellFactory(listView -> new ParticipantCell());
    }

    // ==== API cho MeetingRoomController gọi ====

    public Parent getRoot() {
        return root;
    }

    public void setParticipants(ObservableList<Participant> participants, String currentUserId) {
        this.participants = participants;
        this.currentUserId = currentUserId;
        participantsListView.setItems(participants);
        updateTitle();
    }

    public void setOnToggleAudio(Consumer<String> onToggleAudio) {
        this.onToggleAudio = onToggleAudio;
    }

    public void setOnToggleVideo(Consumer<String> onToggleVideo) {
        this.onToggleVideo = onToggleVideo;
    }

    public void setOnRemoveParticipant(Consumer<String> onRemoveParticipant) {
        this.onRemoveParticipant = onRemoveParticipant;
    }

    public void refresh() {
        updateTitle();
        participantsListView.refresh();
    }

    private void updateTitle() {
        if (participants != null) {
            titleLabel.setText("Người tham gia (" + participants.size() + ")");
        }
    }

    // ==== Cell UI cho từng participant ====

    private class ParticipantCell extends ListCell<Participant> {
        private final HBox root = new HBox(8);
        private final StackPane avatarCircle = new StackPane();
        private final Label avatarText = new Label();
        private final VBox infoBox = new VBox(2);
        private final Label nameLabel = new Label();
        private final Label statusLabel = new Label();

        private final HBox actionsBox = new HBox(4);
        private final Button audioBtn = new Button();
        private final Button videoBtn = new Button();
        private final Button kickBtn = new Button("X");

        ParticipantCell() {
            super();

            avatarCircle.setPrefSize(32, 32);
            avatarCircle.setMaxSize(32, 32);
            avatarCircle.setStyle("-fx-background-color: #4b5563; -fx-background-radius: 9999;");
            avatarText.setTextFill(Color.WHITE);
            avatarText.setStyle("-fx-font-weight: bold;");
            avatarCircle.getChildren().add(avatarText);

            nameLabel.setTextFill(Color.WHITE);
            statusLabel.setTextFill(Color.LIGHTGRAY);
            statusLabel.setStyle("-fx-font-size: 10;");

            infoBox.getChildren().addAll(nameLabel, statusLabel);
            HBox.setHgrow(infoBox, Priority.ALWAYS);

            audioBtn.setMinSize(28, 28);
            audioBtn.setPrefSize(28, 28);
            audioBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

            videoBtn.setMinSize(28, 28);
            videoBtn.setPrefSize(28, 28);
            videoBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

            kickBtn.setMinSize(28, 28);
            kickBtn.setPrefSize(28, 28);
            kickBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #f87171;");

            actionsBox.getChildren().addAll(audioBtn, videoBtn, kickBtn);

            root.setStyle("-fx-background-color: rgba(55,65,81,0.5); -fx-padding: 6 8; -fx-background-radius: 6;");
            root.getChildren().addAll(avatarCircle, infoBox, actionsBox);
        }

        @Override
        protected void updateItem(Participant p, boolean empty) {
            super.updateItem(p, empty);

            if (empty || p == null) {
                setGraphic(null);
                return;
            }

            // host = phần tử đầu tiên trong list
            boolean isHost = getIndex() == 0;
            boolean isCurrentUser = p.getId().equals(currentUserId);

            // avatar = chữ cái đầu
            String firstLetter = p.getName() != null && !p.getName().isEmpty()
                    ? p.getName().substring(0, 1).toUpperCase()
                    : "?";
            avatarText.setText(firstLetter);

            // tên
            StringBuilder name = new StringBuilder(p.getName());
            if (isCurrentUser) name.append(" (Bạn)");
            if (isHost) name.append(" ★");
            nameLabel.setText(name.toString());

            // status mic/cam nhỏ
            String mic = p.isAudioOn() ? "Mic: On" : "Mic: Off";
            String cam = p.isVideoOn() ? "Cam: On" : "Cam: Off";
            statusLabel.setText(mic + " | " + cam);

            // button icon đơn giản (text)
            audioBtn.setText(p.isAudioOn() ? "🔊" : "🔇");
            audioBtn.setOnAction(e -> {
                if (onToggleAudio != null) onToggleAudio.accept(p.getId());
            });

            videoBtn.setText(p.isVideoOn() ? "🎥" : "📷✖");
            videoBtn.setOnAction(e -> {
                if (onToggleVideo != null) onToggleVideo.accept(p.getId());
            });

            // chỉ host mới được kick người khác, và không kick chính mình
            boolean canKick = isHost && !isCurrentUser;
            kickBtn.setVisible(canKick);
            kickBtn.setManaged(canKick);
            kickBtn.setOnAction(e -> {
                if (onRemoveParticipant != null) onRemoveParticipant.accept(p.getId());
            });

            // quyền điều khiển: host hoặc chính mình
            boolean canControl = isHost || isCurrentUser;
            audioBtn.setVisible(canControl);
            audioBtn.setManaged(canControl);
            videoBtn.setVisible(canControl);
            videoBtn.setManaged(canControl);

            setGraphic(root);
        }
    }
}
