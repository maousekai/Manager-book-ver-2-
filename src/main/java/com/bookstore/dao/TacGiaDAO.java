package com.bookstore.dao;

import com.bookstore.model.TacGia;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacGiaDAO {
    public List<TacGia> getAll() throws SQLException {
        List<TacGia> list = new ArrayList<>();
        String sql = "SELECT * FROM TacGia";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TacGia tg = new TacGia();
                tg.setMaTG(rs.getInt("MaTG"));
                tg.setTenTG(rs.getString("TenTG"));
                tg.setGhiChu(rs.getString("GhiChu"));
                list.add(tg);
            }
        }
        return list;
    }

    public TacGia getById(int maTG) throws SQLException {
        String sql = "SELECT * FROM TacGia WHERE MaTG = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTG);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TacGia tg = new TacGia();
                    tg.setMaTG(rs.getInt("MaTG"));
                    tg.setTenTG(rs.getString("TenTG"));
                    tg.setGhiChu(rs.getString("GhiChu"));
                    return tg;
                }
            }
        }
        return null;
    }

    public void insert(TacGia tg) throws SQLException {
        String sql = "INSERT INTO TacGia (TenTG, GhiChu) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tg.getTenTG());
            ps.setString(2, tg.getGhiChu());
            ps.executeUpdate();
        }
    }

    public void update(TacGia tg) throws SQLException {
        String sql = "UPDATE TacGia SET TenTG = ?, GhiChu = ? WHERE MaTG = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tg.getTenTG());
            ps.setString(2, tg.getGhiChu());
            ps.setInt(3, tg.getMaTG());
            ps.executeUpdate();
        }
    }

    public void delete(int maTG) throws SQLException {
        String sql = "DELETE FROM TacGia WHERE MaTG = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTG);
            ps.executeUpdate();
        }
    }
}