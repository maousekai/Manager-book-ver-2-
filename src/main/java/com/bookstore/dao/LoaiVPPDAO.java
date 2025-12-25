package com.bookstore.dao;

import com.bookstore.model.LoaiVPP;
import com.bookstore.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiVPPDAO {

    public List<LoaiVPP> getAll() throws SQLException {
        List<LoaiVPP> list = new ArrayList<>();

        String sql = "SELECT MaLoaiVPP, TenLoai FROM LoaiVPP";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new LoaiVPP(
                    rs.getInt("MaLoaiVPP"),
                    rs.getString("TenLoai")
                ));
            }
        }
        return list;
    }
}
