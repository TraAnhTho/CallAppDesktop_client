package com.example.dacs4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;

public class UiController {

    // Nhóm 1: Các thành phần cơ bản
    @FXML private Button loginButton;
    @FXML private TextField inputField;
    @FXML private Label labelText;
    @FXML private TextArea textArea;
    @FXML private ToggleButton toggleButton;

    // Nhóm 2: Các thành phần phức tạp
    @FXML private TableView<?> tableView;
    @FXML private TabPane tabPane;

    // Nhóm 3: Các thành phần phức tạp
    @FXML private Accordion accordion;
    @FXML private ProgressBar progressBar;
    @FXML private MenuButton menuButton;

    // Hàm xử lý sự kiện Login
    @FXML
    private void handleLogin() {
        System.out.println("Login button clicked!");
    }

    // Hàm xử lý sự kiện Toggle
    @FXML
    private void handleToggle() {
        System.out.println("Toggle button clicked: " + toggleButton.isSelected());
    }

    // Hàm xử lý MenuOption
    @FXML
    private void handleOption1() {
        System.out.println("Option 1 selected");
    }

    @FXML
    private void handleOption2() {
        System.out.println("Option 2 selected");
    }

    // Hàm khởi tạo Table (ví dụ dữ liệu bảng)
    @FXML
    private void initialize() {
        // Setup table data, add some columns, etc.
    }
}
