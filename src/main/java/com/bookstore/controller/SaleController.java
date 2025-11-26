package com.bookstore.controller;

import com.bookstore.dao.SachDAO;
import com.bookstore.dao.TheLoaiDAO;
import com.bookstore.model.Cart;
import com.bookstore.model.ChiTietHoaDon;
import com.bookstore.model.Sach;
import com.bookstore.model.TheLoai;
import com.bookstore.util.AlertUtil;
import com.bookstore.util.AuthManager;
import com.bookstore.dao.VanPhongPhamDAO;
import com.bookstore.model.VanPhongPham;
import com.bookstore.model.ChiTietHoaDonVPP;
import com.bookstore.model.AbstractChiTietHoaDon;
import com.bookstore.controller.VppDetailController;
import com.bookstore.dao.KhachHangDAO;
import com.bookstore.dao.HoaDonDAO;
import com.bookstore.model.KhachHang;
import com.bookstore.model.HoaDon;
import java.time.LocalDate;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.layout.HBox;

public class SaleController {

    @FXML private TextField searchField;
    @FXML private ListView<TheLoai> categoryListView;
    @FXML private ListView<String> vppCategoryListView;
    @FXML private TilePane bookTilePane;
    @FXML private ListView<AbstractChiTietHoaDon> cartListView;
    @FXML private Label totalLabel;
    @FXML private Label discountLabel;
    @FXML private Button checkoutButton;
    @FXML private Button clearCartButton;

    private SachDAO sachDAO = new SachDAO();
    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
    private Cart cart = new Cart(); 
    private List<Sach> allBooksCache;
    private DecimalFormat df = new DecimalFormat("#,##0 VND");
    private Map<Integer, Sach> sachCache = new HashMap<>();
    private VanPhongPhamDAO vppDAO = new VanPhongPhamDAO();
    private List<VanPhongPham> allVppsCache; // Cache VPP
    private Map<Integer, VanPhongPham> vppCache = new HashMap<>();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    @FXML
    private void initialize() {
        setupCategoryList();
        setupVppCategoryList();
        setupSearchField();
        setupCartListView();

        loadAllBooksToCache();
        loadAllVppsToCache(); 
        loadCategories();
        loadVppCategories();

        bookTilePane.getChildren().clear();
        displayBooks(allBooksCache);
        displayVPPs(allVppsCache);
        
        updateCartUI(); // Cập nhật giỏ hàng
    }
    // Tải và hiển thị danh sách Thể loại
    private void loadCategories() {
        try {
            categoryListView.setItems(FXCollections.observableArrayList(theLoaiDAO.getAll()));
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải thể loại: " + e.getMessage());
        }
    }

    // Cấu hình cho ListView Thể loại
    private void setupCategoryList() {
        // Tùy chỉnh cách hiển thị tên Thể loại
        categoryListView.setCellFactory(lv -> new ListCell<TheLoai>() {
            @Override
            protected void updateItem(TheLoai item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getTenTL());
            }
        });

