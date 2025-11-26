package com.bookstore.dao;

import com.bookstore.model.AbstractChiTietHoaDon;
import com.bookstore.model.ChiTietHoaDon;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    public List<ChiTietHoaDon> getAllForHoaDon(int maHD) throws SQLException { 
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHD = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon item = new ChiTietHoaDon();
                    item.setMaHD(rs.getInt("MaHD"));
                    item.setMaSach(rs.getInt("MaSach"));
                    item.setSoLuong(rs.getInt("SoLuong"));
                    item.setDonGia(rs.getDouble("DonGia"));
                    item.setThanhTien(rs.getDouble("ThanhTien")); 
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void insert(ChiTietHoaDon ct) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            insert(ct, conn);
        }
    }

    public void insert(ChiTietHoaDon ct, Connection conn) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ct.getMaHD());
            ps.setInt(2, ct.getMaSach());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
            ps.setDouble(5, ct.getThanhTien());
            ps.executeUpdate();
        }
    }

    public void update(ChiTietHoaDon ct) throws SQLException {
        String sql = "UPDATE ChiTietHoaDon SET SoLuong = ?, DonGia = ?, ThanhTien = ? WHERE MaHD = ? AND MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getDonGia());
            ps.setDouble(3, ct.getThanhTien());
            ps.setInt(4, ct.getMaHD());
            ps.setInt(5, ct.getMaSach());
            ps.executeUpdate();
        }
    }

    public void delete(int maHD, int maSach) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHD = ? AND MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ps.setInt(2, maSach);
            ps.executeUpdate();
        }
    }
}