package com.bookstore.model;

public class Sach {
    private int maSach;
    private String tenSach;
    private double donGia;
    private int soLuong;
    private int namXuatBan;
    private String anh;
    private String khuyenMai;
    private String viTriKe;
    private String viTriNgan;
    private String viTriHang;
    private String moTa;
    private int maNXB;
    
    public Sach() {}
    
    public Sach(int maSach,String tenSach, double donGia, int soLuong, int namXuatBan, String anh, String khuyenMai,
			String viTriKe, String viTriNgan, String viTriHang, String moTa, int maNXB) {
    	this.maSach = maSach;
		this.tenSach = tenSach;
		this.donGia = donGia;
		this.soLuong = soLuong;
		this.namXuatBan = namXuatBan;
		this.anh = anh;
		this.khuyenMai = khuyenMai;
		this.viTriKe = viTriKe;
		this.viTriNgan = viTriNgan;
		this.viTriHang = viTriHang;
		this.moTa = moTa;
		this.maNXB = maNXB;
	}
    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }
    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public int getNamXuatBan() { return namXuatBan; }
    public void setNamXuatBan(int namXuatBan) { this.namXuatBan = namXuatBan; }
    public String getAnh() { return anh; }
    public void setAnh(String anh) { this.anh = anh; }
    public String getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(String khuyenMai) { this.khuyenMai = khuyenMai; }
    public String getViTriKe() { return viTriKe; }
    public void setViTriKe(String viTriKe) { this.viTriKe = viTriKe; }
    public String getViTriNgan() { return viTriNgan; }
    public void setViTriNgan(String viTriNgan) { this.viTriNgan = viTriNgan; }
    public String getViTriHang() { return viTriHang; }
    public void setViTriHang(String viTriHang) { this.viTriHang = viTriHang; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public int getMaNXB() { return maNXB; }
    public void setMaNXB(int maNXB) { this.maNXB = maNXB; }

}