package com.bookstore.controller;

import com.bookstore.dao.NhaXuatBanDAO;
import com.bookstore.dao.SachDAO;
import com.bookstore.model.Sach;
import com.bookstore.model.GioHang;
import com.bookstore.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import javafx.scene.text.Text;

public class SachController {
    @FXML private ImageView bookImageView;
    @FXML private Text titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label priceLabel;
    @FXML private Label genreLabel;
    @FXML private Label publisherLabel;
    @FXML private Label yearLabel;
    @FXML private Label locationLabel;
    @FXML private Label saleOffLabel;
    @FXML private TextArea descriptionArea;

    private Sach book;
    private GioHang cart;
    private DecimalFormat df = new DecimalFormat("#,##0 VND");
    
    // Các DAO cần thiết
    private SachDAO sachDAO = new SachDAO();
    private NhaXuatBanDAO nxbDAO = new NhaXuatBanDAO();
    // Phương thức này được BanHangController gọi để truyền dữ liệu vào
    public void setBookAndCart(Sach book, GioHang cart) {
        this.book = book;
        this.cart = cart;
        loadBookDetails();
    }

    private void loadBookDetails() {
        if (book == null) return;

        titleLabel.setText(book.getTenSach());
        priceLabel.setText(df.format(book.getDonGia()));
        yearLabel.setText(String.valueOf(book.getNamXuatBan()));
        locationLabel.setText(book.getViTriKe() + " - " + book.getViTriNgan() + " - " + book.getViTriHang());
        descriptionArea.setText(book.getMoTa());
        saleOffLabel.setText(book.getKhuyenMai());

        // Tải ảnh 
        try {
            String imagePath = "/com/bookstore/view/images/books/" + book.getAnh();
            Image img = new Image(getClass().getResource(imagePath).toExternalForm());
            bookImageView.setImage(img);
        } catch (Exception e) {
            AlertUtil.showError("Lỗi load ảnh sách: " + e.getMessage());
        }

        // Tải thông tin từ các bảng khác (Tác giả, NXB, Thể loại)
        try {
            List<String> authors = sachDAO.getAuthorsForBook(book.getMaSach());
            authorLabel.setText("Tác giả: " + String.join(", ", authors));

            List<String> genres = sachDAO.getGenresForBook(book.getMaSach());
            genreLabel.setText(String.join(", ", genres));

            publisherLabel.setText(nxbDAO.getById(book.getMaNXB()).getTenNXB());

        } catch (SQLException e) {
            AlertUtil.showError("Lỗi load chi tiết (Authors/Genres/NXB): " + e.getMessage());
        }
    }

    @FXML
    private void handleAddToCart() { // Thêm sách vào giỏ
        if (cart != null && book != null) {
            cart.addBook(book, 1);
            AlertUtil.showInfo("Đã thêm \"" + book.getTenSach() + "\" vào giỏ hàng!");
            // Đóng cửa sổ sau khi thêm
            handleClose();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}