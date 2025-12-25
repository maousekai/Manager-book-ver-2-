package com.bookstore.dao;

import com.bookstore.model.HoaDon;
import com.bookstore.model.ThongSoBaoCao;
import com.bookstore.model.ChiTietHoaDon;
import com.bookstore.model.ChiTietHoaDonVPP;
import com.bookstore.model.ThongSoNgay;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
	public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getInt("MaHD"));
                hd.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setMaNV(rs.getInt("MaNV"));
                hd.setMaKH(rs.getInt("MaKH"));
                hd.setKhuyenMaiApDung(rs.getBoolean("KhuyenMaiApDung"));
                hd.setSoSachMua(rs.getInt("SoSachMua"));
                list.add(hd);
            }
        }
        return list;
    }

    public HoaDon getById(int maHD) throws SQLException {
        String sql = "SELECT * FROM HoaDon WHERE MaHD = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getInt("MaHD"));
                    hd.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                    hd.setTongTien(rs.getDouble("TongTien"));
                    hd.setMaNV(rs.getInt("MaNV"));
                    hd.setMaKH(rs.getInt("MaKH"));
                    hd.setKhuyenMaiApDung(rs.getBoolean("KhuyenMaiApDung"));
                    hd.setSoSachMua(rs.getInt("SoSachMua"));
                    
                    return hd;
                }
            }
        }
        return null;
    }

    private int insertHoaDon(HoaDon hd, Connection conn) throws SQLException {
        String sql = "INSERT INTO HoaDon (NgayLap, TongTien, MaNV, MaKH, KhuyenMaiApDung, SoSachMua) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(hd.getNgayLap()));
            ps.setDouble(2, hd.getTongTien());
            ps.setInt(3, hd.getMaNV());
            ps.setInt(4, hd.getMaKH());
            ps.setBoolean(5, hd.isKhuyenMaiApDung());
            ps.setInt(6, hd.getSoSachMua());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Insert hóa đơn thất bại");
    }

    public void update(HoaDon hd) throws SQLException {
        String sql = "UPDATE HoaDon SET NgayLap = ?, TongTien = ?, MaNV = ?, MaKH = ?, KhuyenMaiApDung = ?, SoSachMua = ? WHERE MaHD = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDate(1, Date.valueOf(hd.getNgayLap()));
			ps.setDouble(2, hd.getTongTien());
			ps.setInt(3, hd.getMaNV());
			ps.setInt(4, hd.getMaKH());
			ps.setBoolean(5, hd.isKhuyenMaiApDung());
			ps.setInt(6, hd.getSoSachMua());
            ps.setInt(7, hd.getMaHD());
            ps.executeUpdate();
        }
    }

    public void delete(int maHD) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE MaHD = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ps.executeUpdate();
        }
    }

    public void createHoaDonWithDetails(HoaDon hd, List<ChiTietHoaDon> bookDetails, List<ChiTietHoaDonVPP> vppDetails) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            int soSachMua = bookDetails.stream().mapToInt(ChiTietHoaDon::getSoLuong).sum();

            double tongTien = bookDetails.stream().mapToDouble(ChiTietHoaDon::getThanhTien).sum() +
                              vppDetails.stream().mapToDouble(ChiTietHoaDonVPP::getThanhTien).sum();
            
            hd.setSoSachMua(soSachMua);
            hd.setTongTien(tongTien);

            if (soSachMua >= 10) {
                double minBookPrice = bookDetails.stream().mapToDouble(ChiTietHoaDon::getDonGia).min().orElse(0);
                hd.setTongTien(hd.getTongTien() - minBookPrice);
                hd.setKhuyenMaiApDung(true);
            } else {
                hd.setKhuyenMaiApDung(false);
            }

            int maHD = insertHoaDon(hd, conn);
            hd.setMaHD(maHD);

            ChiTietHoaDonDAO bookDAO = new ChiTietHoaDonDAO();
            for (ChiTietHoaDon detail : bookDetails) {
                detail.setMaHD(maHD);
                bookDAO.insert(detail, conn);
                String updateStock = "UPDATE Sach SET SoLuong = SoLuong - ? WHERE MaSach = ?";
                try (PreparedStatement psStock = conn.prepareStatement(updateStock)) {
                    psStock.setInt(1, detail.getSoLuong());
                    psStock.setInt(2, detail.getMaSach());
                    psStock.executeUpdate();
                }
            }

            ChiTietHoaDonVPPDAO vppDAO = new ChiTietHoaDonVPPDAO();
            for (ChiTietHoaDonVPP detail : vppDetails) {
                detail.setMaHD(maHD);
                vppDAO.insert(detail, conn);
                String updateStockVPP = "UPDATE VanPhongPham SET SoLuong = SoLuong - ? WHERE MaVPP = ?";
                try (PreparedStatement psStock = conn.prepareStatement(updateStockVPP)) {
                    psStock.setInt(1, detail.getSoLuong());
                    psStock.setInt(2, detail.getMaVPP());
                    psStock.executeUpdate();
                }
            }
            
            if (hd.getMaKH() > 1) { // Bỏ qua Khách vãng lai (MaKH = 1)
                // Chỉ cập nhật Tổng Chi Tiêu
                String updateCustomer = "UPDATE KhachHang SET TongChiTieu = TongChiTieu + ? WHERE MaKH = ?";
                try (PreparedStatement psCust = conn.prepareStatement(updateCustomer)) {
                    psCust.setDouble(1, hd.getTongTien()); // Tham số 1
                    psCust.setInt(2, hd.getMaKH());        // Tham số 2
                    psCust.executeUpdate();
                }
            }

            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Hoàn tác nếu có lỗi
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    public List<HoaDon> getAllByDateRange(LocalDate start, LocalDate end) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE NgayLap BETWEEN ? AND ? ORDER BY NgayLap DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getInt("MaHD"));
                    hd.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                    hd.setTongTien(rs.getDouble("TongTien"));
                    hd.setMaNV(rs.getInt("MaNV"));
                    hd.setMaKH(rs.getInt("MaKH"));
                    hd.setKhuyenMaiApDung(rs.getBoolean("KhuyenMaiApDung"));
                    hd.setSoSachMua(rs.getInt("SoSachMua"));
                    list.add(hd);
                }
            }
        }
        return list;
    }