        // Lắng nghe sự kiện click
        categoryListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                	vppCategoryListView.getSelectionModel().clearSelection();
                    filterBooksByCategory(newVal);
                }
            }
        );
    }

    // Cấu hình cho ô tìm kiếm
    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterBooksByName(newVal);
        });
    }

    // 🔻 ĐÂY LÀ PHIÊN BẢN ĐÚNG (XỬ LÝ CẢ SÁCH VÀ VPP) 🔻
    private void setupCartListView() {
        cartListView.setCellFactory(lv -> new ListCell<AbstractChiTietHoaDon>() {
            
            private HBox hBox = new HBox(10);
            private Label nameLabel = new Label();
            private Button plusButton = new Button("+");
            private Button minusButton = new Button("-");
            private Button removeButton = new Button("x");
            private Label qtyLabel = new Label();
            private Region spacer = new Region();

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                nameLabel.setPrefWidth(120.0); 
                nameLabel.setWrapText(true);
                qtyLabel.setPrefWidth(20);
                
                String buttonStyle = "-fx-font-size: 10px; -fx-padding: 2 6;";
                plusButton.setStyle(buttonStyle);
                minusButton.setStyle(buttonStyle);
                removeButton.setStyle(buttonStyle + " -fx-background-color: #f8adad;");

                hBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                hBox.getChildren().addAll(nameLabel, spacer, minusButton, qtyLabel, plusButton, removeButton);
            }

            @Override
            protected void updateItem(AbstractChiTietHoaDon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                // KIỂM TRA XEM LÀ SÁCH HAY VPP
                if (item instanceof ChiTietHoaDon) {
                    // --- XỬ LÝ SÁCH ---
                    ChiTietHoaDon bookItem = (ChiTietHoaDon) item;
                    Sach s = sachCache.computeIfAbsent(bookItem.getMaSach(), ma -> {
                        try { return sachDAO.getById(ma); } catch (Exception e) { return null; }
                    });

                    if (s != null) {
                        nameLabel.setText(s.getTenSach());
                        qtyLabel.setText(String.valueOf(bookItem.getSoLuong()));
                        
                        plusButton.setOnAction(e -> handleAddToCart(s));
                        minusButton.setOnAction(e -> { cart.decreaseBook(bookItem); updateCartUI(); });
                        removeButton.setOnAction(e -> { cart.removeBook(bookItem); updateCartUI(); });
                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                    }

                } else if (item instanceof ChiTietHoaDonVPP) {
                    // --- XỬ LÝ VPP ---
                    ChiTietHoaDonVPP vppItem = (ChiTietHoaDonVPP) item;
                    VanPhongPham v = vppCache.computeIfAbsent(vppItem.getMaVPP(), ma -> {
                        try { return vppDAO.getById(ma); } catch (Exception e) { return null; }
                    });

                    if (v != null) {
                        nameLabel.setText(v.getTenVPP());
                        qtyLabel.setText(String.valueOf(vppItem.getSoLuong()));
                        
                        plusButton.setOnAction(e -> handleAddVppToCart(v));
                        minusButton.setOnAction(e -> { cart.decreaseVPP(vppItem); updateCartUI(); });
                        removeButton.setOnAction(e -> { cart.removeVPP(vppItem); updateCartUI(); });
                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    // Tải toàn bộ sách vào cache
    private void loadAllBooksToCache() {
        try {
            // Lấy 500 sách VÀ LỌC BỎ SÁCH HẾT HÀNG
            allBooksCache = sachDAO.getAll(500, 0).stream()
                .filter(sach -> sach.getSoLuong() > 0)
                .collect(Collectors.toList());
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải danh sách sách: " + e.getMessage());
            allBooksCache = FXCollections.observableArrayList();
        }
    }

    // Hiển thị sách lên TilePane
    private void displayBooks(List<Sach> booksToDisplay) {
        for (Sach sach : booksToDisplay) {
            VBox bookCard = createBookCard(sach);
            bookTilePane.getChildren().add(bookCard);
        }
    }

    // Tạo một thẻ VBox cho một cuốn sách
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
        } catch (Exception e) { 
            System.err.println("Lỗi tải ảnh sách: " + e.getMessage());
        }
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

        Button addButton = new Button("Thêm vào giỏ");
        addButton.getStyleClass().add("button-primary");
        addButton.setMaxWidth(Double.MAX_VALUE);
        
        addButton.setOnAction(e -> handleAddToCart(sach));

        card.getChildren().addAll(imageWrapper, title, price, stock, addButton);
        card.setOnMouseClicked(event -> {
            if (event.getTarget().equals(addButton) == false) {
                showBookDetail(sach);
            }
        });
        return card;
    }
    @FXML
    private void handleClearFilters() {
    	searchField.clear();
        categoryListView.getSelectionModel().clearSelection();
        vppCategoryListView.getSelectionModel().clearSelection();
        
        bookTilePane.getChildren().clear(); // Clear 1 lần ở đây
        displayBooks(allBooksCache);        
        displayVPPs(allVppsCache);
    }

    // Lọc tìm kiếm Sách VÀ VPP
    private void filterBooksByName(String keyword) {
    	bookTilePane.getChildren().clear(); // Clear 1 lần

        if (keyword == null || keyword.isEmpty()) {
            displayBooks(allBooksCache);
            displayVPPs(allVppsCache); // Hiển thị cả VPP nếu rỗng
            return;
        }

        String lowerCaseKeyword = keyword.toLowerCase();
        
        // Lọc Sách
        List<Sach> filteredBooks = allBooksCache.stream()
            .filter(sach -> sach.getTenSach().toLowerCase().contains(lowerCaseKeyword))
            .collect(Collectors.toList());
        displayBooks(filteredBooks);
        
        // Lọc VPP
        List<VanPhongPham> filteredVPPs = allVppsCache.stream()
            .filter(vpp -> vpp.getTenVPP().toLowerCase().contains(lowerCaseKeyword))
            .collect(Collectors.toList());
        displayVPPs(filteredVPPs);
    }

    // Lọc sách theo thể loại
    private void filterBooksByCategory(TheLoai category) {
        try {
            List<Sach> filtered = sachDAO.getByGenre(category.getMaTL());
            
            bookTilePane.getChildren().clear();
            displayBooks(filtered);
            
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi lọc theo thể loại: " + e.getMessage());
        }
    }

    // Thêm sách vào giỏ hàng
    private void handleAddToCart(Sach sach) {
        if (sach.getSoLuong() <= 0) {
            AlertUtil.showWarning("Sản phẩm đã hết hàng!");
            return;
        }
        
        cart.addBook(sach, 1);
        updateCartUI();
    }

    @FXML
    private void handleClearCart() {
        cart.clear();
        updateCartUI();
    }
    private void updateCartUI() {
        // GỘP 2 DANH SÁCH
        List<AbstractChiTietHoaDon> allItems = new ArrayList<>();
        allItems.addAll(cart.getBookItems());
        allItems.addAll(cart.getVppItems());
        cartListView.setItems(FXCollections.observableArrayList(allItems));
        
        // Cập nhật Tổng tiền
        totalLabel.setText(df.format(cart.getTotal()));

        // Logic khuyến mãi
        if (cart.getBookCount() >= 10) {
            discountLabel.setText("Đã đủ điều kiện KM (mua 10+ sách)");
        } else {
            discountLabel.setText("Khuyến mãi: 0 VND");
        }
    }

    @FXML
    private void handleCheckout() {
        if (cart.getTotal() == 0) {
            AlertUtil.showWarning("Giỏ hàng của bạn đang trống!");
            return;
        }

        try {
            // Mở cửa sổ Thanh toán
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/PaymentView.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setCart(cart);

            Stage stage = new Stage();
            stage.setTitle("Thanh toán đơn hàng");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            String iconPath = "/com/bookstore/view/images/money.png";
            stage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));

            stage.showAndWait();

            // Kiểm tra xem người dùng có xác nhận thanh toán không
            if (paymentController.isPaid()) {
                
                // Lấy Khách hàng (mới hoặc cũ)
                KhachHang customer = paymentController.getCustomerToUse();
                int maKH = 1; // Mặc định là Khách vãng lai (MaKH = 1)
                
                try {
                    if (customer != null) {
                        if (customer.getMaKH() > 0) {
                            // KHÁCH HÀNG CŨ (đã tìm thấy)
                            maKH = customer.getMaKH();
                        } else {
                            // KHÁCH HÀNG MỚI (cần insert)
                            maKH = khachHangDAO.insertAndGetId(customer);
                        }
                    }
                    // Nếu customer == null, vẫn dùng maKH = 1
                    
                } catch (SQLException e_kh) {
                    AlertUtil.showError("Lỗi khi lưu khách hàng: " + e_kh.getMessage());
                    // Vẫn tiếp tục với MaKH = 1
                }

                // Tạo Hóa Đơn
                try {
                    HoaDon hd = new HoaDon();
                    hd.setNgayLap(LocalDate.now());
                    hd.setMaNV(Integer.parseInt(AuthManager.getCurrentUserId()));
                    hd.setMaKH(maKH);
                    
                    hoaDonDAO.createHoaDonWithDetails(hd, cart.getBookItems(), cart.getVppItems());

                    AlertUtil.showInfo("Thanh toán thành công! Hóa đơn #" + hd.getMaHD() + " đã được tạo.");

                    // 5. Dọn dẹp
                    cart.clear();
                    updateCartUI();
                    
                    loadAllBooksToCache();
                    loadAllVppsToCache();
                    handleClearFilters();

                } catch (SQLException e_hd) {
                    AlertUtil.showError("Lỗi nghiêm trọng khi tạo hóa đơn: " + e_hd.getMessage());
                }
            }
            // (Nếu isPaid() == false, người dùng đã đóng cửa sổ)

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi không thể mở cửa sổ thanh toán: " + e.getMessage());
        }
    }
    private void showBookDetail(Sach book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/BookDetailView.fxml"));
            Parent root = loader.load();

            BookDetailController controller = loader.getController();
            controller.setBookAndCart(book, cart); 

            Stage stage = new Stage();
            stage.setTitle("Chi tiết sản phẩm");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 

            stage.showAndWait(); 
            updateCartUI();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ chi tiết: " + e.getMessage());
        }
    }

    // Tải danh sách thể loại VPP (dữ liệu giả)
    private void loadVppCategories() {
        vppCategoryListView.setItems(FXCollections.observableArrayList(
            "Bút - Viết", "Tập - Vở", "Dụng cụ học sinh", "Sản phẩm khác"
        ));
    }

    // Cấu hình cho ListView Thể loại VPP
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

    private void filterProductsByVppCategory(String vppCategoryName) {
        // Lọc VPP dựa trên tên (ví dụ: "Bút")
        String filterWord = "";
        if (vppCategoryName.contains("Bút")) filterWord = "bút";
        else if (vppCategoryName.contains("Vở")) filterWord = "vở";
        else if (vppCategoryName.contains("Dụng cụ")) filterWord = "dụng cụ";
        else filterWord = vppCategoryName.toLowerCase(); // Lọc chung

        String finalFilterWord = filterWord.toLowerCase();
        
        // Lọc từ cache VPP
        List<VanPhongPham> filtered = allVppsCache.stream()
            .filter(vpp -> vpp.getTenVPP().toLowerCase().contains(finalFilterWord))
            .collect(Collectors.toList());
        
        bookTilePane.getChildren().clear();
        displayVPPs(filtered); // Hiển thị VPP đã lọc
    }

    // Tải VPP vào cache
    private void loadAllVppsToCache() {
        try {
            // Lấy VPP VÀ LỌC BỎ HÀNG HẾT
            allVppsCache = vppDAO.getAll().stream()
                .filter(vpp -> vpp.getSoLuong() > 0)
                .collect(Collectors.toList());
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải danh sách VPP: " + e.getMessage());
            allVppsCache = FXCollections.observableArrayList();
        }
    }

    // Hiển thị VPP lên TilePane
    private void displayVPPs(List<VanPhongPham> vppsToDisplay) {
        for (VanPhongPham vpp : vppsToDisplay) {
            VBox vppCard = createVppCard(vpp);
            bookTilePane.getChildren().add(vppCard);
        }
    }
    // Xóa phiên bản createVppCard bị trùng lặp (cũ)
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
        } catch (Exception e) { 
            System.err.println("Lỗi tải ảnh vpp: " + e.getMessage());
        }
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

        Button addButton = new Button("Thêm vào giỏ");
        addButton.getStyleClass().add("button-primary");
        addButton.setMaxWidth(Double.MAX_VALUE);
        
        addButton.setOnAction(e -> handleAddVppToCart(vpp));
        card.getChildren().addAll(imageWrapper, title, price, stock, addButton);
        card.setOnMouseClicked(event -> {
            if (event.getTarget().equals(addButton) == false) {
                showVppDetail(vpp);
            }
        });
        
        return card;
    }

    // Xử lý thêm VPP vào giỏ
    private void handleAddVppToCart(VanPhongPham vpp) {
        if (vpp.getSoLuong() <= 0) {
            AlertUtil.showWarning("Sản phẩm đã hết hàng!");
            return;
        }
        cart.addVPP(vpp, 1);
        updateCartUI();
    }
    
    // Xử lý click VPP
    private void showVppDetail(VanPhongPham vpp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/VppDetailView.fxml"));
            Parent root = loader.load();

            // Lấy controller của cửa sổ chi tiết VPP
            VppDetailController controller = loader.getController();
            controller.setVppAndCart(vpp, cart); // Truyền VPP và giỏ hàng

            Stage stage = new Stage();
            stage.setTitle("Chi tiết Văn phòng phẩm");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Cập nhật giỏ hàng, phòng trường hợp thêm hàng từ cửa sổ chi tiết
            updateCartUI();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ chi tiết VPP: " + e.getMessage());
        }
    }
}