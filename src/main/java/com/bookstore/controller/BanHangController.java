package com.bookstore.controller;

import com.bookstore.dao.*;
import com.bookstore.util.AlertUtil;
import com.bookstore.util.AuthManager;
import com.bookstore.controller.*;


import com.bookstore.model.*;

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

public class BanHangController {

    @FXML private TextField searchField;
    @FXML private ListView<TheLoai> categoryListView;
    @FXML private ListView<LoaiVPP> vppCategoryListView;
    @FXML private TilePane bookTilePane;
    @FXML private ListView<AbstractChiTietHoaDon> cartListView;
    @FXML private Label totalLabel;
    @FXML private Label discountLabel;
    @FXML private Button checkoutButton;
    @FXML private Button clearCartButton;

    private SachDAO sachDAO = new SachDAO();
    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
    private GioHang cart = new GioHang(); 
    private List<Sach> allBooksCache;
    private DecimalFormat df = new DecimalFormat("#,##0 VND");
    private Map<Integer, Sach> sachCache = new HashMap<>();
    private VppDAO vppDAO = new VppDAO();
    private List<VanPhongPham> allVppsCache; // Cache VPP
    private Map<Integer, VanPhongPham> vppCache = new HashMap<>();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private LoaiVPPDAO loaiVPPDAO = new LoaiVPPDAO();

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
        
