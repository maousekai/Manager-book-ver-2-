package com.bookstore.controller;

import com.bookstore.dao.NhanVienDAO;
import com.bookstore.model.NhanVien;
import com.bookstore.util.AlertUtil;
import com.bookstore.util.AuthManager;
import com.bookstore.util.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.SQLException;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button resetButton;

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    
    // Giữ lại logic chống spam/brute-force từ file cũ
    private int loginAttempts = 0;
    private long lastAttemptTime = 0;

    @FXML
    private void initialize() {
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
        
        Platform.runLater(() -> usernameField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        // Chống spam
        if (System.currentTimeMillis() - lastAttemptTime < 60000 && loginAttempts >= 5) {
            AlertUtil.showError("Bạn đã thử quá nhiều lần. Vui lòng đợi 1 phút.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showWarning("Vui lòng điền đầy đủ Tên đăng nhập và Mật khẩu.");
            return;
        }

        try {
            NhanVien nv = nhanVienDAO.login(username, password);

            if (nv != null) {
                loginAttempts = 0;
                AuthManager.login(String.valueOf(nv.getMaNV()), nv.getMaQuyen());
                StageManager.switchScene(
                    "/com/bookstore/view/DashboardView.fxml", 
                    "Tổng quan - Hệ thống quản lý nhà sách"
                );

            } else {
                AlertUtil.showError("Sai tên đăng nhập hoặc mật khẩu.");
                loginAttempts++;
                lastAttemptTime = System.currentTimeMillis();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi CSDL: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText();
        String newPassword = "123"; 
        if (username.isEmpty()) {
            AlertUtil.showWarning("Nhập tên đăng nhập để reset.");
            return;
        }
        try {
            nhanVienDAO.resetPassword(username, newPassword);
            AlertUtil.showInfo("Mật khẩu cho '" + username + "' đã được reset thành '123'.");
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi reset mật khẩu: " + e.getMessage());
        }
    }
}