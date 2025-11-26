package com.bookstore.model;

public abstract class AbstractChiTietHoaDon {
    protected int maHD;
    protected int maSanPham;
    protected int soLuong;
    protected double donGia;
    protected double thanhTien;

    public AbstractChiTietHoaDon() {}

    public AbstractChiTietHoaDon(int maHD, int maSanPham, int soLuong, double donGia) {
        if (soLuong < 0 || donGia < 0) throw new IllegalArgumentException("Giá trị không âm");
        this.maHD = maHD;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }

    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }
    public int getMaSanPham() { return maSanPham; }
    public void setMaSanPham(int maSanPham) { this.maSanPham = maSanPham; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        if (soLuong < 0) throw new IllegalArgumentException("Số lượng không âm");
        this.soLuong = soLuong;
        this.thanhTien = soLuong * donGia;
    }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) {
        if (donGia < 0) throw new IllegalArgumentException("Giá không âm");
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }
    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }
}