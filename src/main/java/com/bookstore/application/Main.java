package com.bookstore.application;

import com.bookstore.util.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            StageManager.setPrimaryStage(primaryStage);

            // Tải FXML Đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/DangNhapView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Đăng nhập hệ thống quản lý nhà sách");
            String iconPath = "/com/bookstore/view/images/iconmain.png";
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}