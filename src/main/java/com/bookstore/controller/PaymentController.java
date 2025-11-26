package com.bookstore.controller;

import com.bookstore.dao.KhachHangDAO;
import com.bookstore.model.Cart;
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

public class PaymentController {
    @FXML private Label totalLabel;
    @FXML private Label discountLabel;
    @FXML private TextField customerPhoneField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private Button payButton;
    @FXML private VBox newCustomerBox;
    @FXML private TextField customerNameField;
    @FXML private TextField customerAddressField;
    @FXML private Label customerStatusLabel;
    
    private Cart cart;
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private boolean paid = false;
    private KhachHang customerToUse; // Khách hàng (mới hoặc cũ) sẽ được dùng
    private DecimalFormat df = new DecimalFormat("#,##0 VND");

    @FXML
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList(
            "Tiền mặt", "Thẻ ngân hàng", "Momo", "VNPay"
        ));
        paymentMethodCombo.setValue("Tiền mặt");

        customerPhoneField.setOnAction(e -> findCustomerByPhone());
        // Thêm listener khi focus
        customerPhoneField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { 
                findCustomerByPhone();
            }
        });
    }
    private void findCustomerByPhone() {
        String phone = customerPhoneField.getText();
        if (phone.isEmpty()) {
            // Nếu SĐT rỗng, coi như khách vãng lai
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
                showNewCustomerFields(false); // Ẩn form KH mới
                customerStatusLabel.setText("Khách hàng: " + foundCustomer.getHoTen());
                customerStatusLabel.setVisible(true);
            } else {//không tìm thấy KH
                customerToUse = null; // Sẽ là khách hàng mới
                showNewCustomerFields(true); // Hiện form KH mới
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

    //Được gọi bởi SaleController để truyền giỏ hàng vào
     
    public void setCart(Cart cart) {
        this.cart = cart;
        updateUI();
    }

    private void updateUI() {
        totalLabel.setText(df.format(cart.getTotal()));
        if (cart.getBookCount() >= 10) {
            discountLabel.setText("Đã áp dụng KM (mua 10+ sách)");
        } else {
            discountLabel.setText("Không áp dụng khuyến mãi");
        }
    }

    @FXML
    private void handlePay() {
        // Nếu đã tìm thấy khách hàng (customerToUse != null)
        if (customerToUse != null) {
            paid = true;
            closeWindow();
            return;
        }

        // Nếu không tìm thấy SĐT (SĐT rỗng), coi như khách vãng lai (MaKH = 1)
        if (customerPhoneField.getText().isEmpty()) {
            customerToUse = null; // SaleController sẽ hiểu là MaKH = 1
            paid = true;
            closeWindow();
            return;
        }

        // Nếu SĐT mới và đang tạo khách hàng mới
        if (newCustomerBox.isVisible()) {
            if (customerNameField.getText().isEmpty()) {
                AlertUtil.showWarning("Vui lòng nhập Tên Khách hàng mới!");
                return;
            }
            // Tạo đối tượng KhachHang mới
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