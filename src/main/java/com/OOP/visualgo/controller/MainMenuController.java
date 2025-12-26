package com.oop.visualgo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainMenuController {

    @FXML
    public void initialize() {
        System.out.println("Main Menu initialized");
    }

    // ==================== Tree Selection ====================

    @FXML
    private void onGenericTreeClick(ActionEvent event) {
        System.out.println("Generic Tree selected");
        navigateToVisualization(event, "Generic Tree");
    }

    @FXML
    private void onBinaryTreeClick(ActionEvent event) {
        System.out.println("Binary Tree selected");
        navigateToVisualization(event, "Binary Tree");
    }

    @FXML
    private void onBSTClick(ActionEvent event) {
        System.out.println("BST selected");
        navigateToVisualization(event, "BST");
    }

    @FXML
    private void onAVLClick(ActionEvent event) {
        System.out.println("AVL Tree selected");
        navigateToVisualization(event, "AVL Tree");
    }

    @FXML
    private void onRBTreeClick(ActionEvent event) {
        System.out.println("Red-Black Tree selected");
        navigateToVisualization(event, "Red-Black Tree");
    }

    @FXML
    private void onBTreeClick(ActionEvent event) {
        System.out.println("B-Tree selected");
        navigateToVisualization(event, "B-Tree");
    }

    // ==================== Navigation ====================

    private void navigateToVisualization(ActionEvent event, String treeType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop/visualgo/visualization-view.fxml"));
            Parent root = loader.load();

            // Get controller and set tree type
            VisualizationViewController controller = loader.getController();
            if (controller != null) {
                controller.setTreeType(treeType);
            }

            // Get stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Lưu trạng thái fullscreen/maximized hiện tại
            boolean wasFullScreen = stage.isFullScreen();
            boolean wasMaximized = stage.isMaximized();

            // Tạo scene mới với kích thước hiện tại của stage
            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(newScene);
            stage.setTitle("VisualGo - " + treeType);

            // Khôi phục trạng thái fullscreen/maximized
            if (wasFullScreen) {
                stage.setFullScreen(true);
            } else if (wasMaximized) {
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Could not load visualization view.\nReason: " + e.getMessage());
        }
    }

    // ==================== Help & Quit ====================

    @FXML
    private void onHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("VisualGo - Tree Visualization");
        alert.setContentText(
            "Welcome to VisualGo!\n\n" +
            "This application helps you visualize tree data structures and algorithms.\n\n" +
            "Features:\n" +
            "• Generic Tree\n" +
            "• Binary Tree\n" +
            "• Binary Search Tree (BST)\n" +
            "• Red-Black Tree\n\n" +
            "Select a tree type to start visualizing operations like insert, delete, search, and traversal."
        );
        alert.showAndWait();
    }

    @FXML
    private void onQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit");
        alert.setHeaderText("Exit Application");
        alert.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    // ==================== Helper Methods ====================

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

