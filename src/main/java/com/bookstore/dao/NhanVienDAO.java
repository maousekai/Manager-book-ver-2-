package com.bookstore.dao;

import com.bookstore.model.NhanVien;
import com.bookstore.util.DatabaseManager;
import com.bookstore.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
	public List<NhanVien> getAll() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        }
        return list;
    }

	public NhanVien getById(int maNV) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        }
        return null;
    }

	public void insert(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (TenNV, NgaySinh, DiaChi, DienThoai, TaiKhoan, MatKhau, NgayVaoLam, MaQuyen, ViTri, LuongCoBan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getTenNV());
            ps.setDate(2, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString(3, nv.getDiaChi());
            ps.setString(4, nv.getDienThoai());
            ps.setString(5, nv.getTaiKhoan());
            ps.setString(6, PasswordUtil.hashPassword(nv.getMatKhau()));
            ps.setDate(7, nv.getNgayVaoLam() != null ? Date.valueOf(nv.getNgayVaoLam()) : null);
            ps.setInt(8, nv.getMaQuyen());
            ps.setString(9, nv.getViTri());
            ps.setDouble(10, nv.getLuongCoBan());
            ps.executeUpdate();
        }
    }

	public void update(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET TenNV = ?, NgaySinh = ?, DiaChi = ?, DienThoai = ?, TaiKhoan = ?, MatKhau = ?, NgayVaoLam = ?, MaQuyen = ?, ViTri = ?, LuongCoBan = ? WHERE MaNV = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getTenNV());
            ps.setDate(2, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString(3, nv.getDiaChi());
            ps.setString(4, nv.getDienThoai());
            ps.setString(5, nv.getTaiKhoan());
            
            if (!nv.getMatKhau().startsWith("$2a$")) {
                ps.setString(6, PasswordUtil.hashPassword(nv.getMatKhau()));
            } else {
                ps.setString(6, nv.getMatKhau());
            }
            
            ps.setDate(7, nv.getNgayVaoLam() != null ? Date.valueOf(nv.getNgayVaoLam()) : null);
            ps.setInt(8, nv.getMaQuyen());
            ps.setString(9, nv.getViTri());
            ps.setDouble(10, nv.getLuongCoBan()); 
            ps.setInt(11, nv.getMaNV());
            ps.executeUpdate();
        }
    }

    public void delete(int maNV) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
            ps.executeUpdate();
        }
    }

    public NhanVien login(String taiKhoan, String matKhau) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE TaiKhoan = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = mapResultSetToNhanVien(rs);
                    boolean match = PasswordUtil.checkPassword(matKhau, nv.getMatKhau());
                    System.out.println("Debug: TaiKhoan=" + taiKhoan + ", Entered=" + matKhau + ", StoredHash=" + nv.getMatKhau() + ", Match=" + match); // Debug, xóa sau
                    if (match) return nv;
                }
            }
        }
        return null;
    }

    public void resetPassword(String taiKhoan, String newPlainPassword) throws SQLException {
        String hashed = PasswordUtil.hashPassword(newPlainPassword);
        String sql = "UPDATE NhanVien SET MatKhau = ? WHERE TaiKhoan = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setString(2, taiKhoan);
            ps.executeUpdate();
        }
    }

    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getInt("MaNV"));
        nv.setTenNV(rs.getString("TenNV"));
        nv.setNgaySinh(rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null);
        nv.setDiaChi(rs.getString("DiaChi"));
        nv.setDienThoai(rs.getString("DienThoai"));
        nv.setTaiKhoan(rs.getString("TaiKhoan"));
        nv.setMatKhau(rs.getString("MatKhau"));
        nv.setNgayVaoLam(rs.getDate("NgayVaoLam") != null ? rs.getDate("NgayVaoLam").toLocalDate() : null);
        nv.setMaQuyen(rs.getInt("MaQuyen"));
        nv.setViTri(rs.getString("ViTri"));
        nv.setLuongCoBan(rs.getDouble("LuongCoBan"));
        return nv;
    }
}