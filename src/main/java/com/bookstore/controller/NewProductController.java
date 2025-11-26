package com.bookstore.controller;

import com.bookstore.dao.ChiTietNhapKhoDAO;
import com.bookstore.dao.HoaDonNhapKhoDAO;
import com.bookstore.dao.NhaXuatBanDAO;
import com.bookstore.dao.SachDAO;
import com.bookstore.dao.SachTheLoaiDAO;
import com.bookstore.dao.TheLoaiDAO;
import com.bookstore.dao.VanPhongPhamDAO;
import com.bookstore.model.*;
import com.bookstore.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.bookstore.util.AuthManager;
import java.time.LocalDate;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.sql.SQLException;

public class NewProductController {

    // Nút chọn loại
    @FXML private ToggleButton bookToggle;
    @FXML private ToggleButton vppToggle;
    private ToggleGroup typeToggleGroup;
    @FXML private Button chooseImageButton;

    // Trường chung
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField imageField;

    // Trường của Sách
    @FXML private VBox bookFieldsBox;
    @FXML private ComboBox<NhaXuatBan> publisherComboBox;
    @FXML private VBox newPublisherBox;
    @FXML private TextField newPublisherField;
    @FXML private ComboBox<TheLoai> genreComboBox;
    @FXML private VBox newGenreBox;
    @FXML private TextField newGenreField;
    @FXML private TextField yearField;
    @FXML private TextArea descriptionArea;

    // Trường của VPP
    @FXML private VBox vppFieldsBox;
    @FXML private ComboBox<String> supplierComboBox;
    @FXML private VBox newSupplierBox;
    @FXML private TextField newSupplierField;
    @FXML private TextArea notesArea;

    @FXML private Button saveButton;
    @FXML private Label statusLabel;
    private Sach existingSach = null;
    private VanPhongPham existingVpp = null;

    // DAOs
    private SachDAO sachDAO = new SachDAO();
    private VanPhongPhamDAO vppDAO = new VanPhongPhamDAO();
    private NhaXuatBanDAO nxbDAO = new NhaXuatBanDAO();
    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
    private SachTheLoaiDAO sachTheLoaiDAO = new SachTheLoaiDAO();

    // Biến trạng thái
    private boolean isBook = true; // Mặc định là Sách

