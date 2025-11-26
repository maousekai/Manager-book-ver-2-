package com.bookstore.dao;

import com.bookstore.model.ChiTietHoaDonVPP;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonVPPDAO {
    public List<ChiTietHoaDonVPP> getAllForHoaDon(int maHD) throws SQLException {
        List<ChiTietHoaDonVPP> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDonVPP WHERE MaHD = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDonVPP item = new ChiTietHoaDonVPP();
                    item.setMaHD(rs.getInt("MaHD"));
                    item.setMaVPP(rs.getInt("MaVPP"));
                    item.setSoLuong(rs.getInt("SoLuong"));
                    item.setDonGia(rs.getDouble("DonGia"));
                    item.setThanhTien(rs.getDouble("ThanhTien"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void insert(ChiTietHoaDonVPP ct) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            insert(ct, conn);
        }
    }

    public void insert(ChiTietHoaDonVPP ct, Connection conn) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDonVPP (MaHD, MaVPP, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ct.getMaHD());
            ps.setInt(2, ct.getMaVPP());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
            ps.setDouble(5, ct.getThanhTien());
            ps.executeUpdate();
        }
    }

    public void update(ChiTietHoaDonVPP ct) throws SQLException {
        String sql = "UPDATE ChiTietHoaDonVPP SET SoLuong = ?, DonGia = ?, ThanhTien = ? WHERE MaHD = ? AND MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getDonGia());
            ps.setDouble(3, ct.getThanhTien());
            ps.setInt(4, ct.getMaHD());
            ps.setInt(5, ct.getMaVPP());
            ps.executeUpdate();
        }
    }

    public void delete(int maHD, int maVPP) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDonVPP WHERE MaHD = ? AND MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ps.setInt(2, maVPP);
            ps.executeUpdate();
        }
    }
}