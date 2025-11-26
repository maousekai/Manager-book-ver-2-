package com.bookstore.dao;

import com.bookstore.model.SachTacGia;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SachTacGiaDAO {
    public List<SachTacGia> getAll() throws SQLException {
        List<SachTacGia> list = new ArrayList<>();
        String sql = "SELECT * FROM Sach_TacGia";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SachTacGia stg = new SachTacGia();
                stg.setMaSach(rs.getInt("MaSach"));
                stg.setMaTG(rs.getInt("MaTG"));
                list.add(stg);
            }
        }
        return list;
    }

    public void insert(SachTacGia stg) throws SQLException {
        String sql = "INSERT INTO Sach_TacGia (MaSach, MaTG) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stg.getMaSach());
            ps.setInt(2, stg.getMaTG());
            ps.executeUpdate();
        }
    }

    public void delete(int maSach, int maTG) throws SQLException {
        String sql = "DELETE FROM Sach_TacGia WHERE MaSach = ? AND MaTG = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            ps.setInt(2, maTG);
            ps.executeUpdate();
        }
    }
}