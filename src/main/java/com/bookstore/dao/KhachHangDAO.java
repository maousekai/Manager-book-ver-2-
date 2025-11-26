package com.bookstore.dao;

import com.bookstore.model.KhachHang;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    public List<KhachHang> getAll() throws SQLException {
        List<KhachHang> list = new ArrayList<>();  
        String sql = "SELECT * FROM KhachHang";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getInt("MaKH"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setDiaChi(rs.getString("DiaChi"));
                kh.setDienThoai(rs.getString("DienThoai"));
                kh.setGhiChu(rs.getString("GhiChu")); 
                kh.setTongChiTieu(rs.getDouble("TongChiTieu"));
                list.add(kh);
            }
        }
        return list;
    }

    public KhachHang getById(int maKH) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getInt("MaKH"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setDiaChi(rs.getString("DiaChi"));
                    kh.setDienThoai(rs.getString("DienThoai"));
                    kh.setGhiChu(rs.getString("GhiChu"));
                    return kh;
                }
            }
        }
        return null;
    }

    public int insertAndGetId(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO KhachHang (HoTen, DiaChi, DienThoai, GhiChu) VALUES (?, ?, ?, ?)";
        
        // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy ID
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getDiaChi());
            ps.setString(3, kh.getDienThoai());
            ps.setString(4, kh.getGhiChu());
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Tạo khách hàng thất bại, không có hàng nào được thêm.");
            }

            // Lấy MaKH vừa được tạo
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về MaKH
                } else {
                    throw new SQLException("Tạo khách hàng thất bại, không lấy được ID.");
                }
            }
        }
    }
    public KhachHang findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE DienThoai = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getInt("MaKH"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setDiaChi(rs.getString("DiaChi"));
                    kh.setDienThoai(rs.getString("DienThoai"));
                    kh.setGhiChu(rs.getString("GhiChu"));
                    kh.setTongChiTieu(rs.getDouble("TongChiTieu"));
                    return kh;
                }
            }
        }
        return null; // Không tìm thấy
    }
    public void update(KhachHang kh) throws SQLException {
        String sql = "UPDATE KhachHang SET HoTen = ?, DiaChi = ?, DienThoai = ?, GhiChu = ? WHERE MaKH = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getDiaChi());
            ps.setString(3, kh.getDienThoai());
            ps.setString(4, kh.getGhiChu());
            ps.setInt(5, kh.getMaKH());
            ps.executeUpdate();
        }
    }

    public void delete(int maKH) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maKH);
            ps.executeUpdate();
        }
    }
}