package com.bookstore.controller;

import com.bookstore.dao.HoaDonDAO;
import com.bookstore.dao.SachDAO;
import com.bookstore.dao.TheLoaiDAO;
import com.bookstore.model.*;
import com.bookstore.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.YearMonth;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class BaoCaoController {

    // Bộ lọc
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    // KPI Cards
    @FXML private Label revenueLabel;
    @FXML private Label profitLabel;
    @FXML private Label itemsSoldLabel;

    // Biểu đồ
    @FXML private BarChart<String, Number> revenueBarChart;
    @FXML private PieChart genrePieChart;

    // Bảng
    @FXML private TableView<BookRanking> rankingTable;

    // DAOs
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private SachDAO sachDAO = new SachDAO();
    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
    
    private DecimalFormat df = new DecimalFormat("#,##0 VND");

    @FXML
    private void initialize() {
        // Đặt ngày mặc định là 1 tuần qua
        startDatePicker.setValue(LocalDate.now().minusWeeks(1));
        endDatePicker.setValue(LocalDate.now());
        ObservableList<String> months = FXCollections.observableArrayList(
                "Cả Năm", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            );
            monthComboBox.setItems(months);
            monthComboBox.setValue("Cả Năm"); // Mặc định

            // Điền năm 
            int currentYear = LocalDate.now().getYear();
            yearComboBox.setItems(FXCollections.observableArrayList(
                currentYear, currentYear - 1, currentYear - 2
            ));
            yearComboBox.setValue(currentYear);
            // Thiết lập bảng xếp hạng
        setupRankingTable();
        // Tải dữ liệu lần đầu
        handleFilter();
    }
    
    @FXML
    private void handleFilter() { // Lọc theo ngày
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

        // Gọi hàm trung tâm
        runReport(start, end);
        AlertUtil.showInfo("Đã lọc báo cáo từ " + start + " đến " + end);
    }

    @FXML
    private void handleFilterByMonthYear() { // Lọc theo tháng-năm
        Integer year = yearComboBox.getValue();
        String monthStr = monthComboBox.getValue();

        if (year == null || monthStr == null) {
            AlertUtil.showWarning("Vui lòng chọn Tháng và Năm.");
            return;
        }

        LocalDate startDate;
        LocalDate endDate;

        if (monthStr.equals("Cả Năm")) {
            // Lọc cả năm
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
            AlertUtil.showInfo("Đã lọc báo cáo cho cả năm " + year);
        } else {
            // Lọc theo tháng
            // Chuyển "Tháng 1" -> 1
            int month = Integer.parseInt(monthStr.split(" ")[1]); 
            
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
            AlertUtil.showInfo("Đã lọc báo cáo cho " + monthStr + " năm " + year);
        }

        // Cập nhật DatePicker
        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);

        // Gọi hàm trung tâm
        runReport(startDate, endDate);
    }

    private void loadKpiCards(LocalDate start, LocalDate end) throws SQLException { // KPI
        ThongSoBaoCao stats = hoaDonDAO.getReportStats(start, end);
        revenueLabel.setText(df.format(stats.getTotalRevenue()));
        profitLabel.setText(df.format(stats.getTotalProfit()));
        itemsSoldLabel.setText(String.valueOf(stats.getTotalItemsSold()));
    }
    
    private void loadRevenueChart(LocalDate start, LocalDate end) throws SQLException { // Biểu đồ doanh thu
        List<ThongSoNgay> dailyData = hoaDonDAO.getDailyRevenue(start, end);
        revenueBarChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        
        for (ThongSoNgay ds : dailyData) {
            series.getData().add(new XYChart.Data<>(ds.getNgay().toString(), ds.getDoanhThu()));
        }
        revenueBarChart.getData().add(series);
    }
    
    private void loadGenreChart(LocalDate start, LocalDate end) throws SQLException { // Biểu đồ thể loại
        List<ThongSoTheLoai> genreData = theLoaiDAO.getGenreStats(start, end);
        genrePieChart.getData().clear();
        
        for (ThongSoTheLoai gs : genreData) { 
            PieChart.Data slice = new PieChart.Data(gs.getTenTheLoai() + " (" + gs.getSoLuongBan() + ")", gs.getSoLuongBan()); 
            genrePieChart.getData().add(slice);
        }
    }
    
    private void setupRankingTable() { // Bảng xếp hạng
        TableColumn<BookRanking, String> nameCol = new TableColumn<>("Tên Sách");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        nameCol.prefWidthProperty().bind(rankingTable.widthProperty().multiply(0.8));

        TableColumn<BookRanking, Integer> qtyCol = new TableColumn<>("Số Lượng Bán");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("soLuongBan"));
        qtyCol.prefWidthProperty().bind(rankingTable.widthProperty().multiply(0.2));
        
        rankingTable.getColumns().addAll(nameCol, qtyCol);
    }
    
    private void loadRankingTableData(LocalDate start, LocalDate end) throws SQLException {
        List<BookRanking> rankingData = sachDAO.getBookRanking(start, end);
        rankingTable.setItems(FXCollections.observableArrayList(rankingData));
    }
    private void runReport(LocalDate start, LocalDate end) { //gọi hết các hàm load
        try {
            loadKpiCards(start, end);
            loadRevenueChart(start, end);
            loadGenreChart(start, end);
            loadRankingTableData(start, end);
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải dữ liệu báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}