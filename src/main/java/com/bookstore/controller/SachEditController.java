package com.bookstore.controller;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.bookstore.dao.NhaXuatBanDAO;
import com.bookstore.dao.SachDAO;
import com.bookstore.model.NhaXuatBan;
import com.bookstore.model.Sach;
import com.bookstore.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SachEditController {

    @FXML private Text titleText;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField imageField;
    @FXML private VBox bookFieldsBox;
    @FXML private ComboBox<NhaXuatBan> publisherComboBox;
    @FXML private TextField yearField;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;
    @FXML private Button chooseImageButton;
    @FXML private TextField promotionField;

    private SachDAO sachDAO = new SachDAO();
    private NhaXuatBanDAO nxbDAO = new NhaXuatBanDAO();
    private Sach currentBook;
    private boolean dataChanged = false;

    @FXML
    private void initialize() {
        loadPublishers();
    }

   // Được gọi bởi SanPhamController để truyền Sách vào
    
    public void setBook(Sach sach) {
        this.currentBook = sach;
        populateData();
    }

    private void loadPublishers() {
        try {
            publisherComboBox.setItems(FXCollections.observableArrayList(nxbDAO.getAll()));
            publisherComboBox.setConverter(new javafx.util.StringConverter<NhaXuatBan>() {
                @Override public String toString(NhaXuatBan nxb) { return (nxb == null) ? "" : nxb.getTenNXB(); }
                @Override public NhaXuatBan fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Nhà xuất bản: " + e.getMessage());
        }
    }
    
    private void populateData() {
        titleText.setText("Sửa: " + currentBook.getTenSach());
        nameField.setText(currentBook.getTenSach());
        priceField.setText(String.valueOf(currentBook.getDonGia()));
        stockField.setText(String.valueOf(currentBook.getSoLuong()));
        imageField.setText(currentBook.getAnh());
        yearField.setText(String.valueOf(currentBook.getNamXuatBan()));
        descriptionArea.setText(currentBook.getMoTa());
        promotionField.setText(currentBook.getKhuyenMai());
        
        // Chọn NXB trong ComboBox
        for (NhaXuatBan nxb : publisherComboBox.getItems()) {
            if (nxb.getMaNXB() == currentBook.getMaNXB()) {
                publisherComboBox.setValue(nxb);
                break;
            }
        }
    }
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        // Lấy Stage hiện tại để hiển thị dialog
        Stage stage = (Stage) chooseImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Xác định thư mục đích trong source code
                Path destDir = Paths.get("src/main/resources/com/bookstore/view/images/books");
                
                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                
                // Đường dẫn đầy đủ của tệp đích
                Path destPath = destDir.resolve(selectedFile.getName());
                
                // Sao chép tệp
                Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Cập nhật TextField (và đối tượng nếu cần)
                imageField.setText(selectedFile.getName());
                
                AlertUtil.showInfo("Đã sao chép ảnh '" + selectedFile.getName() + "' vào dự án.");

            } catch (IOException e) {
                e.printStackTrace();
                AlertUtil.showError("Lỗi khi sao chép ảnh: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleSave() {
        try {
            // Cập nhật dữ liệu vào đối tượng 'currentBook'
            currentBook.setTenSach(nameField.getText());
            currentBook.setDonGia(Double.parseDouble(priceField.getText()));
            
            // Dòng này bây giờ sẽ lấy tên tệp từ ô imageField (đã được cập nhật bởi handleChooseImage)
            currentBook.setAnh(imageField.getText()); 
            
            currentBook.setNamXuatBan(Integer.parseInt(yearField.getText()));
            currentBook.setMoTa(descriptionArea.getText());
            currentBook.setMaNXB(publisherComboBox.getValue().getMaNXB());
            currentBook.setKhuyenMai(promotionField.getText());
            // Gọi DAO để update
            sachDAO.update(currentBook);
            
            dataChanged = true;
            AlertUtil.showInfo("Cập nhật sách thành công!");
            handleCancel(); // Đóng cửa sổ

        } catch (NumberFormatException e) {
            AlertUtil.showError("Giá và Năm XB phải là SỐ.");
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi CSDL khi cập nhật Sách: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Dùng titleText thay vì saveButton để lấy Scene
        Stage stage = (Stage) titleText.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleClearPromotion() {
        promotionField.clear();
    }
    
    public boolean isDataChanged() {
        return dataChanged;
    }
}