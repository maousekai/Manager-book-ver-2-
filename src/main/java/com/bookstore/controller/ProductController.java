package com.bookstore.controller;

import com.bookstore.dao.SachDAO;
import com.bookstore.dao.TheLoaiDAO;
import com.bookstore.dao.VanPhongPhamDAO;
import com.bookstore.model.Sach;
import com.bookstore.model.TheLoai;
import com.bookstore.model.VanPhongPham;
import com.bookstore.util.AlertUtil;
import com.bookstore.util.AuthManager; // 🔻 THÊM IMPORT NÀY 🔻
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // 🔻 THÊM IMPORT NÀY 🔻
import javafx.geometry.Insets;
import javafx.scene.Parent; // 🔻 THÊM IMPORT NÀY 🔻
import javafx.scene.Scene; // 🔻 THÊM IMPORT NÀY 🔻
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality; // 🔻 THÊM IMPORT NÀY 🔻
import javafx.stage.Stage; // 🔻 THÊM IMPORT NÀY 🔻
import javafx.geometry.Pos;
import java.io.IOException; // 🔻 THÊM IMPORT NÀY 🔻
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.layout.StackPane;

public class ProductController {

    // Cột 1: Lọc
    @FXML private TextField searchField;
    @FXML private ListView<TheLoai> categoryListView;
    @FXML private ListView<String> vppCategoryListView;

    // Cột 2: Sản phẩm
    @FXML private TilePane productTilePane;
    @FXML private Button importStockButton;

    // Các DAO
    private SachDAO sachDAO = new SachDAO();
    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
    private VanPhongPhamDAO vppDAO = new VanPhongPhamDAO();

    // Cache
    private List<Sach> allBooksCache;
    private List<VanPhongPham> allVppsCache;
    private DecimalFormat df = new DecimalFormat("#,##0 VND");

    @FXML
    private void initialize() {
        setupCategoryList();
        setupVppCategoryList();
        setupSearchField();
        
        loadAllData(); // Tải dữ liệu lần đầu
    }

    // Tải/Tải lại tất cả dữ liệu
    private void loadAllData() {
        loadAllBooksToCache();
        loadAllVppsToCache();
        loadCategories();
        loadVppCategories();
        
        handleClearFilters(); // Hiển thị tất cả
    }

    // --- CÁC HÀM KHỞI TẠO (Setup) ---

    private void loadCategories() {
        try {
            categoryListView.setItems(FXCollections.observableArrayList(theLoaiDAO.getAll()));
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải thể loại: " + e.getMessage());
        }
    }

    private void setupCategoryList() {
        categoryListView.setCellFactory(lv -> new ListCell<TheLoai>() {
            @Override
            protected void updateItem(TheLoai item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getTenTL());
            }
        });

        categoryListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    vppCategoryListView.getSelectionModel().clearSelection();
                    filterBooksByCategory(newVal);
                }
            }
        );
    }
    
    private void loadVppCategories() {
        vppCategoryListView.setItems(FXCollections.observableArrayList(
            "Bút - Viết", "Tập - Vở", "Dụng cụ học sinh", "Sản phẩm khác"
        ));
    }

    private void setupVppCategoryList() {
        vppCategoryListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    categoryListView.getSelectionModel().clearSelection();
                    filterProductsByVppCategory(newVal);
                }
            }
        );
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterProductsByName(newVal);
        });
    }

    // --- CÁC HÀM XỬ LÝ DỮ LIỆU ---

    private void loadAllBooksToCache() {
        try {
            allBooksCache = sachDAO.getAll(500, 0);
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Sách: " + e.getMessage());
            allBooksCache = FXCollections.observableArrayList();
        }
    }
    
    private void loadAllVppsToCache() {
        try {
            allVppsCache = vppDAO.getAll();
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải VPP: " + e.getMessage());
            allVppsCache = FXCollections.observableArrayList();
        }
    }

    // 🔻 SỬA LẠI HÀM NÀY 🔻
    private void displayBooks(List<Sach> booksToDisplay) {
        for (Sach sach : booksToDisplay) {
            VBox bookCard = createBookCard(sach); // Gọi hàm (Sach)
            productTilePane.getChildren().add(bookCard);
        }
    }

    // 🔻 SỬA LẠI HÀM NÀY 🔻
    private void displayVPPs(List<VanPhongPham> vppsToDisplay) {
        for (VanPhongPham vpp : vppsToDisplay) {
            VBox vppCard = createVppCard(vpp); // Gọi hàm (VPP)
            productTilePane.getChildren().add(vppCard);
        }
    }

    // 🔻 XÓA HÀM CŨ createProductCard(...) 🔻
    // (Đã xóa)

    // 🔻 THÊM HÀM MỚI: createBookCard(Sach sach) 🔻
    private VBox createBookCard(Sach sach) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);

        card.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView();
        try {
            String imagePath = "/com/bookstore/view/images/books/" + sach.getAnh();
            Image img = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(img);
        } catch (Exception e) {}
        imageView.setFitWidth(150);
        imageView.setFitHeight(200); 
        imageView.setPreserveRatio(true);
        StackPane imageWrapper = new StackPane();
        imageWrapper.setMinHeight(200); 
        imageWrapper.setMinWidth(150);  
        imageWrapper.setAlignment(Pos.CENTER);
        imageWrapper.getChildren().add(imageView);

        Label title = new Label(sach.getTenSach());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold;");
        Label price = new Label(df.format(sach.getDonGia()));
        price.setStyle("-fx-font-size: 14px; -fx-text-fill: #D32F2F;");
        Label stock = new Label("Còn: " + sach.getSoLuong() + " cuốn");
        stock.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        card.getChildren().addAll(imageWrapper, title, price, stock);
        
        // PHÂN QUYỀN SỬA
        if (AuthManager.getCurrentRole() == 1) { // 1 = MANAGER
            card.setOnMouseClicked(e -> showEditBook(sach));
            card.setStyle("-fx-cursor: hand;");
        }
        
        return card;
    }

    private VBox createVppCard(VanPhongPham vpp) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);

        card.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView();
        try {
            String imagePath = "/com/bookstore/view/images/vpp/" + vpp.getAnh();
            Image img = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(img);
        } catch (Exception e) { }
        imageView.setFitWidth(150);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        
        StackPane imageWrapper = new StackPane();
        imageWrapper.setMinHeight(200);
        imageWrapper.setMinWidth(150);  
        imageWrapper.setAlignment(Pos.CENTER);
        imageWrapper.getChildren().add(imageView);
        Label title = new Label(vpp.getTenVPP());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold;");
        Label price = new Label(df.format(vpp.getDonGia()));
        price.setStyle("-fx-font-size: 14px; -fx-text-fill: #D32F2F;");
        Label stock = new Label("Còn: " + vpp.getSoLuong() + " cái");
        stock.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        card.getChildren().addAll(imageWrapper, title, price, stock);
        
        // PHÂN QUYỀN SỬA
        if (AuthManager.getCurrentRole() == 1) { // 1 = MANAGER
            card.setOnMouseClicked(e -> showEditVpp(vpp));
            card.setStyle("-fx-cursor: hand;");
        }
        
        return card;
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        categoryListView.getSelectionModel().clearSelection();
        vppCategoryListView.getSelectionModel().clearSelection();
        
        productTilePane.getChildren().clear(); 
        displayBooks(allBooksCache);        
        displayVPPs(allVppsCache);
    }

    // Lọc theo Tên (Sách và VPP)
    private void filterProductsByName(String keyword) {
        productTilePane.getChildren().clear();
        if (keyword == null || keyword.isEmpty()) {
            handleClearFilters();
            return;
        }

        String lowerCaseKeyword = keyword.toLowerCase();
        
        List<Sach> filteredBooks = allBooksCache.stream()
            .filter(s -> s.getTenSach().toLowerCase().contains(lowerCaseKeyword))
            .collect(Collectors.toList());
        displayBooks(filteredBooks);
        
        List<VanPhongPham> filteredVPPs = allVppsCache.stream()
            .filter(v -> v.getTenVPP().toLowerCase().contains(lowerCaseKeyword))
            .collect(Collectors.toList());
        displayVPPs(filteredVPPs);
    }

    // Lọc theo Thể loại Sách
    private void filterBooksByCategory(TheLoai category) {
        try {
            List<Sach> filtered = sachDAO.getByGenre(category.getMaTL());
            productTilePane.getChildren().clear();
            displayBooks(filtered);
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi lọc theo thể loại: " + e.getMessage());
        }
    }
    
    // Lọc theo Thể loại VPP
    private void filterProductsByVppCategory(String vppCategoryName) {
        String filterWord = "";
        if (vppCategoryName.contains("Bút")) filterWord = "bút";
        else if (vppCategoryName.contains("Vở")) filterWord = "vở";
        else filterWord = vppCategoryName.toLowerCase();

        String finalFilterWord = filterWord.toLowerCase();
        List<VanPhongPham> filtered = allVppsCache.stream()
            .filter(vpp -> vpp.getTenVPP().toLowerCase().contains(finalFilterWord))
            .collect(Collectors.toList());
        
        productTilePane.getChildren().clear();
        displayVPPs(filtered);
    }

    @FXML
    private void handleImportStock() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/NewProductView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Sản Phẩm Mới / Nhập kho");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            String iconPath = "/com/bookstore/view/images/iconmain.png";
            stage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
            stage.showAndWait(); 
            loadAllData();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ Thêm Sản Phẩm Mới: " + e.getMessage());
        }
    }
    private void showEditBook(Sach sach) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/BookEditView.fxml"));
            Parent root = loader.load();

            BookEditController controller = loader.getController();
            controller.setBook(sach);

            Stage stage = new Stage();
            stage.setTitle("Sửa chi tiết Sách");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            String iconPath = "/com/bookstore/view/images/iconmain.png";
            stage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
            
            stage.showAndWait();
            
            if (controller.isDataChanged()) {
                loadAllData();
            }

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ Sửa Sách: " + e.getMessage());
        }
    }
    
    private void showEditVpp(VanPhongPham vpp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/VppEditView.fxml"));
            Parent root = loader.load();

            VppEditController controller = loader.getController();
            controller.setVpp(vpp); // Truyền VPP

            Stage stage = new Stage();
            stage.setTitle("Sửa chi tiết Văn phòng phẩm");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            String iconPath = "/com/bookstore/view/images/iconmain.png";
            stage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
            
            stage.showAndWait();
            
            if (controller.isDataChanged()) {
                loadAllData();
            }

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ Sửa VPP: " + e.getMessage());
        }
    }
}