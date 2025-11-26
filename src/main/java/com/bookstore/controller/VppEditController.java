package com.bookstore.controller;

import com.bookstore.dao.VanPhongPhamDAO;
import com.bookstore.model.VanPhongPham;
import com.bookstore.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class VppEditController {

    @FXML private Text titleText;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField imageField;
    @FXML private ComboBox<String> supplierComboBox;
    @FXML private TextArea notesArea;
    @FXML private Button saveButton;

    private VanPhongPhamDAO vppDAO = new VanPhongPhamDAO();
    private VanPhongPham currentVpp;
    private boolean dataChanged = false;

    @FXML
    private void initialize() {
        // Tải các NCC (giả) giống như NewProductController
        supplierComboBox.setItems(FXCollections.observableArrayList(
            "Thiên Long", "Campus", "Hồng Hà", "Deli", "Pentel"
        ));
    }

    public void setVpp(VanPhongPham vpp) {
        this.currentVpp = vpp;
        populateData();
    }
    
    private void populateData() {
        titleText.setText("Sửa: " + currentVpp.getTenVPP());
        nameField.setText(currentVpp.getTenVPP());
        priceField.setText(String.valueOf(currentVpp.getDonGia()));
        stockField.setText(String.valueOf(currentVpp.getSoLuong()));
        imageField.setText(currentVpp.getAnh());
        notesArea.setText(currentVpp.getGhiChu());
        supplierComboBox.setValue(currentVpp.getNhaCungCap()); // Đặt NCC
    }

    @FXML
    private void handleSave() {
        try {
            // Cập nhật dữ liệu vào đối tượng
            currentVpp.setTenVPP(nameField.getText());
            currentVpp.setDonGia(Double.parseDouble(priceField.getText()));
            currentVpp.setAnh(imageField.getText());
            currentVpp.setNhaCungCap(supplierComboBox.getValue()); // Lấy NCC từ ComboBox
            currentVpp.setGhiChu(notesArea.getText());
            
            // Gọi DAO để update
            vppDAO.update(currentVpp);
            
            dataChanged = true;
            AlertUtil.showInfo("Cập nhật VPP thành công!");
            handleCancel(); // Đóng cửa sổ

        } catch (NumberFormatException e) {
            AlertUtil.showError("Giá phải là SỐ.");
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi CSDL khi cập nhật VPP: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    
    public boolean isDataChanged() {
        return dataChanged;
    }
}