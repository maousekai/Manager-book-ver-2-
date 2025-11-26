package com.bookstore.model;

public class ChiTietHoaDonVPP extends AbstractChiTietHoaDon {
    public ChiTietHoaDonVPP() {}

    public ChiTietHoaDonVPP(int maHD, int maVPP, int soLuong, double donGia) {
        super(maHD, maVPP, soLuong, donGia);
    }

    public int getMaVPP() { return maSanPham; }
    public void setMaVPP(int maVPP) { this.maSanPham = maVPP; }

}