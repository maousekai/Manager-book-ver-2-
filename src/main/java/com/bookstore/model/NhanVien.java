package com.bookstore.model;

import java.time.LocalDate;

public class NhanVien {
    private int maNV;
    private String tenNV;
    private LocalDate ngaySinh;
    private String diaChi;
    private String dienThoai;
    private String taiKhoan;
    private String matKhau;
    private LocalDate ngayVaoLam;
    private int maQuyen;
    private String viTri;
    private double luongCoBan;

    public NhanVien() {}

    public NhanVien(int maNV, String tenNV, LocalDate ngaySinh, String diaChi, String dienThoai, String taiKhoan, String matKhau, LocalDate ngayVaoLam, int maQuyen, String viTri, double luongCoBan) {
    	this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.dienThoai = dienThoai;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.ngayVaoLam = ngayVaoLam;
        this.maQuyen = maQuyen;
        this.viTri = viTri;
        this.luongCoBan = luongCoBan;
    }

    public int getMaQuyen() { return maQuyen; }
    public void setMaQuyen(int maQuyen) { this.maQuyen = maQuyen; }
    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }
}