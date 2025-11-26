package com.bookstore.model;

public class ChiTietHoaDon extends AbstractChiTietHoaDon {
    public ChiTietHoaDon() {}

    public ChiTietHoaDon(int maHD, int maSach, int soLuong, double donGia) {
        super(maHD, maSach, soLuong, donGia);
    }

    public int getMaSach() { return maSanPham; }
    public void setMaSach(int maSach) { this.maSanPham = maSach; }

}