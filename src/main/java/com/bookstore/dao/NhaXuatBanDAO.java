package com.bookstore.dao;

import com.bookstore.model.NhaXuatBan;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaXuatBanDAO {
    public List<NhaXuatBan> getAll() throws SQLException {
        List<NhaXuatBan> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaXuatBan";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhaXuatBan nxb = new NhaXuatBan();
                nxb.setMaNXB(rs.getInt("MaNXB"));
                nxb.setTenNXB(rs.getString("TenNXB"));
                nxb.setDiaChi(rs.getString("DiaChi"));
                nxb.setDienThoai(rs.getString("DienThoai"));
                list.add(nxb);
            }
        }
        return list;
    }

    public NhaXuatBan getById(int maNXB) throws SQLException {
        String sql = "SELECT * FROM NhaXuatBan WHERE MaNXB = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNXB);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhaXuatBan nxb = new NhaXuatBan();
                    nxb.setMaNXB(rs.getInt("MaNXB"));
                    nxb.setTenNXB(rs.getString("TenNXB"));
                    nxb.setDiaChi(rs.getString("DiaChi"));
                    nxb.setDienThoai(rs.getString("DienThoai"));
                    return nxb;
                }
            }
        }
        return null;
    }

    public int insertAndGetId(NhaXuatBan nxb) throws SQLException {
        String sql = "INSERT INTO NhaXuatBan (TenNXB, DiaChi, DienThoai) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nxb.getTenNXB());
            ps.setString(2, nxb.getDiaChi());
            ps.setString(3, nxb.getDienThoai());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo NXB thất bại.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    nxb.setMaNXB(newId);
                    return newId; // Trả về ID mới tạo
                } else {
                    throw new SQLException("Tạo NXB thất bại, không lấy được ID.");
                }
            }
        }
    }

    public void update(NhaXuatBan nxb) throws SQLException {
        String sql = "UPDATE NhaXuatBan SET TenNXB = ?, DiaChi = ?, DienThoai = ? WHERE MaNXB = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nxb.getTenNXB());
            ps.setString(2, nxb.getDiaChi());
            ps.setString(3, nxb.getDienThoai());
            ps.setInt(4, nxb.getMaNXB());
            ps.executeUpdate();
        }
    }

    public void delete(int maNXB) throws SQLException {
        String sql = "DELETE FROM NhaXuatBan WHERE MaNXB = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNXB);
            ps.executeUpdate();
        }
    }
}