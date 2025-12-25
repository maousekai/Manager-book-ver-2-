package com.bookstore.controller;

import com.bookstore.util.AlertUtil;
import com.bookstore.util.AuthManager;
import com.bookstore.util.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import com.bookstore.dao.NhanVienDAO; 
import com.bookstore.model.NhanVien;
import java.sql.SQLException;

public class TrangChuController {

    @FXML private BorderPane contentPane;
    @FXML private ListView<String> navigationList;
    @FXML private Label userLabel;
    @FXML private Button logoutButton;
    
    @FXML private VBox sidebar; 
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    @FXML
    private void initialize() {
    	try {
            int maNV = Integer.parseInt(AuthManager.getCurrentUserId());
            NhanVien nv = nhanVienDAO.getById(maNV);
            if (nv != null) { userLabel.setText("Xin chào, " + nv.getTenNV()); } 
            else { userLabel.setText("Xin chào, " + AuthManager.getCurrentUserId()); }
        } catch (Exception e) { userLabel.setText("Xin chào, Lỗi"); }

        // phân quyền hiển thị menu
        ObservableList<String> navItems;
        int userRole = AuthManager.getCurrentRole(); // Lấy INT

        if (userRole == 1) { // 1 = MANAGER/ADMIN
            navItems = FXCollections.observableArrayList(
                "Trang chủ", "Bán hàng", "Quản lý Sản phẩm", "Quản lý Nhân viên","Chấm công",
                "Quản lý Khách hàng", "Quản lý Hoá đơn", "Hoá đơn Nhập kho", "Báo cáo"
            );
        } else { // 2 = STAFF
            navItems = FXCollections.observableArrayList(
                "Trang chủ", "Bán hàng", "Quản lý Sản phẩm", "Quản lý Khách hàng" , "Chấm Công"
            );
        }
        navigationList.setItems(navItems);

        // Tùy chỉnh ListView
        navigationList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
            }
        });

        //sự kiện click
        navigationList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadView(newValue);
                }
            }
        );

        // load màn hình mặc định
        loadView("Trang chủ");
        navigationList.getSelectionModel().select("Trang chủ");

        sidebar.setOnMouseEntered(e -> {
            animateSidebar(220.0); 
            sidebar.getStyleClass().remove("sidebar-collapsed");
        });
        sidebar.setOnMouseExited(e -> {
            animateSidebar(10.0); 
            sidebar.getStyleClass().add("sidebar-collapsed");
        });
        sidebar.getStyleClass().add("sidebar-collapsed");
    }

    public void loadView(String viewName) {
        String fxmlFile = "";
        try {
            switch (viewName) {
                case "Trang chủ":
                    fxmlFile = "/com/bookstore/view/ManHinhChinhView.fxml";
                    break;
                case "Bán hàng":
                    fxmlFile = "/com/bookstore/view/BanHangView.fxml";
                    break;
                case "Quản lý Sản phẩm":
                    fxmlFile = "/com/bookstore/view/SanPhamView.fxml";
                    break;
                case "Quản lý Hoá đơn":
                    fxmlFile = "/com/bookstore/view/HoaDonView.fxml";
                    break;
                case "Hoá đơn Nhập kho":
                    fxmlFile = "/com/bookstore/view/HoaDonNhapKhoView.fxml";
                    break;
                case "Quản lý Nhân viên":
                    fxmlFile = "/com/bookstore/view/NhanVienView.fxml";
                    break;
                case "Quản lý Khách hàng":
                    fxmlFile = "/com/bookstore/view/KhachHangView.fxml";
                    break;
                case "Báo cáo":
                    fxmlFile = "/com/bookstore/view/BaoCaoView.fxml";
                    break;
                case "Chấm công":
                    fxmlFile = "/com/bookstore/view/BangChamCongView.fxml";
                    break;
                default:
                    fxmlFile = null;
                    break; 
            }

            if (fxmlFile == null) {
                AlertUtil.showInfo("Chức năng '" + viewName + "' không xác định.");
                contentPane.setCenter(null);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();

            if (viewName.equals("Trang chủ")) {
                ManHinhChinhController homeController = loader.getController();
                homeController.setDashboardController(this);
            }

            contentPane.setCenter(view);
            navigationList.getSelectionModel().select(viewName);

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi khi tải: " + fxmlFile + "\n" + e.getMessage());
            contentPane.setCenter(null);
        } catch (Exception e) {
             e.printStackTrace();
             AlertUtil.showError("Lỗi không xác định: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        AuthManager.logout();
        StageManager.switchScene(
            "/com/bookstore/view/DangNhapView.fxml", 
            "Đăng nhập hệ thống"
        );
    }

    private void animateSidebar(double width) { // Thu phóng sidebar
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(sidebar.prefWidthProperty(), width);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv); // Tốc độ 0.2 giây
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}