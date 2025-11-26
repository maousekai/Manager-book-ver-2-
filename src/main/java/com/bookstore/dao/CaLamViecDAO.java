package com.bookstore.dao;

import com.bookstore.model.CaLamViec;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CaLamViecDAO {
    public List<CaLamViec> getAll() throws SQLException {
        List<CaLamViec> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLamViec";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CaLamViec ca = new CaLamViec();
                ca.setMaCa(rs.getInt("MaCa"));
                ca.setTenCa(rs.getString("TenCa"));
                ca.setGioBatDau(rs.getTime("GioBatDau") != null ? rs.getTime("GioBatDau").toLocalTime() : null);
                ca.setGioKetThuc(rs.getTime("GioKetThuc") != null ? rs.getTime("GioKetThuc").toLocalTime() : null);
                ca.setGhiChu(rs.getString("GhiChu"));
                list.add(ca);
            }
        }
        return list;
    }

    public void insert(CaLamViec ca) throws SQLException {
        String sql = "INSERT INTO CaLamViec (TenCa, GioBatDau, GioKetThuc, GhiChu) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ca.getTenCa());
            ps.setTime(2, ca.getGioBatDau() != null ? Time.valueOf(ca.getGioBatDau()) : null);
            ps.setTime(3, ca.getGioKetThuc() != null ? Time.valueOf(ca.getGioKetThuc()) : null);
            ps.setString(4, ca.getGhiChu());
            ps.executeUpdate();
        }
    }
    public void update(CaLamViec ca) throws SQLException {
		String sql = "UPDATE CaLamViec SET TenCa = ?, GioBatDau = ?, GioKetThuc = ?, GhiChu = ? WHERE MaCa = ?";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ca.getTenCa());
			ps.setTime(2, ca.getGioBatDau() != null ? Time.valueOf(ca.getGioBatDau()) : null);
			ps.setTime(3, ca.getGioKetThuc() != null ? Time.valueOf(ca.getGioKetThuc()) : null);
			ps.setString(4, ca.getGhiChu());
			ps.setInt(5, ca.getMaCa());
			ps.executeUpdate();
		}
		
    }
    public void delete(int maCa)  throws SQLException {
    			String sql = "DELETE FROM CaLamViec WHERE MaCa = ?";
    			try ( Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
    				ps.setInt(1, maCa);
    				ps.executeUpdate();
    			}
    }
    public void getbyId(int maCa) throws SQLException {
    			String sql = "SELECT * FROM CaLamViec WHERE MaCa = ?";
    			try (Connection conn = DatabaseManager.getConnection();
					 PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, maCa);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							CaLamViec ca = new CaLamViec();
							ca.setMaCa(rs.getInt("MaCa"));
							ca.setTenCa(rs.getString("TenCa"));
							ca.setGioBatDau(rs.getTime("GioBatDau") != null ? rs.getTime("GioBatDau").toLocalTime() : null);
							ca.setGioKetThuc(rs.getTime("GioKetThuc") != null ? rs.getTime("GioKetThuc").toLocalTime() : null);
							ca.setGhiChu(rs.getString("GhiChu"));
						}
					}
				} }
}