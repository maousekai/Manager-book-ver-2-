package com.bookstore.model;

public class NhaXuatBan {
    private int maNXB;
    private String tenNXB;
    private String diaChi;
    private String dienThoai;
    
    public NhaXuatBan() {}
    public NhaXuatBan(int maNXB ,String tenNXB, String diaChi, String dienThoai) {
    	this.maNXB = maNXB;
		this.tenNXB = tenNXB;
		this.diaChi = diaChi;
		this.dienThoai = dienThoai;
	}
    
    public int getMaNXB() { return maNXB; }
    public void setMaNXB(int maNXB) { this.maNXB = maNXB; }
    public String getTenNXB() { return tenNXB; }
    public void setTenNXB(String tenNXB) { this.tenNXB = tenNXB; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }

}