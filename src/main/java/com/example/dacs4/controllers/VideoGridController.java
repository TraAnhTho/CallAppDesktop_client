package com.example.dacs4.controllers;

import com.example.dacs4.models.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class VideoGridController {

    @FXML
    private GridPane videoGrid;

    private final List<Participant> participants = new ArrayList<>();
    private String currentUserId;

    public void initialize() {
//        loadMockParticipants();   // test: tự render
    }

//    private void loadMockParticipants() {
//        List<Participant> list = new ArrayList<>();
//        list.add(new Participant("1", "A", true, true, false));
//        list.add(new Participant("2", "B", true, false, false));
//        list.add(new Participant("3", "C", false, false, false));
//
//        // ❌ KHÔNG dùng videoGridController.set...
//        // ✔ Gọi chính controller hiện tại
//        setCurrentUserId("1");
//        setParticipants(list);
//    }

    // SET ====

    public void setParticipants(List<Participant> list) {
        participants.clear();
        participants.addAll(list);
        renderGrid();
    }

    public void setCurrentUserId(String id) {
        this.currentUserId = id;
    }

    // RENDER ====

    private void renderGrid() {
        videoGrid.getChildren().clear();
        videoGrid.getColumnConstraints().clear();

        int count = participants.size();
        int cols = calcColumns(count);

        for (int i = 0; i < cols; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / cols);
            videoGrid.getColumnConstraints().add(cc);
        }

        int row = 0, col = 0;

        for (Participant p : participants) {
            StackPane tile = createTile(p);

            videoGrid.add(tile, col, row);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }
    }

    private int calcColumns(int n) {
        if (n <= 1) return 1;
        if (n <= 2) return 2;
        if (n <= 4) return 2;
        if (n <= 6) return 3;
        return 4;
    }

    private StackPane createTile(Participant p) {
        StackPane tile = new StackPane();
        tile.getStyleClass().add("video-tile");

        if (p.isVideoOn()) {
            VBox ph = new VBox(8);
            ph.setAlignment(javafx.geometry.Pos.CENTER);
            ph.getStyleClass().add("video-placeholder");

            Label icon = new Label("📹");
            icon.getStyleClass().add("video-placeholder-icon");

            Label text = new Label("Camera đang bật");
            text.getStyleClass().add("video-placeholder-text");

            ph.getChildren().addAll(icon, text);
            tile.getChildren().add(ph);
        } else {
            Label avatar = new Label(p.getName().substring(0, 1).toUpperCase());
            avatar.getStyleClass().add("avatar-circle");
            tile.getChildren().add(avatar);
        }

        HBox bottom = new HBox(10);
        bottom.getStyleClass().add("bottom-info");

        Label name = new Label(
                p.getName() + (p.getId().equals(currentUserId) ? " (Bạn)" : "")
        );
        name.getStyleClass().add("info-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label mic = new Label(p.isAudioOn() ? "🎤" : "🔇");
        mic.getStyleClass().add("status-icon");
        if (!p.isAudioOn()) mic.getStyleClass().add("red");

        Label cam = new Label(p.isVideoOn() ? "" : "📵");
        cam.getStyleClass().add("status-icon");
        if (!p.isVideoOn()) cam.getStyleClass().add("red");

        bottom.getChildren().addAll(name, spacer, mic, cam);

        BorderPane wrapper = new BorderPane();
        wrapper.setBottom(bottom);

        tile.getChildren().add(wrapper);

        return tile;
    }
}
