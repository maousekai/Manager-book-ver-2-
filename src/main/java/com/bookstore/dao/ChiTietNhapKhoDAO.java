package com.bookstore.dao;

import com.bookstore.model.ChiTietNhapKho;
import com.bookstore.util.DatabaseManager;
import java.sql.*;

public class ChiTietNhapKhoDAO {

    public void insert(ChiTietNhapKho ctn) throws SQLException {
        String sql = "INSERT INTO ChiTietNhapKho (MaHDN, MaSach, MaVPP, SoLuongNhap) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ctn.getMaHDN());

            // Xử lý giá trị NULL cho MaSach hoặc MaVPP
            if (ctn.getMaSach() != null) {
                ps.setInt(2, ctn.getMaSach());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            
            if (ctn.getMaVPP() != null) {
                ps.setInt(3, ctn.getMaVPP());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            ps.setInt(4, ctn.getSoLuongNhap());
            ps.executeUpdate();
        }
    }
}