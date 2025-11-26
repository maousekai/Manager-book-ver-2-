package com.bookstore.model;

import java.time.LocalDate;

public class HoaDonNhapKho {
    private int maHDN;
    private int maNV;
    private LocalDate ngayNhap;
    private int tongSoLuong;
    private String ghiChu;

    public int getMaHDN() {
        return maHDN;
    }
    public void setMaHDN(int maHDN) {
        this.maHDN = maHDN;
    }
    public int getMaNV() {
        return maNV;
    }
    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }
    public LocalDate getNgayNhap() {
        return ngayNhap;
    }
    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }
    public int getTongSoLuong() {
        return tongSoLuong;
    }
    public void setTongSoLuong(int tongSoLuong) {
        this.tongSoLuong = tongSoLuong;
    }
    public String getGhiChu() {
        return ghiChu;
    }
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}