    private final String ADD_NEW_PUBLISHER_TEXT = "--- Thêm Nhà Xuất Bản Mới ---";
    private final String ADD_NEW_GENRE_TEXT = "--- Thêm Thể Loại Mới ---";
    private final String ADD_NEW_SUPPLIER_TEXT = "--- Thêm Nhà Cung Cấp Mới ---";

    
    private HoaDonNhapKhoDAO hoaDonNhapKhoDAO = new HoaDonNhapKhoDAO();
    private ChiTietNhapKhoDAO chiTietNhapKhoDAO = new ChiTietNhapKhoDAO();
    @FXML
    private void initialize() {
        // Nhóm 2 nút Toggle
        typeToggleGroup = new ToggleGroup();
        bookToggle.setToggleGroup(typeToggleGroup);
        vppToggle.setToggleGroup(typeToggleGroup);

        // Thêm listener để Ẩn/Hiện form
        typeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == bookToggle) {
                isBook = true;
                showBookFields(true);
                showVppFields(false);
            } else if (newToggle == vppToggle) {
                isBook = false;
                showBookFields(false);
                showVppFields(true);
            }
        });

        // Chọn Sách làm mặc định
        bookToggle.setSelected(true);

        // Tải dữ liệu cho các ComboBox
        loadPublishers();
        loadGenres();
        loadSuppliers();
        
        // Thêm listener cho các ComboBox
        setupComboBoxListeners();
        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Mất focus (on blur)
                checkProductExists();
            }
        });
    }
    
    private void loadPublishers() {
        try {
            ObservableList<NhaXuatBan> publishers = FXCollections.observableArrayList(nxbDAO.getAll());
            // Tạo NXB "ảo"
            NhaXuatBan addNewNXB = new NhaXuatBan();
            addNewNXB.setTenNXB(ADD_NEW_PUBLISHER_TEXT);
            publishers.add(addNewNXB);
            
            publisherComboBox.setItems(publishers);
            publisherComboBox.setPromptText("Chọn Nhà Xuất Bản...");     
            // Tùy chỉnh cách hiển thị
            publisherComboBox.setConverter(new javafx.util.StringConverter<NhaXuatBan>() {
                @Override public String toString(NhaXuatBan nxb) { return (nxb == null) ? "" : nxb.getTenNXB(); }
                @Override public NhaXuatBan fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Nhà xuất bản: " + e.getMessage());
        }
    }
    
    private void loadGenres() {
        try {
            ObservableList<TheLoai> genres = FXCollections.observableArrayList(theLoaiDAO.getAll());
            TheLoai addNewGenre = new TheLoai();
            addNewGenre.setTenTL(ADD_NEW_GENRE_TEXT);
            genres.add(addNewGenre);

            genreComboBox.setItems(genres);
            genreComboBox.setPromptText("Chọn Thể loại...");

            genreComboBox.setConverter(new javafx.util.StringConverter<TheLoai>() {
                @Override public String toString(TheLoai tl) { return (tl == null) ? "" : tl.getTenTL(); }
                @Override public TheLoai fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Thể loại: " + e.getMessage());
        }
    }

    private void loadSuppliers() {
        ObservableList<String> suppliers = FXCollections.observableArrayList(
            "Thiên Long", "Campus", "Hồng Hà", "Deli", "Pentel"
        );
        suppliers.add(ADD_NEW_SUPPLIER_TEXT); // Thêm dòng "magic string"
        supplierComboBox.setItems(suppliers);
        supplierComboBox.setPromptText("Chọn nhà cung cấp...");
    }

    private void setupComboBoxListeners() {
        // Listener cho Nhà Xuất Bản
        publisherComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean show = (newVal != null && newVal.getTenNXB().equals(ADD_NEW_PUBLISHER_TEXT));
            newPublisherBox.setVisible(show);
            newPublisherBox.setManaged(show);
        });

        // Listener cho Thể Loại
        genreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean show = (newVal != null && newVal.getTenTL().equals(ADD_NEW_GENRE_TEXT));
            newGenreBox.setVisible(show);
            newGenreBox.setManaged(show);
        });

        // Listener cho Nhà Cung Cấp
        supplierComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean show = (newVal != null && newVal.equals(ADD_NEW_SUPPLIER_TEXT));
            newSupplierBox.setVisible(show);
            newSupplierBox.setManaged(show);
        });
    }

    private void showBookFields(boolean show) {
        bookFieldsBox.setVisible(show);
        bookFieldsBox.setManaged(show);
    }
    
    private void showVppFields(boolean show) {
        vppFieldsBox.setVisible(show);
        vppFieldsBox.setManaged(show);
    }

    @FXML
    private void handleSave() {
        try {
            String stockText = stockField.getText();
            if (stockText.isEmpty()) {
                AlertUtil.showWarning("Vui lòng nhập Số lượng nhập.");
                return;
            }
            int soLuongNhap = Integer.parseInt(stockText);
            String tenSanPham = nameField.getText();
            
            int maSachLuu = 0;
            int maVppLuu = 0;
            String ghiChu = "";
			// Kiểm tra nếu là Sách hay VPP
            if (existingSach != null) {
                sachDAO.addStock(existingSach.getMaSach(), soLuongNhap);
                maSachLuu = existingSach.getMaSach();
                ghiChu = "Nhập kho: " + existingSach.getTenSach();
                AlertUtil.showInfo("Đã nhập thêm " + soLuongNhap + " cuốn " + existingSach.getTenSach() + ".");
            }
            
            else if (existingVpp != null) {
                vppDAO.addStock(existingVpp.getMaVPP(), soLuongNhap);
                maVppLuu = existingVpp.getMaVPP();
                ghiChu = "Nhập kho: " + existingVpp.getTenVPP();
                AlertUtil.showInfo("Đã nhập thêm " + soLuongNhap + " cái " + existingVpp.getTenVPP() + ".");
            }

            else {
                if (tenSanPham.isEmpty() || priceField.getText().isEmpty()) {
                    AlertUtil.showWarning("Tên và Giá là bắt buộc cho sản phẩm mới.");
                    return;
                }
                ghiChu = "Thêm mới: " + tenSanPham;

                if (isBook) {
                    int maNXB;
                    if (publisherComboBox.getValue() == null) { AlertUtil.showWarning("Vui lòng chọn NXB."); return; }
                    if (publisherComboBox.getValue().getTenNXB().equals(ADD_NEW_PUBLISHER_TEXT)) {
                        if (newPublisherField.getText().isEmpty()) { AlertUtil.showWarning("Vui lòng nhập tên NXB mới."); return; }
                        NhaXuatBan newNXB = new NhaXuatBan(); newNXB.setTenNXB(newPublisherField.getText());
                        maNXB = nxbDAO.insertAndGetId(newNXB);
                    } else { maNXB = publisherComboBox.getValue().getMaNXB(); }

                    int maTL;
                    if (genreComboBox.getValue() == null) { AlertUtil.showWarning("Vui lòng chọn Thể loại."); return; }
                    if (genreComboBox.getValue().getTenTL().equals(ADD_NEW_GENRE_TEXT)) {
                        if (newGenreField.getText().isEmpty()) { AlertUtil.showWarning("Vui lòng nhập tên Thể loại mới."); return; }
                        TheLoai newGenre = new TheLoai(); newGenre.setTenTL(newGenreField.getText());
                        maTL = theLoaiDAO.insertAndGetId(newGenre);
                    } else { maTL = genreComboBox.getValue().getMaTL(); }

                    Sach newBook = new Sach();
                    newBook.setTenSach(tenSanPham);
                    newBook.setDonGia(Double.parseDouble(priceField.getText()));
                    newBook.setSoLuong(soLuongNhap);
                    newBook.setAnh(imageField.getText());
                    newBook.setMaNXB(maNXB);
                    newBook.setNamXuatBan(Integer.parseInt(yearField.getText()));
                    newBook.setMoTa(descriptionArea.getText());
                    newBook.setViTriKe("A1"); newBook.setViTriNgan("N1"); newBook.setViTriHang("H1");
                    
                    maSachLuu = sachDAO.insertAndGetId(newBook);
                    
                    SachTheLoai stl = new SachTheLoai(maSachLuu, maTL);
                    sachTheLoaiDAO.insert(stl);
                    AlertUtil.showInfo("Đã thêm Sách mới thành công!");

                } else { // Thêm VPP mới
                    String supplierName;
                    if (supplierComboBox.getValue() == null) { AlertUtil.showWarning("Vui lòng chọn NCC."); return; }
                    if (supplierComboBox.getValue().equals(ADD_NEW_SUPPLIER_TEXT)) {
                        if (newSupplierField.getText().isEmpty()) { AlertUtil.showWarning("Vui lòng nhập tên NCC mới."); return; }
                        supplierName = newSupplierField.getText();
                    } else { supplierName = supplierComboBox.getValue(); }
                    
                    VanPhongPham newVpp = new VanPhongPham();
                    newVpp.setTenVPP(tenSanPham);
                    newVpp.setDonGia(Double.parseDouble(priceField.getText()));
                    newVpp.setSoLuong(soLuongNhap);
                    newVpp.setAnh(imageField.getText());
                    newVpp.setNhaCungCap(supplierName);
                    newVpp.setGhiChu(notesArea.getText());
                    newVpp.setNguongDatHang(50);
                    
                    maVppLuu = vppDAO.insertAndGetId(newVpp);
                    AlertUtil.showInfo("Đã thêm VPP mới thành công!");
                }
            }

            HoaDonNhapKho hdn = new HoaDonNhapKho();
            hdn.setMaNV(Integer.parseInt(AuthManager.getCurrentUserId()));
            hdn.setNgayNhap(LocalDate.now());
            hdn.setTongSoLuong(soLuongNhap);
            hdn.setGhiChu(ghiChu);
            
            int maHDN = hoaDonNhapKhoDAO.insertAndGetId(hdn);
            
            ChiTietNhapKho ctn = new ChiTietNhapKho();
            ctn.setMaHDN(maHDN);
            ctn.setSoLuongNhap(soLuongNhap);
            if (maSachLuu > 0) ctn.setMaSach(maSachLuu);
            if (maVppLuu > 0) ctn.setMaVPP(maVppLuu);
            
            chiTietNhapKhoDAO.insert(ctn);

            closeWindow(); // Đóng cửa sổ sau khi mọi thứ thành công

        } catch (NumberFormatException e) {
            AlertUtil.showError("Giá, Số lượng, và Năm XB phải là SỐ.");
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi CSDL khi lưu sản phẩm hoặc HĐ nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    private void checkProductExists() {
        String name = nameField.getText();
        if (name.isEmpty()) return;

        try {
            if (isBook) {
                existingSach = sachDAO.findExactlyByName(name);
                if (existingSach != null) {
                    setFormMode(true, "Sách đã tồn tại. Chỉ cần nhập số lượng.");
                    // Tự động điền giá cũ
                    priceField.setText(String.valueOf(existingSach.getDonGia()));
                } else {
                    setFormMode(false, null);
                }
            } else { // Là VPP
                existingVpp = vppDAO.findExactlyByName(name);
                if (existingVpp != null) {
                    setFormMode(true, "VPP đã tồn tại. Chỉ cần nhập số lượng.");
                    // Tự động điền giá cũ
                    priceField.setText(String.valueOf(existingVpp.getDonGia()));
                } else {
                    setFormMode(false, null);
                }
            }
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi kiểm tra sản phẩm: " + e.getMessage());
        }
    }

     //Bật/Tắt các trường nhập dựa trên việc sản phẩm có tồn tại hay không
     
    private void setFormMode(boolean isExisting, String message) {
        // Vô hiệu hóa các trường không cần thiết
        priceField.setDisable(isExisting);
        imageField.setDisable(isExisting);
        bookFieldsBox.setDisable(isExisting);
        vppFieldsBox.setDisable(isExisting);
        
        // Cập nhật trạng thái
        if (isExisting) {
            statusLabel.setText(message);
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
            saveButton.setText("Cập nhật Tồn kho"); 
            stockField.requestFocus(); 
        } else {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
            saveButton.setText("Lưu Sản Phẩm Mới");
            existingSach = null;
            existingVpp = null;
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
                // Xác định thư mục đích dựa trên isBook (Sách hay VPP)
                Path destDir;
                if (isBook) {
                    destDir = Paths.get("src/main/resources/com/bookstore/view/images/books");
                } else {
                    destDir = Paths.get("src/main/resources/com/bookstore/view/images/vpp");
                }
                
                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                
                // Đường dẫn đầy đủ của tệp đích
                Path destPath = destDir.resolve(selectedFile.getName());
                
                // Sao chép tệp
                Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                
                //Cập nhật TextField
                imageField.setText(selectedFile.getName());
                
                AlertUtil.showInfo("Đã sao chép ảnh '" + selectedFile.getName() + "' vào dự án.");

            } catch (IOException e) {
                e.printStackTrace();
                AlertUtil.showError("Lỗi khi sao chép ảnh: " + e.getMessage());
            }
        }
    }
}