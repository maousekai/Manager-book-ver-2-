package com.bookstore.dao;

import com.bookstore.model.HoaDonNhapKho;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonNhapKhoDAO {
    public int insertAndGetId(HoaDonNhapKho hdn) throws SQLException {
        String sql = "INSERT INTO HoaDonNhapKho (MaNV, NgayNhap, TongSoLuong, GhiChu) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, hdn.getMaNV());
            ps.setDate(2, Date.valueOf(hdn.getNgayNhap()));
            ps.setInt(3, hdn.getTongSoLuong());
            ps.setString(4, hdn.getGhiChu());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo HĐ Nhập kho thất bại.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    hdn.setMaHDN(newId);
                    return newId;
                } else {
                    throw new SQLException("Tạo HĐ Nhập kho thất bại, không lấy được ID.");
                }
            }
        }
    }

    public List<HoaDonNhapKho> getAll() throws SQLException {
        List<HoaDonNhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDonNhapKho ORDER BY NgayNhap DESC"; // Sắp xếp mới nhất lên đầu
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HoaDonNhapKho hdn = new HoaDonNhapKho();
                hdn.setMaHDN(rs.getInt("MaHDN"));
                hdn.setMaNV(rs.getInt("MaNV"));
                hdn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
                hdn.setTongSoLuong(rs.getInt("TongSoLuong"));
                hdn.setGhiChu(rs.getString("GhiChu"));
                list.add(hdn);
            }
        }
        return list;
    }

    public List<HoaDonNhapKho> getAllByDateRange(LocalDate start, LocalDate end) throws SQLException {
        List<HoaDonNhapKho> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDonNhapKho WHERE NgayNhap BETWEEN ? AND ? ORDER BY NgayNhap DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDonNhapKho hdn = new HoaDonNhapKho();
                    hdn.setMaHDN(rs.getInt("MaHDN"));
                    hdn.setMaNV(rs.getInt("MaNV"));
                    hdn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
                    hdn.setTongSoLuong(rs.getInt("TongSoLuong"));
                    hdn.setGhiChu(rs.getString("GhiChu"));
                    list.add(hdn);
                }
            }
        }
        return list;
    }
}