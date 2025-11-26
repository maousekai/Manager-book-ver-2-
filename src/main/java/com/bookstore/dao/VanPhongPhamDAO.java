package com.bookstore.dao;

import com.bookstore.model.VanPhongPham;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VanPhongPhamDAO {
	public List<VanPhongPham> getAll() throws SQLException {
        List<VanPhongPham> list = new ArrayList<>();
        String sql = "SELECT * FROM VanPhongPham";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VanPhongPham vpp = new VanPhongPham();
                vpp.setMaVPP(rs.getInt("MaVPP"));
                vpp.setTenVPP(rs.getString("TenVPP"));
                vpp.setDonGia(rs.getDouble("DonGia"));
                vpp.setSoLuong(rs.getInt("SoLuong"));
                vpp.setNhaCungCap(rs.getString("NhaCungCap"));
                vpp.setNguongDatHang(rs.getInt("NguongDatHang"));
                vpp.setGhiChu(rs.getString("GhiChu"));
                vpp.setAnh(rs.getString("Anh")); // THÊM DÒNG NÀY
                list.add(vpp);
            }
        }
        return list;
    }

	public VanPhongPham getById(int maVPP) throws SQLException {
        String sql = "SELECT * FROM VanPhongPham WHERE MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maVPP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VanPhongPham vpp = new VanPhongPham();
                    vpp.setMaVPP(rs.getInt("MaVPP"));
                    vpp.setTenVPP(rs.getString("TenVPP"));
                    vpp.setDonGia(rs.getDouble("DonGia"));
                    vpp.setSoLuong(rs.getInt("SoLuong"));
                    vpp.setNhaCungCap(rs.getString("NhaCungCap"));
                    vpp.setNguongDatHang(rs.getInt("NguongDatHang"));
                    vpp.setGhiChu(rs.getString("GhiChu"));
                    vpp.setAnh(rs.getString("Anh")); // THÊM DÒNG NÀY
                    return vpp;
                }
            }
        }
        return null;
    }

	public int insertAndGetId(VanPhongPham vpp) throws SQLException {
        String sql = "INSERT INTO VanPhongPham (TenVPP, DonGia, SoLuong, NhaCungCap, NguongDatHang, GhiChu, Anh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, vpp.getTenVPP());
            ps.setDouble(2, vpp.getDonGia());
            ps.setInt(3, vpp.getSoLuong());
            ps.setString(4, vpp.getNhaCungCap());
            ps.setInt(5, vpp.getNguongDatHang());
            ps.setString(6, vpp.getGhiChu());
            ps.setString(7, vpp.getAnh());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo VPP thất bại, không hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    vpp.setMaVPP(newId); // Cập nhật lại MaVPP cho đối tượng
                    return newId; // Trả về MaVPP
                } else {
                    throw new SQLException("Tạo VPP thất bại, không lấy được ID.");
                }
            }
        }
    }

	public void update(VanPhongPham vpp) throws SQLException {
        // Thêm "Anh = ?"
        String sql = "UPDATE VanPhongPham SET TenVPP = ?, DonGia = ?, SoLuong = ?, NhaCungCap = ?, NguongDatHang = ?, GhiChu = ?, Anh = ? WHERE MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vpp.getTenVPP());
            ps.setDouble(2, vpp.getDonGia());
            ps.setInt(3, vpp.getSoLuong());
            ps.setString(4, vpp.getNhaCungCap());
            ps.setInt(5, vpp.getNguongDatHang());
            ps.setString(6, vpp.getGhiChu());
            ps.setString(7, vpp.getAnh());
            ps.setInt(8, vpp.getMaVPP());
            ps.executeUpdate();
        }
    }

    public void delete(int maVPP) throws SQLException {
        String sql = "DELETE FROM VanPhongPham WHERE MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maVPP);
            ps.executeUpdate();
        }
    }
    public VanPhongPham findExactlyByName(String tenVPP) throws SQLException {
        String sql = "SELECT * FROM VanPhongPham WHERE TenVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenVPP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VanPhongPham vpp = new VanPhongPham();
                    vpp.setMaVPP(rs.getInt("MaVPP"));
                    vpp.setTenVPP(rs.getString("TenVPP"));
                    vpp.setDonGia(rs.getDouble("DonGia"));
                    vpp.setSoLuong(rs.getInt("SoLuong"));
                    return vpp;
                }
            }
        }
        return null; // Không tìm thấy
    }
    public void addStock(int maVPP, int soLuongThem) throws SQLException {
        String sql = "UPDATE VanPhongPham SET SoLuong = SoLuong + ? WHERE MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuongThem);
            ps.setInt(2, maVPP);
            ps.executeUpdate();
        }
    }
}