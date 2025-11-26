package com.bookstore.dao;

import com.bookstore.model.BookRanking;
import com.bookstore.model.Sach;
import com.bookstore.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SachDAO {
	public List<Sach> getAll(int limit, int offset) throws SQLException {
        List<Sach> list = new ArrayList<>();
        String sql = "SELECT * FROM Sach ORDER BY MaSach OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sach sach = new Sach();
                    sach.setMaSach(rs.getInt("MaSach"));
                    sach.setTenSach(rs.getString("TenSach"));
                    sach.setDonGia(rs.getDouble("DonGia"));
                    sach.setSoLuong(rs.getInt("SoLuong"));
                    sach.setNamXuatBan(rs.getInt("NamXuatBan"));
                    sach.setAnh(rs.getString("Anh"));
                    sach.setKhuyenMai(rs.getString("KhuyenMai"));
                    sach.setViTriKe(rs.getString("ViTriKe"));
                    sach.setViTriNgan(rs.getString("ViTriNgan"));
                    sach.setViTriHang(rs.getString("ViTriHang"));
                    sach.setMoTa(rs.getString("MoTa"));
                    sach.setMaNXB(rs.getInt("MaNXB"));
                    list.add(sach);
                }
            }
        }
        return list;
    }
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Sach";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    public List<Sach> getByGenre(int maTheLoai) throws SQLException {
        List<Sach> list = new ArrayList<>();
        String sql = "SELECT s.* FROM Sach s JOIN Sach_TheLoai st ON s.MaSach = st.MaSach WHERE st.MaTL = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTheLoai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	
                    Sach sach = new Sach();
                    sach.setMaSach(rs.getInt("MaSach"));
                    sach.setTenSach(rs.getString("TenSach"));
                    sach.setDonGia(rs.getDouble("DonGia"));
                    sach.setSoLuong(rs.getInt("SoLuong"));
                    sach.setNamXuatBan(rs.getInt("NamXuatBan"));
                    sach.setAnh(rs.getString("Anh"));
                    sach.setKhuyenMai(rs.getString("KhuyenMai"));
                    sach.setViTriKe(rs.getString("ViTriKe"));
                    sach.setViTriNgan(rs.getString("ViTriNgan"));
                    sach.setViTriHang(rs.getString("ViTriHang"));
                    sach.setMoTa(rs.getString("MoTa"));
                    sach.setMaNXB(rs.getInt("MaNXB"));
                    list.add(sach);
                }
            }
        }
        return list;
    }
    public Sach getById(int maSach) throws SQLException {
        String sql = "SELECT * FROM Sach WHERE MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sach s = new Sach();
                    s.setMaSach(rs.getInt("MaSach"));
                    s.setTenSach(rs.getString("TenSach"));
                    s.setDonGia(rs.getDouble("DonGia"));
                    s.setSoLuong(rs.getInt("SoLuong"));
                    s.setNamXuatBan(rs.getInt("NamXuatBan"));
                    s.setAnh(rs.getString("Anh"));
                    s.setKhuyenMai(rs.getString("KhuyenMai"));
                    s.setViTriKe(rs.getString("ViTriKe"));
                    s.setViTriNgan(rs.getString("ViTriNgan"));
                    s.setViTriHang(rs.getString("ViTriHang"));
                    s.setMoTa(rs.getString("MoTa"));
                    s.setMaNXB(rs.getInt("MaNXB"));
                    return s;
                }
            }
        }
        return null;
    }
    public List<Sach> searchByName(String name) throws SQLException {
		List<Sach> list = new ArrayList<>();
		String sql = "SELECT * FROM Sach WHERE TenSach LIKE ?";
		try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, "%" + name + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Sach sach = new Sach();
					sach.setMaSach(rs.getInt("MaSach"));
					sach.setTenSach(rs.getString("TenSach"));
					sach.setDonGia(rs.getDouble("DonGia"));
					sach.setSoLuong(rs.getInt("SoLuong"));
					sach.setNamXuatBan(rs.getInt("NamXuatBan"));
					sach.setAnh(rs.getString("Anh"));
					sach.setKhuyenMai(rs.getString("KhuyenMai"));
					sach.setViTriKe(rs.getString("ViTriKe"));
					sach.setViTriNgan(rs.getString("ViTriNgan"));
					sach.setViTriHang(rs.getString("ViTriHang"));
					sach.setMoTa(rs.getString("MoTa"));
					sach.setMaNXB(rs.getInt("MaNXB"));
					list.add(sach);
				}
			}
		}
		return list;
	}
    public void insert(Sach s) throws SQLException {
        String sql = "INSERT INTO Sach (TenSach, DonGia, SoLuong, NamXuatBan, Anh, KhuyenMai, ViTriKe, ViTriNgan, ViTriHang, MoTa, MaNXB, MaSach) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getTenSach());
            ps.setDouble(2, s.getDonGia());
            ps.setInt(3, s.getSoLuong());
            ps.setInt(4, s.getNamXuatBan());
            ps.setString(5, s.getAnh());
            ps.setString(6, s.getKhuyenMai());
            ps.setString(7, s.getViTriKe());
            ps.setString(8, s.getViTriNgan());
            ps.setString(9, s.getViTriHang());
            ps.setString(10, s.getMoTa());
            ps.setInt(11, s.getMaNXB());
            ps.setInt(12, s.getMaSach());
            ps.executeUpdate();
        }
    }

    public void update(Sach s) throws SQLException {
        String sql = "UPDATE Sach SET TenSach = ?, DonGia = ?, SoLuong = ?, NamXuatBan = ?, Anh = ?, KhuyenMai = ?, ViTriKe = ?, ViTriNgan = ?, ViTriHang = ?, MoTa = ?, MaNXB = ? WHERE MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, s.getTenSach());
			ps.setDouble(2, s.getDonGia());
			ps.setInt(3, s.getSoLuong());
			ps.setInt(4, s.getNamXuatBan());
			ps.setString(5, s.getAnh());
			ps.setString(6, s.getKhuyenMai());
			ps.setString(7, s.getViTriKe());
			ps.setString(8, s.getViTriNgan());
			ps.setString(9, s.getViTriHang());
			ps.setString(10, s.getMoTa());
			ps.setInt(11, s.getMaNXB());
            ps.setInt(12, s.getMaSach());
            ps.executeUpdate();
        }
    }

    public void delete(int maSach) throws SQLException {
        String sql = "DELETE FROM Sach WHERE MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            ps.executeUpdate();
        }
    }
    public List<String> getAuthorsForBook(int maSach) throws SQLException {
        List<String> authors = new ArrayList<>();
        String sql = "SELECT t.TenTG FROM TacGia t JOIN Sach_TacGia st ON t.MaTG = st.MaTG WHERE st.MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authors.add(rs.getString("TenTG"));
                }
            }
        }
        return authors;
    }
    public List<String> getGenresForBook(int maSach) throws SQLException {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT tl.TenTL FROM TheLoai tl JOIN Sach_TheLoai stl ON tl.MaTL = stl.MaTL WHERE stl.MaSach = ?";
        try (Connection conn = DatabaseManager.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, maSach);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					genres.add(rs.getString("TenTL"));
				}
			}
		}
        return genres;
    }
    public int insertAndGetId(Sach s) throws SQLException {
        String sql = "INSERT INTO Sach (TenSach, DonGia, SoLuong, NamXuatBan, Anh, KhuyenMai, ViTriKe, ViTriNgan, ViTriHang, MoTa, MaNXB) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, s.getTenSach());
            ps.setDouble(2, s.getDonGia());
            ps.setInt(3, s.getSoLuong());
            ps.setInt(4, s.getNamXuatBan());
            ps.setString(5, s.getAnh());
            ps.setString(6, s.getKhuyenMai());
            ps.setString(7, s.getViTriKe());
            ps.setString(8, s.getViTriNgan());
            ps.setString(9, s.getViTriHang());
            ps.setString(10, s.getMoTa());
            ps.setInt(11, s.getMaNXB());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Tạo sách thất bại, không hàng nào được thêm.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    s.setMaSach(newId);
                    return newId; // Trả về MaSach
                } else {
                    throw new SQLException("Tạo sách thất bại, không lấy được ID.");
                }
            }
        }}
        public Sach findExactlyByName(String tenSach) throws SQLException {
            String sql = "SELECT * FROM Sach WHERE TenSach = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tenSach);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Sach s = new Sach();
                        s.setMaSach(rs.getInt("MaSach"));
                        s.setTenSach(rs.getString("TenSach"));
                        s.setDonGia(rs.getDouble("DonGia"));
                        s.setSoLuong(rs.getInt("SoLuong"));
                        return s;
                    }
                }
            }
            return null; // Không tìm thấy
        }

        public void addStock(int maSach, int soLuongThem) throws SQLException {
            String sql = "UPDATE Sach SET SoLuong = SoLuong + ? WHERE MaSach = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, soLuongThem);
                ps.setInt(2, maSach);
                ps.executeUpdate();
            }
        
    }
        public List<BookRanking> getBookRanking(LocalDate start, LocalDate end) throws SQLException {
            List<BookRanking> list = new ArrayList<>();
            String sql = "SELECT TOP 10 s.TenSach, SUM(ct.SoLuong) as SoLuongBan " +
                         "FROM Sach s " +
                         "JOIN ChiTietHoaDon ct ON s.MaSach = ct.MaSach " +
                         "JOIN HoaDon h ON ct.MaHD = h.MaHD " +
                         "WHERE h.NgayLap BETWEEN ? AND ? " +
                         "GROUP BY s.TenSach " +
                         "ORDER BY SoLuongBan DESC";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(start));
                ps.setDate(2, Date.valueOf(end));
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        BookRanking br = new BookRanking();
                        br.setTenSach(rs.getString("TenSach"));
                        br.setSoLuongBan(rs.getInt("SoLuongBan"));
                        list.add(br);
                    }
                }
            }
            return list;
        }
}