// tính lợi nhuận cho cái bảng báo cáo
    public ThongSoBaoCao getReportStats(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT " +
                     "    SUM(TongTien) as TotalRevenue, " +
                     "    SUM(LoiNhuan) as TotalProfit, " +
                     "    SUM(SoLuong) as TotalItems " +
                     "FROM ( " +
                     "    SELECT SoLuong, ThanhTien AS TongTien, DonGia, " +
                     "        (CASE " +
                     "            WHEN DonGia > 100000 THEN 10000 * SoLuong " +
                     "            WHEN DonGia > 10000  THEN 3000 * SoLuong " +
                     "            ELSE 1000 * SoLuong " +
                     "        END) AS LoiNhuan " +
                     "    FROM ChiTietHoaDon ct JOIN HoaDon h ON ct.MaHD = h.MaHD " +
                     "    WHERE h.NgayLap BETWEEN ? AND ? " +
                     "    UNION ALL " +
                     "    SELECT SoLuong, ThanhTien AS TongTien, DonGia, " +
                     "        (CASE " +
                     "            WHEN DonGia > 100000 THEN 10000 * SoLuong " +
                     "            WHEN DonGia > 10000  THEN 3000 * SoLuong " +
                     "            ELSE 1000 * SoLuong " +
                     "        END) AS LoiNhuan " +
                     "    FROM ChiTietHoaDonVPP ctvpp JOIN HoaDon h ON ctvpp.MaHD = h.MaHD " +
                     "    WHERE h.NgayLap BETWEEN ? AND ? " +
                     ") AS CombinedSales";

        ThongSoBaoCao stats = new ThongSoBaoCao();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setDate(3, Date.valueOf(start));
            ps.setDate(4, Date.valueOf(end));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalRevenue(rs.getDouble("TotalRevenue"));
                    stats.setTotalProfit(rs.getDouble("TotalProfit"));
                    stats.setTotalItemsSold(rs.getInt("TotalItems"));
                }
            }
        }
        return stats;
    }
// doanh thu theo ngày
    public List<ThongSoNgay> getDailyRevenue(LocalDate start, LocalDate end) throws SQLException {
        List<ThongSoNgay> list = new ArrayList<>();
        String sql = "SELECT NgayLap, SUM(TongTien) as DoanhThu " +
                     "FROM HoaDon " +
                     "WHERE NgayLap BETWEEN ? AND ? " +
                     "GROUP BY NgayLap " +
                     "ORDER BY NgayLap ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongSoNgay ds = new ThongSoNgay();
                    ds.setNgay(rs.getDate("NgayLap").toLocalDate());
                    ds.setDoanhThu(rs.getDouble("DoanhThu"));
                    list.add(ds);
                }
            }
        }
        return list;
    }
}