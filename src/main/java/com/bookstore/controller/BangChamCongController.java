package com.bookstore.controller;

import com.bookstore.dao.BangChamCongDAO;
import com.bookstore.dao.NhanVienDAO;
import com.bookstore.model.BangChamCong;
import com.bookstore.model.NhanVien;
import com.bookstore.util.AlertUtil;
import com.bookstore.util.AuthManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BangChamCongController {

    @FXML private DatePicker ngayLamPicker;
    @FXML private TextField gioVaoField;
    @FXML private TextField gioRaField;

    @FXML private TableView<BangChamCong> bangChamCongTable;
    @FXML private TableColumn<BangChamCong, String> colNhanVien;
    @FXML private TableColumn<BangChamCong, LocalDate> colNgay;
    @FXML private TableColumn<BangChamCong, LocalTime> colGioVao;
    @FXML private TableColumn<BangChamCong, LocalTime> colGioRa;
    @FXML private TableColumn<BangChamCong, Double> colGioLam;

    private final BangChamCongDAO chamCongDAO = new BangChamCongDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Map<Integer, String> nhanVienMap = new HashMap<>();
    private int maNV;

    @FXML
    private void initialize() {
        this.maNV = Integer.parseInt(AuthManager.getCurrentUserId());

        setupTable();
        loadChamCong();
        bangChamCongTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try {
            loadNhanVienMap();
        } catch (SQLException e) {
            AlertUtil.showError("Không tải được dữ liệu nhân viên");
        }
    }
    private NhanVien getCurrentNhanVien() {
        try {
            int maNV = Integer.parseInt(AuthManager.getCurrentUserId());
            return nhanVienDAO.getById(maNV);
        } catch (Exception e) {
            return null;
        }
    }
    private void setupTable() {
        colNhanVien.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getMaNV() + ""
            )
        );
        colNgay.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("ngayLam"));
        colGioVao.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gioVao"));
        colGioRa.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gioRa"));
        colGioLam.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gioLam"));
        colNhanVien.setCellFactory(column -> new TableCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					try {
						int maNV = Integer.parseInt(item);
						NhanVien nv = nhanVienDAO.getById(maNV);
						setText(nv != null ? nv.getTenNV() : "N/A");
					} catch (SQLException e) {
						setText("Lỗi");
					}
				}
			}
		});
    }
    private void loadChamCong() {
        try {
            int role = AuthManager.getCurrentRole();
            int maNV = Integer.parseInt(AuthManager.getCurrentUserId());

            if (role == 1) {
                bangChamCongTable.setItems(
                    FXCollections.observableArrayList(chamCongDAO.getAll())
                );
            } else {
                bangChamCongTable.setItems(
                    FXCollections.observableArrayList(chamCongDAO.getByNhanVien(maNV))
                );
            }
        } catch (Exception e) {
            AlertUtil.showError("Lỗi tải chấm công: " + e.getMessage());
        }
    }
    @FXML
    private void handleCheckIn() {
        try {
            BangChamCong dangMo =
                chamCongDAO.getChamCongHomNayDangMo(maNV);

            if (dangMo != null) {
                AlertUtil.showWarning("Bạn đã chấm công vào hôm nay rồi");
                return;
            }

            BangChamCong cc = new BangChamCong();
            cc.setMaNV(maNV);
            cc.setMaCa(1);
            cc.setNgayLam(LocalDate.now());
            cc.setGioVao(LocalTime.now());
            cc.setTrangThai("Đang làm");

            chamCongDAO.insert(cc);
            loadChamCong();
            AlertUtil.showInfo("Chấm công vào thành công");

        } catch (SQLException e) {
            AlertUtil.showError("Lỗi chấm công vào: " + e.getMessage());
        }
    }
    @FXML
    private void handleCheckOut() {
        try {
            BangChamCong dangMo =
                chamCongDAO.getChamCongHomNayDangMo(maNV);

            if (dangMo == null) {
                AlertUtil.showWarning("Bạn chưa chấm công vào hôm nay");
                return;
            }

            chamCongDAO.updateGioLam(
                dangMo.getMaChamCong(),
                dangMo.getGioVao(),
                LocalTime.now()
            );

            loadChamCong();
            AlertUtil.showInfo("Chấm công ra thành công");

        } catch (SQLException e) {
            AlertUtil.showError("Lỗi chấm công ra: " + e.getMessage());
        }
    }

    private void loadNhanVienMap() throws SQLException {
        for (NhanVien nv : nhanVienDAO.getAll()) {
            nhanVienMap.put(nv.getMaNV(), nv.getTenNV());
        }
    }
   

}
