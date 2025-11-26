package com.bookstore.model;

public class ChiTietNhapKho {
    private int maCTN;
    private int maHDN;
    private Integer maSach;
    private Integer maVPP; 
    private int soLuongNhap;

    public int getMaCTN() {
        return maCTN;
    }
    public void setMaCTN(int maCTN) {
        this.maCTN = maCTN;
    }
    public int getMaHDN() {
        return maHDN;
    }
    public void setMaHDN(int maHDN) {
        this.maHDN = maHDN;
    }
    public Integer getMaSach() {
        return maSach;
    }
    public void setMaSach(Integer maSach) {
        this.maSach = maSach;
    }
    public Integer getMaVPP() {
        return maVPP;
    }
    public void setMaVPP(Integer maVPP) {
        this.maVPP = maVPP;
    }
    public int getSoLuongNhap() {
        return soLuongNhap;
    }
    public void setSoLuongNhap(int soLuongNhap) {
        this.soLuongNhap = soLuongNhap;
    }
}