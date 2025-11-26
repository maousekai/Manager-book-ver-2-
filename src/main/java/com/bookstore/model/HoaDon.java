package com.bookstore.model;

import java.time.LocalDate;

public class HoaDon {
    private int maHD;
    private LocalDate ngayLap;
    private double tongTien;
    private int maNV;
    private int maKH;
    private boolean khuyenMaiApDung;
    private int soSachMua;
    
    public HoaDon() {}
    public HoaDon(int maHD ,LocalDate ngayLap, double tongTien, int maNV, int maKH, boolean khuyenMaiApDung, int soSachMua) {
    	this.maHD = maHD;
		this.ngayLap = ngayLap;
		this.tongTien = tongTien;
		this.maNV = maNV;
		this.maKH = maKH;
		this.khuyenMaiApDung = khuyenMaiApDung;
		this.soSachMua = soSachMua;
	}
    
    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }
    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public int getMaKH() { return maKH; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public boolean isKhuyenMaiApDung() { return khuyenMaiApDung; }
    public void setKhuyenMaiApDung(boolean khuyenMaiApDung) { this.khuyenMaiApDung = khuyenMaiApDung; }
    public int getSoSachMua() { return soSachMua; }
    public void setSoSachMua(int soSachMua) { this.soSachMua = soSachMua; }

}