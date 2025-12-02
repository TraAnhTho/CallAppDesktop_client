package com.example.dacs4.controllers;

import com.example.dacs4.models.ChatMessage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class FileSharingController {

    @FXML
    private BorderPane root;

    @FXML
    private Button uploadButton;

    @FXML
    private ListView<ChatMessage> filesListView;

    private ObservableList<ChatMessage> fileMessages;
    private Consumer<File> onFileUpload;

    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private void initialize() {
        // Placeholder khi chưa có file
        Label placeholder = new Label("Chưa có tệp tin nào được chia sẻ");
        placeholder.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12;");
        filesListView.setPlaceholder(placeholder);

        // Custom cell hiển thị giống UI React
        filesListView.setCellFactory(list -> new FileMessageCell());
    }

    // ========== API cho MeetingRoomController ==========

    public Parent getRoot() {
        return root;
    }

    public void setOnFileUpload(Consumer<File> onFileUpload) {
        this.onFileUpload = onFileUpload;
    }

    public void setMessages(ObservableList<ChatMessage> fileMessages) {
        this.fileMessages = fileMessages;
        filesListView.setItems(fileMessages);
    }

    public void addFileMessage(ChatMessage msg) {
        if (fileMessages != null) {
            fileMessages.add(msg);
        }
    }

    // ========== Xử lý chọn file ==========

    @FXML
    private void handleUploadClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn tệp để tải lên");
        File file = chooser.showOpenDialog(root.getScene().getWindow());
        if (file != null && onFileUpload != null) {
            onFileUpload.accept(file);
        }
    }

    // ========== Cell hiển thị từng file ==========

    private class FileMessageCell extends ListCell<ChatMessage> {
        private final VBox rootBox = new VBox(4);
        private final HBox contentBox = new HBox(8);
        private final Label iconLabel = new Label();
        private final VBox infoBox = new VBox(2);
        private final Label fileNameLabel = new Label();
        private final Label metaLabel = new Label();
        private final Button downloadButton = new Button();

        FileMessageCell() {
            super();

            iconLabel.setStyle("-fx-font-size: 20;");

            fileNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13;");
            fileNameLabel.setMaxWidth(Double.MAX_VALUE);

            metaLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 10;");

            infoBox.getChildren().addAll(fileNameLabel, metaLabel);
            HBox.setHgrow(infoBox, Priority.ALWAYS);

            downloadButton.setText("⬇");
            downloadButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            downloadButton.setOnAction(e -> {
                // TODO: xử lý download file thực sự (sau này dùng socket/P2P)
                System.out.println("Download clicked for " + fileNameLabel.getText());
            });

            contentBox.getChildren().addAll(iconLabel, infoBox, downloadButton);
            rootBox.getChildren().add(contentBox);
            rootBox.setStyle("-fx-background-color: rgba(55,65,81,0.5); -fx-padding: 8; -fx-background-radius: 8;");
        }

        @Override
        protected void updateItem(ChatMessage msg, boolean empty) {
            super.updateItem(msg, empty);

            if (empty || msg == null) {
                setGraphic(null);
                return;
            }

            String fileName = msg.getFileName() != null ? msg.getFileName() : msg.getText();
            fileNameLabel.setText(fileName);

            String icon = getFileIcon(fileName);
            iconLabel.setText(icon);

            String time = msg.getTimestamp() != null
                    ? msg.getTimestamp().format(timeFormatter)
                    : "";
            metaLabel.setText(msg.getSenderName() + " • " + time);

            setGraphic(rootBox);
        }
    }

    // ========== Helpers giống React ==========

    private String getFileIcon(String fileName) {
        String ext = "";
        int idx = fileName.lastIndexOf('.');
        if (idx != -1 && idx < fileName.length() - 1) {
            ext = fileName.substring(idx + 1).toLowerCase();
        }

        if (ext.matches("jpg|jpeg|png|gif|svg")) return "🖼️";
        if (ext.equals("pdf")) return "📄";
        if (ext.matches("doc|docx")) return "📝";
        if (ext.matches("xls|xlsx")) return "📊";
        if (ext.matches("zip|rar")) return "🗜️";
        return "📎";
    }
}
