package com.bookstore.controller;

import com.bookstore.dao.KhachHangDAO;
import com.bookstore.model.GioHang;
import com.bookstore.model.KhachHang;
import com.bookstore.util.AlertUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.text.DecimalFormat;

public class CuaSoThanhToanController {
    @FXML private Label totalLabel;
    @FXML private TextField customerPhoneField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private Button payButton;
    @FXML private VBox newCustomerBox;
    @FXML private TextField customerNameField;
    @FXML private TextField customerAddressField;
    @FXML private Label customerStatusLabel;
    @FXML private Label promoLabel;        
    @FXML private Label finalTotalLabel;

    private GioHang cart;
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private boolean paid = false;
    private KhachHang customerToUse;
    private DecimalFormat df = new DecimalFormat("#,##0 VND");
    private Double passedOriginalTotal = null;
    private Double passedDiscountTotal = null;
    private Double passedFinalTotal = null;

    @FXML
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList(
            "Tiền mặt", "Thẻ ngân hàng", "Momo", "VNPay"
        ));
        paymentMethodCombo.setValue("Tiền mặt");

        customerPhoneField.setOnAction(e -> findCustomerByPhone());
        customerPhoneField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                findCustomerByPhone();
            }
        });
    }

    private void findCustomerByPhone() {
        String phone = customerPhoneField.getText();
        if (phone == null || phone.isEmpty()) {
            customerToUse = null;
            showNewCustomerFields(false);
            customerStatusLabel.setText(null);
            customerStatusLabel.setVisible(false);
            return;
        }

        try {
            KhachHang foundCustomer = khachHangDAO.findByPhone(phone);
            if (foundCustomer != null) {
                customerToUse = foundCustomer;
                showNewCustomerFields(false);
                customerStatusLabel.setText("Khách hàng: " + foundCustomer.getHoTen());
                customerStatusLabel.setVisible(true);
            } else {
                customerToUse = null;
                showNewCustomerFields(true);
                customerStatusLabel.setVisible(false);
                Platform.runLater(() -> customerNameField.requestFocus());
            }
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tìm kiếm SĐT: " + e.getMessage());
        }
    }

    private void showNewCustomerFields(boolean show) {
        newCustomerBox.setVisible(show);
        newCustomerBox.setManaged(show);
    }

    // được gọi bởi BanHangController
    public void setCart(GioHang cart) {
        this.cart = cart;
        updateUI();
    }

    // BanHangController sẽ gọi method này để truyền các tổng đã tính sẵn
    public void setTotals(double originalTotal, double discountTotal, double finalTotal) {
        this.passedOriginalTotal = originalTotal;
        this.passedDiscountTotal = discountTotal;
        this.passedFinalTotal = finalTotal;
        updateUI();
    }

    private void updateUI() {
        if (cart == null && passedOriginalTotal == null) return;

        double originalTotal = passedOriginalTotal != null ? passedOriginalTotal : cart.getOriginalTotal();
        double discountTotal = passedDiscountTotal != null ? passedDiscountTotal : cart.getDiscountTotal();
        double finalTotal    = passedFinalTotal != null ? passedFinalTotal : cart.getFinalTotal();

        totalLabel.setText(df.format(originalTotal));
        promoLabel.setText(
            discountTotal > 0 ? "-" + df.format(discountTotal) : df.format(0)
        );
        finalTotalLabel.setText(df.format(finalTotal));
    }

    @FXML
    private void handlePay() { // Xử lý thanh toán
        if (customerToUse != null) {
            paid = true;
            closeWindow();
            return;
        }

        if (customerPhoneField.getText().isEmpty()) {
            customerToUse = null;
            paid = true;
            closeWindow();
            return;
        }

        if (newCustomerBox.isVisible()) {
            if (customerNameField.getText().isEmpty()) {
                AlertUtil.showWarning("Vui lòng nhập Tên Khách hàng mới!");
                return;
            }
            customerToUse = new KhachHang();
            customerToUse.setHoTen(customerNameField.getText());
            customerToUse.setDienThoai(customerPhoneField.getText());
            customerToUse.setDiaChi(customerAddressField.getText());
            customerToUse.setGhiChu("Khách hàng mới từ POS");
            paid = true;
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) payButton.getScene().getWindow();
        stage.close();
    }

    public boolean isPaid() { return paid; }
    public KhachHang getCustomerToUse() { return customerToUse; }
}
