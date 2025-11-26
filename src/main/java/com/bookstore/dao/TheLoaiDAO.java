package com.bookstore.dao;

import com.bookstore.model.GenreStats;
import com.bookstore.model.TheLoai;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TheLoaiDAO {
    public List<TheLoai> getAll() throws SQLException {
        List<TheLoai> list = new ArrayList<>();
        String sql = "SELECT * FROM TheLoai";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TheLoai tl = new TheLoai();
                tl.setMaTL(rs.getInt("MaTL"));
                tl.setTenTL(rs.getString("TenTL"));
                list.add(tl);
            }
        }
        return list;
    }

    public TheLoai getById(int maTL) throws SQLException {
        String sql = "SELECT * FROM TheLoai WHERE MaTL = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTL);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TheLoai tl = new TheLoai();
                    tl.setMaTL(rs.getInt("MaTL"));
                    tl.setTenTL(rs.getString("TenTL"));
                    return tl;
                }
            }
        }
        return null;
    }

    public int insertAndGetId(TheLoai tl) throws SQLException {
        String sql = "INSERT INTO TheLoai (TenTL) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, tl.getTenTL());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo Thể loại thất bại.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    tl.setMaTL(newId); // Cập nhật lại MaTL cho đối tượng
                    return newId; // Trả về MaTL
                } else {
                    throw new SQLException("Tạo Thể loại thất bại, không lấy được ID.");
                }
            }
        }
    }
    public void update(TheLoai tl) throws SQLException {
        String sql = "UPDATE TheLoai SET TenTL = ? WHERE MaTL = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tl.getTenTL());
            ps.setInt(2, tl.getMaTL());
            ps.executeUpdate();
        }
    }

    public void delete(int maTL) throws SQLException {
        String sql = "DELETE FROM TheLoai WHERE MaTL = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTL);
            ps.executeUpdate();
        }
    }
    // Thống kê 5 thể loại bán chạy nhất trong khoảng thời gian
    public List<GenreStats> getGenreStats(LocalDate start, LocalDate end) throws SQLException {
        List<GenreStats> list = new ArrayList<>();
        String sql = "SELECT TOP 5 tl.TenTL, SUM(ct.SoLuong) as SoLuongBan " +
                     "FROM TheLoai tl " +
                     "JOIN Sach_TheLoai stl ON tl.MaTL = stl.MaTL " +
                     "JOIN ChiTietHoaDon ct ON stl.MaSach = ct.MaSach " +
                     "JOIN HoaDon h ON ct.MaHD = h.MaHD " +
                     "WHERE h.NgayLap BETWEEN ? AND ? " +
                     "GROUP BY tl.TenTL " +
                     "ORDER BY SoLuongBan DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GenreStats gs = new GenreStats();
                    gs.setTenTheLoai(rs.getString("TenTL"));
                    gs.setSoLuongBan(rs.getInt("SoLuongBan"));
                    list.add(gs);
                }
            }
        }
        return list;
    }
}