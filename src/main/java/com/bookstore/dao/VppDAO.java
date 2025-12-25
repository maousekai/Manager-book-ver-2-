package com.bookstore.dao;

import com.bookstore.model.VanPhongPham;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VppDAO {

    public List<VanPhongPham> getAll() throws SQLException {
        List<VanPhongPham> list = new ArrayList<>();
        String sql = "SELECT * FROM VanPhongPham";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VanPhongPham vpp = mapResultSetToVPP(rs);
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
                    return mapResultSetToVPP(rs);
                }
            }
        }
        return null;
    }

    // Hàm phụ để map dữ liệu cho gọn (tránh lặp code)
    private VanPhongPham mapResultSetToVPP(ResultSet rs) throws SQLException {
        VanPhongPham vpp = new VanPhongPham();
        vpp.setMaVPP(rs.getInt("MaVPP"));
        vpp.setTenVPP(rs.getString("TenVPP"));
        vpp.setDonGia(rs.getDouble("DonGia"));
        vpp.setSoLuong(rs.getInt("SoLuong"));
        vpp.setNhaCungCap(rs.getString("NhaCungCap"));
        vpp.setNguongDatHang(rs.getInt("NguongDatHang"));
        vpp.setGhiChu(rs.getString("GhiChu"));
        vpp.setAnh(rs.getString("Anh"));
        vpp.setKhuyenMai(rs.getString("KhuyenMai")); 
        vpp.setMaLoaiVPP(rs.getInt("MaLoaiVPP"));
        return vpp;
    }

    public int insertAndGetId(VanPhongPham vpp) throws SQLException {
        String sql = "INSERT INTO VanPhongPham (TenVPP, DonGia, SoLuong, NhaCungCap, NguongDatHang, GhiChu, Anh, KhuyenMai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, vpp.getTenVPP());
            ps.setDouble(2, vpp.getDonGia());
            ps.setInt(3, vpp.getSoLuong());
            ps.setString(4, vpp.getNhaCungCap());
            ps.setInt(5, vpp.getNguongDatHang());
            ps.setString(6, vpp.getGhiChu());
            ps.setString(7, vpp.getAnh());
            ps.setString(8, vpp.getKhuyenMai()); 
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Tạo VPP thất bại.");

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    vpp.setMaVPP(newId);
                    return newId;
                } else {
                    throw new SQLException("Thất bại lấy ID.");
                }
            }
        }
    }

    public void update(VanPhongPham vpp) throws SQLException {
        String sql = "UPDATE VanPhongPham SET TenVPP = ?, DonGia = ?, SoLuong = ?, NhaCungCap = ?, NguongDatHang = ?, GhiChu = ?, Anh = ?, KhuyenMai = ? WHERE MaVPP = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vpp.getTenVPP());
            ps.setDouble(2, vpp.getDonGia());
            ps.setInt(3, vpp.getSoLuong());
            ps.setString(4, vpp.getNhaCungCap());
            ps.setInt(5, vpp.getNguongDatHang());
            ps.setString(6, vpp.getGhiChu());
            ps.setString(7, vpp.getAnh());
            ps.setString(8, vpp.getKhuyenMai()); 
            ps.setInt(9, vpp.getMaVPP());        
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
    public List<VanPhongPham> getByLoaiVPP(int maLoaiVPP) throws SQLException {
        List<VanPhongPham> list = new ArrayList<>();

        String sql = """
            SELECT * FROM VanPhongPham
            WHERE MaLoaiVPP = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maLoaiVPP);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VanPhongPham vpp = new VanPhongPham();
                vpp.setMaVPP(rs.getInt("MaVPP"));
                vpp.setTenVPP(rs.getString("TenVPP"));
                vpp.setDonGia(rs.getDouble("DonGia"));
                vpp.setSoLuong(rs.getInt("SoLuong"));
                vpp.setNhaCungCap(rs.getString("NhaCungCap"));
                vpp.setNguongDatHang(rs.getInt("NguongDatHang"));
                vpp.setGhiChu(rs.getString("GhiChu"));
                vpp.setAnh(rs.getString("Anh"));
                vpp.setKhuyenMai(rs.getString("KhuyenMai"));
                vpp.setMaLoaiVPP(rs.getInt("MaLoaiVPP"));

                list.add(vpp);
            }
        }
        return list;
    }

}