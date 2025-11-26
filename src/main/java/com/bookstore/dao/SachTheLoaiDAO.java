package com.bookstore.dao;

import com.bookstore.model.SachTheLoai;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SachTheLoaiDAO {
    public List<SachTheLoai> getAll() throws SQLException {
        List<SachTheLoai> list = new ArrayList<>();
        String sql = "SELECT * FROM Sach_TheLoai";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SachTheLoai stl = new SachTheLoai();
                stl.setMaSach(rs.getInt("MaSach"));
                stl.setMaTL(rs.getInt("MaTL"));
                list.add(stl);
            }
        }
        return list;
    }

    public void insert(SachTheLoai stl) throws SQLException {
        String sql = "INSERT INTO Sach_TheLoai (MaSach, MaTL) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stl.getMaSach());
            ps.setInt(2, stl.getMaTL());
            ps.executeUpdate();
        }
    }

    public void delete(int maSach, int maTL) throws SQLException {
        String sql = "DELETE FROM Sach_TheLoai WHERE MaSach = ? AND MaTL = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            ps.setInt(2, maTL);
            ps.executeUpdate();
        }
    }
    
}