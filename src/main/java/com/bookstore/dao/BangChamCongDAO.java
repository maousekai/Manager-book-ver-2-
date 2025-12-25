package com.bookstore.dao;

import com.bookstore.model.BangChamCong;
import com.bookstore.model.NhanVien;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BangChamCongDAO {
    public List<BangChamCong> getAll() throws SQLException {
		List<BangChamCong> list = new ArrayList<>();
		String sql = "SELECT * FROM BangChamCong";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				BangChamCong cc = new BangChamCong();
				cc.setMaChamCong(rs.getInt("MaChamCong"));
				cc.setMaNV(rs.getInt("MaNV"));
				cc.setMaCa(rs.getInt("MaCa"));
				cc.setNgayLam(rs.getDate("NgayLam") != null ? rs.getDate("NgayLam").toLocalDate() : null);
				cc.setGioVao(rs.getTime("GioVao") != null ? rs.getTime("GioVao").toLocalTime() : null);
				cc.setGioRa(rs.getTime("GioRa") != null ? rs.getTime("GioRa").toLocalTime() : null);
				cc.setTrangThai(rs.getString("TrangThai"));
				cc.setGioLam(rs.getDouble("GioLam"));
				cc.setGhiChu(rs.getString("GhiChu"));
				list.add(cc);
			}
		}
		return list;
    }

    public void insert(BangChamCong cc) throws SQLException {
        String sql = "INSERT INTO BangChamCong (MaNV, MaCa, NgayLam, GioVao, GioRa, TrangThai, GioLam, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cc.getMaNV());
            ps.setInt(2, cc.getMaCa());
            ps.setDate(3, Date.valueOf(cc.getNgayLam()));
            ps.setTime(4, cc.getGioVao() != null ? Time.valueOf(cc.getGioVao()) : null);
            ps.setTime(5, cc.getGioRa() != null ? Time.valueOf(cc.getGioRa()) : null);
            ps.setString(6, cc.getTrangThai());
            double gioLam = calculateGioLam(cc.getGioVao(), cc.getGioRa());
            ps.setDouble(7, gioLam);
            ps.setString(8, cc.getGhiChu());
            ps.executeUpdate();
        }
    }

    private double calculateGioLam(LocalTime gioVao, LocalTime gioRa) {
        if (gioVao == null || gioRa == null) return 0;
        return Duration.between(gioVao, gioRa).toMinutes() / 60.0;
    }
    
    public void updateGioLam(int maChamCong, LocalTime gioVao, LocalTime gioRa) throws SQLException {
		String sql = "UPDATE BangChamCong SET GioVao = ?, GioRa = ?, GioLam = ? WHERE MaChamCong = ?";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setTime(1, gioVao != null ? Time.valueOf(gioVao) : null);
			ps.setTime(2, gioRa != null ? Time.valueOf(gioRa) : null);
			double gioLam = calculateGioLam(gioVao, gioRa);
			ps.setDouble(3, gioLam);
			ps.setInt(4, maChamCong);
			ps.executeUpdate();
		}
	}
    public BangChamCong getById(int maChamCong) throws SQLException {
		String sql = "SELECT * FROM BangChamCong WHERE MaChamCong = ?";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, maChamCong);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					BangChamCong cc = new BangChamCong();
					cc.setMaChamCong(rs.getInt("MaChamCong"));
					cc.setMaNV(rs.getInt("MaNV"));
					cc.setMaCa(rs.getInt("MaCa"));
					cc.setNgayLam(rs.getDate("NgayLam") != null ? rs.getDate("NgayLam").toLocalDate() : null);
					cc.setGioVao(rs.getTime("GioVao") != null ? rs.getTime("GioVao").toLocalTime() : null);
					cc.setGioRa(rs.getTime("GioRa") != null ? rs.getTime("GioRa").toLocalTime() : null);
					cc.setTrangThai(rs.getString("TrangThai"));
					cc.setGioLam(rs.getDouble("GioLam"));
					cc.setGhiChu(rs.getString("GhiChu"));
					return cc;
				}
			}
		}
		return null;
	}
    public void delete(int maChamCong) throws SQLException {
    			String sql = "DELETE FROM BangChamCong WHERE MaChamCong = ?";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, maChamCong);
			ps.executeUpdate();
		}
    }
    public void update(BangChamCong cc) throws SQLException {
		String sql = "UPDATE BangChamCong SET MaNV = ?, MaCa = ?, NgayLam = ?, GioVao = ?, GioRa = ?, TrangThai = ?, GioLam = ?, GhiChu = ? WHERE MaChamCong = ?";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cc.getMaNV());
			ps.setInt(2, cc.getMaCa());
			ps.setDate(3, Date.valueOf(cc.getNgayLam()));
			ps.setTime(4, cc.getGioVao() != null ? Time.valueOf(cc.getGioVao()) : null);
			ps.setTime(5, cc.getGioRa() != null ? Time.valueOf(cc.getGioRa()) : null);
			ps.setString(6, cc.getTrangThai());
			double gioLam = calculateGioLam(cc.getGioVao(), cc.getGioRa());
			ps.setDouble(7, gioLam);
			ps.setString(8, cc.getGhiChu());
			ps.setInt(9, cc.getMaChamCong());
			ps.executeUpdate();
		}
	}
    public double tinhLuongThang(int maNV, int thang, int nam, double luongGio)
            throws SQLException {

        String sql = """
            SELECT SUM(GioLam)
            FROM BangChamCong
            WHERE MaNV = ?
            AND MONTH(NgayLam) = ?
            AND YEAR(NgayLam) = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            ps.setInt(2, thang);
            ps.setInt(3, nam);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1) * luongGio;
            }
        }
        return 0;
    }
    public List<BangChamCong> getAllWithTenNV() throws SQLException {
        List<BangChamCong> list = new ArrayList<>();

        String sql = """
            SELECT b.*, n.TenNV
            FROM BangChamCong b
            JOIN NhanVien n ON b.MaNV = n.MaNV
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BangChamCong cc = new BangChamCong();
                cc.setMaChamCong(rs.getInt("MaChamCong"));
                cc.setMaNV(rs.getInt("MaNV"));
                cc.setTenNV(rs.getString("TenNV")); 
                cc.setNgayLam(rs.getDate("NgayLam").toLocalDate());
                cc.setGioVao(rs.getTime("GioVao") != null ? rs.getTime("GioVao").toLocalTime() : null);
                cc.setGioRa(rs.getTime("GioRa") != null ? rs.getTime("GioRa").toLocalTime() : null);
                cc.setGioLam(rs.getDouble("GioLam"));
                list.add(cc);
            }
        }
        return list;
    }
    public List<BangChamCong> getByNhanVien(int maNV) throws SQLException {
        List<BangChamCong> list = new ArrayList<>();
        String sql = """
            SELECT cc.*, nv.TenNV
            FROM BangChamCong cc
            JOIN NhanVien nv ON cc.MaNV = nv.MaNV
            WHERE cc.MaNV = ?
            ORDER BY NgayLam DESC, GioVao DESC
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BangChamCong cc = new BangChamCong();
                cc.setMaNV(rs.getInt("MaNV"));
                cc.setTenNV(rs.getString("TenNV"));
                cc.setNgayLam(rs.getDate("NgayLam").toLocalDate());
                cc.setGioVao(rs.getTime("GioVao") != null ? rs.getTime("GioVao").toLocalTime() : null);
                cc.setGioRa(rs.getTime("GioRa") != null ? rs.getTime("GioRa").toLocalTime() : null);
                cc.setGioLam(rs.getDouble("GioLam"));
                list.add(cc);
            }
        }
        return list;
    }
    public double tongGioLamTheoThang(int maNV, int thang, int nam) throws SQLException {
        String sql = """
            SELECT SUM(GioLam)
            FROM BangChamCong
            WHERE MaNV = ?
              AND MONTH(NgayLam) = ?
              AND YEAR(NgayLam) = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            ps.setInt(2, thang);
            ps.setInt(3, nam);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }
    public double tinhLuongThang(int maNV, int thang, int nam) throws SQLException {
        double tongGio = tongGioLamTheoThang(maNV, thang, nam);

        double LUONG_GIO = 20000;

        return tongGio * LUONG_GIO;
    }

    public BangChamCong getChamCongHomNayDangMo(int maNV) throws SQLException {
        String sql = """
            SELECT *
            FROM BangChamCong
            WHERE MaNV = ?
              AND NgayLam = ?
              AND GioRa IS NULL
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BangChamCong cc = new BangChamCong();
                cc.setMaChamCong(rs.getInt("MaChamCong"));
                cc.setMaNV(rs.getInt("MaNV"));
                cc.setNgayLam(rs.getDate("NgayLam").toLocalDate());
                cc.setGioVao(rs.getTime("GioVao").toLocalTime());
                return cc;
            }
        }
        return null;
    }
    public void resetGioLamSauKhiTraLuong(int maNV, int thang, int nam)
            throws SQLException {

        String sql = """
            UPDATE BangChamCong
            SET GioLam = 0
            WHERE MaNV = ?
              AND MONTH(NgayLam) = ?
              AND YEAR(NgayLam) = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ps.executeUpdate();
        }
    }

}