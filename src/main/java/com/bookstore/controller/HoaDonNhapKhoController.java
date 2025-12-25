package com.bookstore.controller;

import com.bookstore.dao.HoaDonNhapKhoDAO;
import com.bookstore.dao.NhanVienDAO;
import com.bookstore.model.HoaDonNhapKho;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoaDonNhapKhoController {

    @FXML private TableView<HoaDonNhapKho> invoiceTable;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    private HoaDonNhapKhoDAO hoaDonNhapKhoDAO = new HoaDonNhapKhoDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    
    // Cache để lưu Tên NV, tránh gọi CSDL liên tục
    private Map<Integer, String> nhanVienCache = new HashMap<>();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadInvoices();
    }

    private void setupTableColumns() {
        TableColumn<HoaDonNhapKho, Integer> idCol = new TableColumn<>("Mã HĐN");
        idCol.setCellValueFactory(new PropertyValueFactory<>("maHDN"));
        idCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.1)); // 10%
        
        TableColumn<HoaDonNhapKho, LocalDate> dateCol = new TableColumn<>("Ngày Nhập");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("ngayNhap"));
        dateCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.15)); // 15%

        TableColumn<HoaDonNhapKho, String> nvCol = new TableColumn<>("Tên NV Nhập");
        nvCol.setCellValueFactory(cellData -> {
            int maNV = cellData.getValue().getMaNV();
            String tenNV = nhanVienCache.computeIfAbsent(maNV, id -> {
                try {
                    NhanVien nv = nhanVienDAO.getById(id);
                    return (nv != null) ? nv.getTenNV() : "Không rõ";
                } catch (SQLException e) {
                    return "Lỗi SQL";
                }
            });
            return new SimpleStringProperty(tenNV);
        });
        nvCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.20)); // 20%

        TableColumn<HoaDonNhapKho, Integer> qtyCol = new TableColumn<>("Tổng SL Nhập");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("tongSoLuong"));
        qtyCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.15)); // 15%
        
        TableColumn<HoaDonNhapKho, String> notesCol = new TableColumn<>("Ghi Chú / Tên SP");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));
        notesCol.prefWidthProperty().bind(invoiceTable.widthProperty().multiply(0.40)); // 40%
        
        invoiceTable.getColumns().clear();
        invoiceTable.getColumns().addAll(idCol, dateCol, nvCol, qtyCol, notesCol);
    }
    @FXML
    private void loadInvoices() { // Tải tất cả HĐ Nhập kho
    	startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        try {
            List<HoaDonNhapKho> invoices = hoaDonNhapKhoDAO.getAll();
            invoiceTable.setItems(FXCollections.observableArrayList(invoices));
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải HĐ Nhập kho: " + e.getMessage());
        }
    }
    @FXML
    private void handleFilter() { // Lọc HĐ Nhập kho theo ngày
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
            List<HoaDonNhapKho> invoices = hoaDonNhapKhoDAO.getAllByDateRange(start, end);
            invoiceTable.setItems(FXCollections.observableArrayList(invoices));
            AlertUtil.showInfo("Đã lọc " + invoices.size() + " hóa đơn nhập từ " + start + " đến " + end);
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi lọc HĐ Nhập kho: " + e.getMessage());
        }
    }	
}