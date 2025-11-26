package com.bookstore.controller;

import com.bookstore.dao.KhachHangDAO;
import com.bookstore.model.KhachHang;
import com.bookstore.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CustomerEditController {

    @FXML private Text titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextArea notesArea;

    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private KhachHang currentCustomer;
    private boolean dataChanged = false;

    //Được gọi bởi CustomerController để truyền dữ liệu vào
     
    public void setCustomer(KhachHang customer) {
        this.currentCustomer = customer;
        populateData();
    }
    
    private void populateData() {
        titleLabel.setText("Sửa: " + currentCustomer.getHoTen());
        nameField.setText(currentCustomer.getHoTen());
        phoneField.setText(currentCustomer.getDienThoai());
        addressField.setText(currentCustomer.getDiaChi());
        notesArea.setText(currentCustomer.getGhiChu());
    }

    @FXML
    private void handleSave() {
        try {
            // Cập nhật dữ liệu vào đối tượng
            currentCustomer.setDienThoai(phoneField.getText());
            currentCustomer.setDiaChi(addressField.getText());
            currentCustomer.setGhiChu(notesArea.getText());

            // Gọi DAO để update
            khachHangDAO.update(currentCustomer); 
            
            dataChanged = true;
            AlertUtil.showInfo("Cập nhật khách hàng thành công!");
            handleCancel(); // Đóng cửa sổ

        } catch (SQLException e) {
            AlertUtil.showError("Lỗi CSDL khi cập nhật: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
    
    public boolean isDataChanged() {
        return dataChanged;
    }
}