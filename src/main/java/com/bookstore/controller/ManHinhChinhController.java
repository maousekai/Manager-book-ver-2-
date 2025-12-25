package com.bookstore.controller;

import com.bookstore.util.AuthManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ManHinhChinhController {

    // Khai báo các nút mới
    @FXML private Button salesButton;
    @FXML private Button productsButton;
    @FXML private Button invoicesButton;
    @FXML private Button reportsButton;
    @FXML private Button employeesButton;
    @FXML private Button customersButton;
    @FXML private Button importInvoiceButton;
    @FXML private Button attendanceButton;

    private TrangChuController dashboardController;

    //Được gọi bởi TrangChuController để truyền tham chiếu
    public void setDashboardController(TrangChuController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void initialize() {
        // Gán sự kiện click cho các nút
        if (salesButton != null) {
            salesButton.setOnAction(e -> dashboardController.loadView("Bán hàng"));
        }
        if (productsButton != null) {
            productsButton.setOnAction(e -> dashboardController.loadView("Quản lý Sản phẩm"));
        }
        if (invoicesButton != null) {
            invoicesButton.setOnAction(e -> dashboardController.loadView("Quản lý Hoá đơn"));
        }
        if (reportsButton != null) {
            reportsButton.setOnAction(e -> dashboardController.loadView("Báo cáo"));
        }
        if (employeesButton != null) {
            employeesButton.setOnAction(e -> dashboardController.loadView("Quản lý Nhân viên"));
        }
        if (customersButton != null) {
            customersButton.setOnAction(e -> dashboardController.loadView("Quản lý Khách hàng"));
        }

        if (importInvoiceButton != null) {
            importInvoiceButton.setOnAction(e -> dashboardController.loadView("Hoá đơn Nhập kho"));
        }
        if (attendanceButton != null) {
            attendanceButton.setOnAction(e -> dashboardController.loadView("Chấm công"));
        }
        applyRoleVisibility();
    }
    private void applyRoleVisibility() {
    	int userRole = AuthManager.getCurrentRole();
        
        if (userRole == 2) {
            hideButton(employeesButton);
            hideButton(invoicesButton);      
            hideButton(importInvoiceButton); 
            hideButton(reportsButton);    
        }
    }
    private void hideButton(Button button) {
        if (button != null) {
            button.setVisible(false);
            button.setManaged(false); 
        }
    }
}