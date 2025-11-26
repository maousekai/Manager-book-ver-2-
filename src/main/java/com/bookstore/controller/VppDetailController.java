package com.bookstore.controller;

import com.bookstore.model.VanPhongPham;
import com.bookstore.model.Cart;
import com.bookstore.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.text.DecimalFormat;

public class VppDetailController {
    @FXML private ImageView vppImageView;
    @FXML private Text titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label supplierLabel;
    @FXML private Label stockLabel;
    @FXML private TextArea descriptionArea;

    private VanPhongPham vpp;
    private Cart cart;
    private DecimalFormat df = new DecimalFormat("#,##0 VND");

    //Phương thức này được SaleController gọi để truyền dữ liệu vào
     
    public void setVppAndCart(VanPhongPham vpp, Cart cart) {
        this.vpp = vpp;
        this.cart = cart;
        loadVppDetails();
    }

    private void loadVppDetails() {
        if (vpp == null) return;

        titleLabel.setText(vpp.getTenVPP());
        priceLabel.setText(df.format(vpp.getDonGia()));
        supplierLabel.setText(vpp.getNhaCungCap());
        stockLabel.setText(String.valueOf(vpp.getSoLuong()));
        descriptionArea.setText(vpp.getGhiChu());

        // Tải ảnh
        try {
            String imagePath = "/com/bookstore/view/images/books/" + vpp.getAnh(); // Sửa nếu bạn đổi thư mục
            Image img = new Image(getClass().getResource(imagePath).toExternalForm());
            vppImageView.setImage(img);
        } catch (Exception e) {

        }
    }

    @FXML
    private void handleAddToCart() {
        if (cart != null && vpp != null) {
            cart.addVPP(vpp, 1);
            AlertUtil.showInfo("Đã thêm \"" + vpp.getTenVPP() + "\" vào giỏ hàng!");
            handleClose(); // Đóng cửa sổ sau khi thêm
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}