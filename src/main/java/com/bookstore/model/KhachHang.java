package com.bookstore.model;

public class KhachHang {
    private int maKH;
    private String hoTen;
    private String diaChi;
    private String dienThoai;
    private String ghiChu;
    private double tongChiTieu;

    public KhachHang() {}

    public KhachHang(int maKH, String hoTen, String diaChi, String dienThoai, String ghiChu ,double tongChiTieu) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.dienThoai = dienThoai;
        this.ghiChu = ghiChu;
        this.tongChiTieu = tongChiTieu;
    }

    public int getMaKH() {
        return maKH;
    }

    public void setMaKH(int maKH) {
        this.maKH = maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    public double getTongChiTieu() {
        return tongChiTieu;
    }
    public void setTongChiTieu(double tongChiTieu) {
        this.tongChiTieu = tongChiTieu;
    }
}