package com.bookstore.controller;

import com.bookstore.dao.BangChamCongDAO;
import com.bookstore.dao.NhanVienDAO;
import com.bookstore.model.NhanVien;
import com.bookstore.util.AlertUtil;
import com.bookstore.util.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class NhanVienController {

    @FXML private TableView<NhanVien> employeeTable;
    @FXML private TextField searchField;
    @FXML private Button deleteButton;
    @FXML private TextField nameField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button paySalaryButton;

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private BangChamCongDAO chamCongDAO = new BangChamCongDAO();
    private ObservableList<NhanVien> masterEmployeeList = FXCollections.observableArrayList();
    

    @FXML
    private void initialize() {
        // Chỉ khởi tạo nếu employeeTable tồn tại
        if (employeeTable != null) {
            setupTableColumns();
            loadEmployees();
            setupSearchFilter();

            employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    deleteButton.setDisable(newSelection == null || newSelection.getMaNV() == 1);
                }
            );
        }
        if (roleCombo != null) {
             roleCombo.setItems(FXCollections.observableArrayList("STAFF", "MANAGER"));
        }
    }

    private void setupTableColumns() {
        TableColumn<NhanVien, Integer> idCol = new TableColumn<>("Mã NV");
        idCol.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        idCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.05));

        TableColumn<NhanVien, String> nameCol = new TableColumn<>("Họ Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("tenNV"));
        nameCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.20));

        TableColumn<NhanVien, String> phoneCol = new TableColumn<>("Điện thoại");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("dienThoai"));
        phoneCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.15));
        
        TableColumn<NhanVien, String> userCol = new TableColumn<>("Tài khoản");
        userCol.setCellValueFactory(new PropertyValueFactory<>("taiKhoan"));
        userCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.15));

        TableColumn<NhanVien, Integer> roleIdCol = new TableColumn<>("Mã Quyền");
        roleIdCol.setCellValueFactory(new PropertyValueFactory<>("maQuyen"));
        roleIdCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.1));

        TableColumn<NhanVien, String> roleCol = new TableColumn<>("Vị trí");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("viTri"));
        roleCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.15));
        
        TableColumn<NhanVien, Double> salaryCol = new TableColumn<>("Lương tháng");
        salaryCol.setCellValueFactory(cellData -> {
            NhanVien nv = cellData.getValue();
            try {
                int thang = LocalDate.now().getMonthValue();
                int nam = LocalDate.now().getYear();

                double luong = chamCongDAO.tinhLuongThang(
                    nv.getMaNV(),
                    thang,
                    nam
                );

                return new javafx.beans.property.SimpleDoubleProperty(luong).asObject();
            } catch (Exception e) {
                return new javafx.beans.property.SimpleDoubleProperty(0).asObject();
            }
        });
        salaryCol.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.20));
        
        salaryCol.setCellFactory(column -> {
            return new TableCell<NhanVien, Double>() {
                private DecimalFormat df = new DecimalFormat("#,##0 VND");
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(df.format(item));
                        setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                    }
                }
            };
        });

        employeeTable.getColumns().clear();
        employeeTable.getColumns().addAll(idCol, nameCol, phoneCol, userCol, roleIdCol, roleCol, salaryCol);
    }
    @FXML
    private void loadEmployees() { // Tải danh sách nhân viên
        try {
            masterEmployeeList.clear();
            masterEmployeeList.addAll(nhanVienDAO.getAll());
            employeeTable.setItems(masterEmployeeList);
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Nhân viên: " + e.getMessage());
        }
    }

    private void setupSearchFilter() { // Tìm kiếm nhân viên
        FilteredList<NhanVien> filteredData = new FilteredList<>(masterEmployeeList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (employee.getTenNV().toLowerCase().contains(lowerCaseFilter)) return true;
                if (employee.getTaiKhoan().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<NhanVien> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedData);
    }

    @FXML
    private void handleNewEmployee() { // mở cửa sổ thêm
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/ThemNhanVienView.fxml"));
            loader.setController(this); 
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Nhân viên Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            String iconPath = "/com/bookstore/view/images/iconmain.png";
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
            
            stage.showAndWait();
            loadEmployees();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ Thêm Nhân viên: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddEmployee() { //thêm nhân viên mới
        if (nameField.getText().isEmpty() || usernameField.getText().isEmpty() || 
            passwordField.getText().isEmpty() || roleCombo.getValue() == null) {
            AlertUtil.showWarning("Họ Tên, Tài khoản, Mật khẩu và Vị trí là bắt buộc.");
            return;
        }

        try {
            NhanVien nv = new NhanVien();
            nv.setTenNV(nameField.getText());
            nv.setNgaySinh(dobPicker.getValue());
            nv.setDiaChi(addressField.getText());
            nv.setDienThoai(phoneField.getText());
            nv.setTaiKhoan(usernameField.getText());
            nv.setMatKhau(passwordField.getText());
            nv.setNgayVaoLam(LocalDate.now());
            if (roleCombo.getValue().equals("MANAGER")) {
                nv.setMaQuyen(1);
                nv.setViTri("MANAGER");
            } else {
                nv.setMaQuyen(2); // Mặc định là STAFF
                nv.setViTri("STAFF");
            }
            nv.setLuongCoBan(0);

            nhanVienDAO.insert(nv);
            AlertUtil.showInfo("Thêm nhân viên '" + nv.getTenNV() + "' thành công!");
            
            handleCancelAdd(); // Đóng cửa sổ pop-up

        } catch (NumberFormatException e) {
            AlertUtil.showError("Lương cơ bản phải là một con số.");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint")) {
                AlertUtil.showError("Lỗi: Tên tài khoản '" + usernameField.getText() + "' đã tồn tại.");
            } else {
                AlertUtil.showError("Lỗi CSDL khi thêm nhân viên: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancelAdd() { // Đóng cửa sổ thêm nhân viên
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handlePaySalary() {
        NhanVien nv = employeeTable.getSelectionModel().getSelectedItem();
        if (nv == null) {
            AlertUtil.showWarning("Vui lòng chọn nhân viên.");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation(
            "Xác nhận trả lương",
            "Xác nhận đã trả lương cho nhân viên:\n" + nv.getTenNV() +
            "\n\nSau khi xác nhận, giờ làm trong tháng sẽ được reset."
        );

        if (!confirmed) return;

        try {
            int thang = LocalDate.now().getMonthValue();
            int nam = LocalDate.now().getYear();

            chamCongDAO.resetGioLamSauKhiTraLuong(
                nv.getMaNV(), thang, nam
            );

            AlertUtil.showInfo("Đã xác nhận trả lương và reset giờ làm.");

        } catch (SQLException e) {
            AlertUtil.showError("Lỗi trả lương: " + e.getMessage());
        }
    }


    @FXML
    private void handleDeleteEmployee() { // Xử lý xóa nhân viên
        NhanVien selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (selected.getMaNV() == 1) {
            AlertUtil.showWarning("Không thể xóa tài khoản Quản trị viên gốc.");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation(
            "Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa nhân viên: " + selected.getTenNV() + "?"
        );

        if (confirmed) {
            try {
                nhanVienDAO.delete(selected.getMaNV());
                masterEmployeeList.remove(selected);
                AlertUtil.showInfo("Đã xóa nhân viên thành công.");
            } catch (SQLException e) {
                 AlertUtil.showError("Lỗi CSDL khi xóa nhân viên: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleViewSalary() { // Xem bảng lương nhân viên
        NhanVien nv = employeeTable.getSelectionModel().getSelectedItem();
        if (nv == null) {
            AlertUtil.showWarning("Vui lòng chọn nhân viên.");
            return;
        }

        try {
            int thang = LocalDate.now().getMonthValue();
            int nam = LocalDate.now().getYear();

            double tongGio = chamCongDAO.tongGioLamTheoThang(
                nv.getMaNV(),
                thang,
                nam
            );

            double luong = chamCongDAO.tinhLuongThang(
                nv.getMaNV(),
                thang,
                nam
            );

            AlertUtil.showInfo(
                "BẢNG LƯƠNG THÁNG " + thang + "/" + nam + "\n\n" +
                "Nhân viên: " + nv.getTenNV() + "\n" +
                "Tổng giờ làm: " + String.format("%.1f", tongGio) + " giờ\n" +
                "Lương thực nhận: " + String.format("%,.0f VND", luong)
            );

        } catch (Exception e) {
            AlertUtil.showError("Lỗi tính lương: " + e.getMessage());
        }
    }


}