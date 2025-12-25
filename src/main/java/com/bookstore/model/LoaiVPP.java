package com.bookstore.model;

public class LoaiVPP {
    private int maLoaiVPP;
    private String tenLoai;

    public LoaiVPP() {}

    public LoaiVPP(int maLoaiVPP, String tenLoai) {
        this.maLoaiVPP = maLoaiVPP;
        this.tenLoai = tenLoai;
    }

    public int getMaLoaiVPP() {
        return maLoaiVPP;
    }

    public void setMaLoaiVPP(int maLoaiVPP) {
        this.maLoaiVPP = maLoaiVPP;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    // QUAN TRỌNG: để ListView hiển thị tên
    @Override
    public String toString() {
        return tenLoai;
    }
}
