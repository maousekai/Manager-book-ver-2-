package com.bookstore.model;

public class VanPhongPham {
    private int maVPP;
    private String tenVPP;
    private double donGia;
    private int soLuong;
    private String nhaCungCap;
    private int nguongDatHang;
    private String khuyenMai;
    private String ghiChu;
    private String anh;
    private int maLoaiVPP;

    public VanPhongPham() {}
    
    public VanPhongPham(int maVPP, String tenVPP, double donGia, int soLuong, 
            String nhaCungCap, int nguongDatHang, String ghiChu, String anh, String khuyenMai, int maLoaiVPP) {
    	this.maVPP = maVPP;
    	this.tenVPP = tenVPP;
    	this.donGia = donGia;
    	this.soLuong = soLuong;
    	this.nhaCungCap = nhaCungCap;
    	this.nguongDatHang = nguongDatHang;
    	this.ghiChu = ghiChu;
    	this.anh = anh;
    	this.khuyenMai = khuyenMai;
    	this.maLoaiVPP = maLoaiVPP;
    	
}
    
    public int getMaVPP() { return maVPP; }
	public void setMaVPP(int maVPP) { this.maVPP = maVPP; }
	public String getTenVPP() { return tenVPP; }
	public void setTenVPP(String tenVPP) { this.tenVPP = tenVPP; }
	public double getDonGia() { return donGia; }
	public void setDonGia(double donGia) { this.donGia = donGia; }
	public int getSoLuong() { return soLuong; }
	public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
	public String getNhaCungCap() { return nhaCungCap; }
	public void setNhaCungCap(String nhaCungCap) { this.nhaCungCap = nhaCungCap; }
	public int getNguongDatHang() { return nguongDatHang; }
	public void setNguongDatHang(int nguongDatHang) { this.nguongDatHang = nguongDatHang; }
	public String getGhiChu() { return ghiChu; }
	public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
	public String getAnh() { return anh; }
    public void setAnh(String anh) { this.anh = anh; }
    public String getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(String khuyenMai) { this.khuyenMai = khuyenMai; }
    public int getMaLoaiVPP() { return maLoaiVPP; }
    public void setMaLoaiVPP(int maLoaiVPP) { this.maLoaiVPP = maLoaiVPP; }
    
}