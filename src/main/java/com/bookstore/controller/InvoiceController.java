package com.bookstore.controller;

import com.bookstore.dao.HoaDonDAO;
import com.bookstore.dao.KhachHangDAO;
import com.bookstore.dao.NhanVienDAO;
import com.bookstore.model.HoaDon;
import com.bookstore.model.KhachHang;
import com.bookstore.model.NhanVien;
import com.bookstore.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceController {

    @FXML private TableView<HoaDon> invoiceTable;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    
    // Cache
    private Map<Integer, String> nhanVienCache = new HashMap<>();
    private Map<Integer, String> khachHangCache = new HashMap<>();
    private DecimalFormat df = new DecimalFormat("#,##0 VND");

    @FXML
    private void initialize() {
        setupTableColumns();
        loadInvoices();
    }
    
 // 🔻 THAY THẾ TOÀN BỘ PHƯƠNG THỨC NÀY 🔻
    private void setupTableColumns() {
        TableColumn<HoaDon, Integer> idCol = new TableColumn<>("Mã HĐ");
        idCol.setCellValueFactory(new PropertyValueFactory<>("maHD"));
        idCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.1)); // 10%

        TableColumn<HoaDon, LocalDate> dateCol = new TableColumn<>("Ngày Lập");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("ngayLap"));
        dateCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.15)); // 15%

        TableColumn<HoaDon, String> nvCol = new TableColumn<>("Nhân viên bán");
        nvCol.setCellValueFactory(cellData -> {
            int maNV = cellData.getValue().getMaNV();
            String tenNV = nhanVienCache.computeIfAbsent(maNV, id -> {
                try {
                    NhanVien nv = nhanVienDAO.getById(id);
                    return (nv != null) ? nv.getTenNV() : "Không rõ";
                } catch (SQLException e) { return "Lỗi"; }
            });
            return new SimpleStringProperty(tenNV);
        });
        nvCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.20)); // 20%
        
        TableColumn<HoaDon, String> khCol = new TableColumn<>("Khách hàng");
        khCol.setCellValueFactory(cellData -> {
            int maKH = cellData.getValue().getMaKH();
            String tenKH = khachHangCache.computeIfAbsent(maKH, id -> {
                try {
                    KhachHang kh = khachHangDAO.getById(id);
                    return (kh != null) ? kh.getHoTen() : "Không rõ";
                } catch (SQLException e) { return "Lỗi"; }
            });
            return new SimpleStringProperty(tenKH);
        });
        khCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.20)); // 20%

        TableColumn<HoaDon, Integer> qtyCol = new TableColumn<>("SL Sách");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("soSachMua"));
        qtyCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.15)); // 15%

        TableColumn<HoaDon, String> totalCol = new TableColumn<>("Tổng Tiền");
        totalCol.setCellValueFactory(cellData -> {
            String formattedTotal = df.format(cellData.getValue().getTongTien());
            return new SimpleStringProperty(formattedTotal);
        });
        totalCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.20)); // 20%

        invoiceTable.getColumns().clear();
        invoiceTable.getColumns().addAll(idCol, dateCol, nvCol, khCol, qtyCol, totalCol);
    }
    @FXML
    private void loadInvoices() {
        // Xóa bộ lọc ngày
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        
        try {
            List<HoaDon> invoices = hoaDonDAO.getAll();
            invoiceTable.setItems(FXCollections.observableArrayList(invoices));
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Hóa đơn: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleFilter() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            AlertUtil.showWarning("Vui lòng chọn cả 'Từ ngày' và 'Đến ngày' để lọc.");
            return;
        }
        
        if (start.isAfter(end)) {
            AlertUtil.showWarning("'Từ ngày' không thể sau 'Đến ngày'.");
            return;
        }

        try {
            // Gọi phương thức DAO mới
            List<HoaDon> invoices = hoaDonDAO.getAllByDateRange(start, end);
            invoiceTable.setItems(FXCollections.observableArrayList(invoices));
            AlertUtil.showInfo("Đã lọc " + invoices.size() + " hóa đơn từ " + start + " đến " + end);
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi lọc Hóa đơn: " + e.getMessage());
        }
    }
    }