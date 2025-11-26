package com.bookstore.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StageManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(String fxmlFile, String title) {
        if (primaryStage == null) {
            System.err.println("Lỗi: Primary stage chưa được thiết lập!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi load FXML: " + fxmlFile + "\n" + e.getMessage());
        }
    }

}