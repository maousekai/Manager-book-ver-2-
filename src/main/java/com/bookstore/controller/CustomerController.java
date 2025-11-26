package com.bookstore.controller;

import com.bookstore.dao.KhachHangDAO;
import com.bookstore.model.KhachHang;
import com.bookstore.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javafx.scene.control.TableCell;

public class CustomerController {

    @FXML private TableView<KhachHang> customerTable;
    @FXML private TextField searchField;
    @FXML private Button editButton;

    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private ObservableList<KhachHang> masterCustomerList = FXCollections.observableArrayList();
    private DecimalFormat df = new DecimalFormat("#,##0 VND");

    @FXML
    private void initialize() {
        setupTableColumns();
        loadCustomers();
        setupSearchFilter();

        customerTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                editButton.setDisable(newSelection == null || newSelection.getMaKH() == 1);
            }
        );
    }

    private void setupTableColumns() {
        TableColumn<KhachHang, Integer> idCol = new TableColumn<>("Mã KH");
        idCol.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        idCol.prefWidthProperty().bind(customerTable.widthProperty().multiply(0.05)); // 5%

        TableColumn<KhachHang, String> nameCol = new TableColumn<>("Họ Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        nameCol.prefWidthProperty().bind(customerTable.widthProperty().multiply(0.20)); // 20%

        TableColumn<KhachHang, String> phoneCol = new TableColumn<>("Điện thoại");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("dienThoai"));
        phoneCol.prefWidthProperty().bind(customerTable.widthProperty().multiply(0.15)); // 15%

        TableColumn<KhachHang, String> addressCol = new TableColumn<>("Địa chỉ");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        addressCol.prefWidthProperty().bind(customerTable.widthProperty().multiply(0.25)); // 25%
        
        // (ĐÃ XÓA CỘT "Tổng SL Mua")
        
        TableColumn<KhachHang, Double> spendingCol = new TableColumn<>("Tổng Chi tiêu");
        spendingCol.setCellValueFactory(new PropertyValueFactory<>("tongChiTieu"));
        spendingCol.prefWidthProperty().bind(customerTable.widthProperty().multiply(0.15)); // 15%
        
        spendingCol.setCellFactory(column -> {
            return new TableCell<KhachHang, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(df.format(item));
                        setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                    }
                }
            };
        });
        
        TableColumn<KhachHang, String> notesCol = new TableColumn<>("Ghi chú");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));
        notesCol.prefWidthProperty().bind(customerTable.widthProperty().multiply(0.20)); // 20%

        customerTable.getColumns().clear();
        customerTable.getColumns().addAll(idCol, nameCol, phoneCol, addressCol, spendingCol, notesCol);
        
        spendingCol.setSortType(TableColumn.SortType.DESCENDING);
        customerTable.getSortOrder().add(spendingCol);
    }
    
    @FXML
    private void loadCustomers() {
        try {
            masterCustomerList.clear();
            masterCustomerList.addAll(khachHangDAO.getAll());
        } catch (SQLException e) {
            AlertUtil.showError("Lỗi tải Khách hàng: " + e.getMessage());
        }
    }

    private void setupSearchFilter() {
        FilteredList<KhachHang> filteredData = new FilteredList<>(masterCustomerList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (customer.getHoTen().toLowerCase().contains(lowerCaseFilter)) return true;
                if (customer.getDienThoai().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<KhachHang> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sortedData);
    }

    @FXML
    private void handleEditCustomer() {
        KhachHang selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            AlertUtil.showWarning("Vui lòng chọn một khách hàng để sửa.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookstore/view/CustomerEditView.fxml"));
            Parent root = loader.load();
            CustomerEditController editController = loader.getController();
            editController.setCustomer(selectedCustomer);

            Stage stage = new Stage();
            stage.setTitle("Sửa thông tin Khách hàng");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            String iconPath = "/com/bookstore/view/images/iconmain.png";
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
            
            stage.showAndWait();

            if (editController.isDataChanged()) {
                loadCustomers();
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi tải cửa sổ Sửa: " + e.getMessage());
        }
    }
}