        updateCartUI(); 
    }
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

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterBooksByName(newVal);
        });
    }

    private void setupCartListView() { // giỏ hàng
        cartListView.setCellFactory(param -> new ListCell<AbstractChiTietHoaDon>() {
            private final Button btnMinus = new Button("-");
            private final Button btnPlus = new Button("+");
            private final Button btnRemove = new Button("x");
            private final Label lblName = new Label();
            private final Label lblPrice = new Label();
            private final Label lblQty = new Label();
            private final VBox infoBox = new VBox(3, lblName, lblPrice);
            private final HBox qtyBox = new HBox(5, btnMinus, lblQty, btnPlus);
            private final HBox layout = new HBox(10, infoBox, qtyBox, btnRemove);

            {
                layout.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS); // Đẩy tên sang trái
                infoBox.setAlignment(Pos.CENTER_LEFT);
                qtyBox.setAlignment(Pos.CENTER);

                btnMinus.setStyle("-fx-min-width: 30px; -fx-background-radius: 5;");
                btnPlus.setStyle("-fx-min-width: 30px; -fx-background-radius: 5;");
                btnRemove.setStyle("-fx-min-width: 30px; -fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                
                lblPrice.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                lblName.setStyle("-fx-font-weight: bold;");
                lblName.setWrapText(true); 
                lblName.setMaxWidth(180);  
                
                // Xử lý sự kiện 
                btnPlus.setOnAction(e -> {
                    AbstractChiTietHoaDon item = getItem();
                    if (item != null) modifyQuantity(item, 1);
                });

                btnMinus.setOnAction(e -> {
                    AbstractChiTietHoaDon item = getItem();
                    if (item != null) modifyQuantity(item, -1);
                });

                btnRemove.setOnAction(e -> {
                    AbstractChiTietHoaDon item = getItem();
                    if (item != null) removeItem(item);
                });
            }

            @Override
            protected void updateItem(AbstractChiTietHoaDon item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Lấy thông tin hiển thị
                    String name = "Unknown";
                    double price = 0;
                    
                    if (item instanceof ChiTietHoaDon) {
                        Sach s = sachCache.get(((ChiTietHoaDon) item).getMaSach());
                        if (s != null) {
                            name = s.getTenSach();
                            // Hiển thị giá gốc
                            price = s.getDonGia();
                        }
                    } else if (item instanceof ChiTietHoaDonVPP) {
                        VanPhongPham v = vppCache.get(((ChiTietHoaDonVPP) item).getMaVPP());
                        if (v != null) {
                            name = v.getTenVPP();
                            price = v.getDonGia();
                        }
                    }
                    lblName.setText(name);
                    lblPrice.setText(df.format(price));
                    lblQty.setText(String.valueOf(item.getSoLuong()));

                    setGraphic(layout);
                }
            }
        });
    }

    private void modifyQuantity(AbstractChiTietHoaDon item, int delta) { 
        int newQty = item.getSoLuong() + delta;
        if (newQty <= 0) {
            removeItem(item);
            return;
        }
        // Kiểm tra tồn kho
        int availableStock = 0;
        if (item instanceof ChiTietHoaDon) {
			Sach s = sachCache.get(((ChiTietHoaDon) item).getMaSach());
			if (s != null) {
				availableStock = s.getSoLuong();
			}
		} else if (item instanceof ChiTietHoaDonVPP) {
			VanPhongPham v = vppCache.get(((ChiTietHoaDonVPP) item).getMaVPP());
			if (v != null) {
				availableStock = v.getSoLuong();
			}
		}
        if (newQty > availableStock) {
			AlertUtil.showWarning("Không thể thêm nữa! Đã đạt giới hạn tồn kho.");
			return;
		}
        // Cập nhật số lượng
        item.setSoLuong(newQty);
        updateCartUI(); 
    }
    private void removeItem(AbstractChiTietHoaDon item) {
        if (item instanceof ChiTietHoaDon) {
            cart.getBookItems().remove(item);
        } else if (item instanceof ChiTietHoaDonVPP) {
            cart.getVppItems().remove(item);
        }
        updateCartUI();
    }
    // Tải toàn bộ sách vào cache
    private void loadAllBooksToCache() {
        try {
            // Lấy 500 sách VÀ LỌC BỎ SÁCH HẾT HÀNG
            allBooksCache = sachDAO.getAll(500, 0).stream()
                .filter(sach -> sach.getSoLuong() > 0)
                .collect(Collectors.toList());
            	sachCache.clear();
            for (Sach s : allBooksCache) {
                sachCache.put(s.getMaSach(), s); // Lưu vào map để tra cứu nhanh
            }
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

        StackPane imageWrapper = new StackPane(imageView);
        imageWrapper.setMinHeight(200);
        imageWrapper.setMinWidth(150);
        imageWrapper.setAlignment(Pos.CENTER);

        Label title = new Label(sach.getTenSach());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold;");

        VBox priceContainer = new VBox(2);
        priceContainer.setAlignment(Pos.CENTER_LEFT);

        double originalPrice = sach.getDonGia();
        double finalPrice = originalPrice;
        String promo = sach.getKhuyenMai();   
        int percent = 0;

        if (promo != null && promo.startsWith("GIAM_")) {
            try {
                percent = Integer.parseInt(promo.replace("GIAM_", ""));
                finalPrice = originalPrice * (1 - percent / 100.0);
            } catch (Exception e) {
                percent = 0;
            }
        }

        if (percent > 0) {

            HBox promoRow = new HBox(8);
            promoRow.setAlignment(Pos.CENTER_LEFT);
            Label salePrice = new Label(df.format(finalPrice));
            salePrice.getStyleClass().add("price-sale");
            Label badge = new Label("-" + percent + "%");
            badge.getStyleClass().add("discount-badge");
            promoRow.getChildren().addAll(salePrice, badge);
            Label oldPrice = new Label(df.format(originalPrice));
            oldPrice.getStyleClass().add("price-original");

            priceContainer.getChildren().addAll(promoRow, oldPrice);
        } else {
            // Không giảm giá
            Label normalPrice = new Label(df.format(originalPrice));
            normalPrice.getStyleClass().add("price-sale");
            priceContainer.getChildren().add(normalPrice);
        }

        Label stock = new Label("Còn: " + sach.getSoLuong() + " cuốn");
        stock.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Button addButton = new Button("Thêm vào giỏ");
        addButton.getStyleClass().add("button-primary");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> handleAddToCart(sach));

        card.getChildren().addAll(
            imageWrapper,
            title,
            priceContainer,  
            stock,
            addButton
        );

        card.setOnMouseClicked(event -> {
            if (!event.getTarget().equals(addButton)) {
                showBookDetail(sach);
            }
        });

        return card;
    }

    @FXML
    private void handleClearFilters() { // Xóa bộ lọc
    	searchField.clear();
        categoryListView.getSelectionModel().clearSelection();
        vppCategoryListView.getSelectionModel().clearSelection();
        
        bookTilePane.getChildren().clear(); 
        displayBooks(allBooksCache);        
        displayVPPs(allVppsCache);
    }

    // Lọc tìm kiếm Sách VÀ VPP
    private void filterBooksByName(String keyword) {
    	bookTilePane.getChildren().clear(); 

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
    private void updateCartUI() { // Cập nhật giao diện giỏ hàng

        List<AbstractChiTietHoaDon> allItems = new ArrayList<>();
        allItems.addAll(cart.getBookItems());
        allItems.addAll(cart.getVppItems());
        cartListView.setItems(FXCollections.observableArrayList(allItems));

        double finalTotal = cart.getTotal(); // Tổng tiền thực tế phải trả (đã trừ KM)
        double originalTotal = 0;            // Tổng tiền gốc (chưa trừ KM)

        for (ChiTietHoaDon item : cart.getBookItems()) {
            Sach s = sachCache.get(item.getMaSach());
            if (s != null) {
                originalTotal += s.getDonGia() * item.getSoLuong();
            }
        }
        for (ChiTietHoaDonVPP item : cart.getVppItems()) {
            VanPhongPham v = vppCache.get(item.getMaVPP());
            if (v != null) {
                originalTotal += v.getDonGia() * item.getSoLuong();
            }
        }
        double discountTotal = originalTotal - finalTotal;
        totalLabel.setText(df.format(finalTotal));
        
        if (discountTotal > 0) {
            discountLabel.setText("Khuyến mãi: -" + df.format(discountTotal));
            discountLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
        } else {
            discountLabel.setText("Khuyến mãi: 0 VND");
            discountLabel.setStyle("-fx-text-fill: #333333;");
        }
    }
    @FXML
    private void handleCheckout() { // Xử lý thanh toán
        if (cart.getTotal() == 0) {
            AlertUtil.showWarning("Giỏ hàng của bạn đang trống!");
            return;
        }

        try {
            // Mở cửa sổ Thanh toán
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/CuaSoThanhToanView.fxml"));
            Parent root = loader.load();

            CuaSoThanhToanController CuaSoThanhToan = loader.getController();

         // Tính tổng gốc dựa vào cache (giá gốc) giống logic ở updateCartUI()
            double originalTotal = 0;
            for (ChiTietHoaDon item : cart.getBookItems()) {
            	Sach s = sachCache.get(item.getMaSach());
            	if (s != null) {
            		originalTotal += s.getDonGia() * item.getSoLuong();
            	}
            }
            for (ChiTietHoaDonVPP item : cart.getVppItems()) {
            	VanPhongPham v = vppCache.get(item.getMaVPP());
            	if (v != null) {
            		originalTotal += v.getDonGia() * item.getSoLuong();
            	}
            }	
         	double finalTotal = cart.getTotal();

         	// Khuyến mãi hiển thị ở Sale = original - final .
         	double discountTotal = originalTotal - finalTotal;

         	CuaSoThanhToan.setCart(cart);
         	CuaSoThanhToan.setTotals(originalTotal, discountTotal, finalTotal);

            Stage stage = new Stage();
            stage.setTitle("Thanh toán đơn hàng");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            String iconPath = "/com/bookstore/view/images/money.png";
            stage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));

            stage.showAndWait();

            // Kiểm tra xem người dùng có xác nhận thanh toán không
            if (CuaSoThanhToan.isPaid()) {
                
                // Lấy Khách hàng (mới hoặc cũ)
                KhachHang customer = CuaSoThanhToan.getCustomerToUse();
                int maKH = 1; // Mặc định là Khách vãng lai (MaKH = 1)
                
                try {
                    if (customer != null) {
                        if (customer.getMaKH() > 0) {
                            // khách hàng cũ
                            maKH = customer.getMaKH();
                        } else {
                            //  khách hàng mới , chèn vào DB và lấy MaKH
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
         } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi không thể mở cửa sổ thanh toán: " + e.getMessage());
        }
    }
    private void showBookDetail(Sach book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/SachView.fxml"));
            Parent root = loader.load();

            SachController controller = loader.getController();
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

    private void loadVppCategories() {
        try {
            vppCategoryListView.setItems(
                FXCollections.observableArrayList(loaiVPPDAO.getAll())
            );
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải loại VPP: " + e.getMessage());
        }
    }
    private void setupVppCategoryList() {
        vppCategoryListView.setCellFactory(lv -> new ListCell<LoaiVPP>() {
            @Override
            protected void updateItem(LoaiVPP item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenLoai());
            }
        });

        vppCategoryListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    categoryListView.getSelectionModel().clearSelection();
                    filterVppByLoai(newVal);
                }
            }
        );
    }
    private void filterVppByLoai(LoaiVPP loai) {
        try {
            List<VanPhongPham> filtered =
                vppDAO.getByLoaiVPP(loai.getMaLoaiVPP());

            bookTilePane.getChildren().clear();
            displayVPPs(filtered);

        } catch (SQLException e) {
            AlertUtil.showError("Lỗi lọc VPP: " + e.getMessage());
        }
    }
    // Tải VPP vào cache
    private void loadAllVppsToCache() {
        try {
            allVppsCache = vppDAO.getAll().stream()
                .filter(vpp -> vpp.getSoLuong() > 0)
                .collect(Collectors.toList());
            vppCache.clear();
            for (VanPhongPham v : allVppsCache) {
                vppCache.put(v.getMaVPP(), v);
            }
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
    private VBox createVppCard(VanPhongPham vpp) { // Tạo thẻ VPP
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

        StackPane imageWrapper = new StackPane(imageView);
        imageWrapper.setMinHeight(200);
        imageWrapper.setMinWidth(150);
        imageWrapper.setAlignment(Pos.CENTER);

        Label title = new Label(vpp.getTenVPP());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold;");

        VBox priceContainer = new VBox(2);
        priceContainer.setAlignment(Pos.CENTER_LEFT);

        double originalPrice = vpp.getDonGia();
        double finalPrice = originalPrice;
        String promo = vpp.getKhuyenMai();   
        int percent = 0;

        if (promo != null && promo.startsWith("GIAM_")) {
            try {
                percent = Integer.parseInt(promo.replace("GIAM_", ""));
                finalPrice = originalPrice * (1 - percent / 100.0);
            } catch (Exception e) {
                percent = 0;
            }
        }

        if (percent > 0) {
            // Dòng 1: Giá mới + badge
            HBox promoRow = new HBox(8);
            promoRow.setAlignment(Pos.CENTER_LEFT);

            Label salePrice = new Label(df.format(finalPrice));
            salePrice.getStyleClass().add("price-sale");

            Label badge = new Label("-" + percent + "%");
            badge.getStyleClass().add("discount-badge");

            promoRow.getChildren().addAll(salePrice, badge);

            // Dòng 2: Giá gốc gạch ngang
            Label oldPrice = new Label(df.format(originalPrice));
            oldPrice.getStyleClass().add("price-original");

            priceContainer.getChildren().addAll(promoRow, oldPrice);
        } else { // Không giảm giá
            Label normalPrice = new Label(df.format(originalPrice));
            normalPrice.getStyleClass().add("price-sale");
            priceContainer.getChildren().add(normalPrice);
        }

        Label stock = new Label("Còn: " + vpp.getSoLuong() + " cái");
        stock.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Button addButton = new Button("Thêm vào giỏ");
        addButton.getStyleClass().add("button-primary");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> handleAddVppToCart(vpp));

        card.getChildren().addAll(
            imageWrapper,
            title,
            priceContainer,
            stock,
            addButton
        );

        card.setOnMouseClicked(event -> {
            if (!event.getTarget().equals(addButton)) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/VppView.fxml"));
            Parent root = loader.load();

            // Lấy controller của cửa sổ chi tiết VPP
            VppController controller = loader.getController();
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