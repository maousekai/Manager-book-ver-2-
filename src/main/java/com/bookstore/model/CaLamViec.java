package com.bookstore.model;

import java.time.LocalTime;

public class CaLamViec {
    private int maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private String ghiChu;

    public CaLamViec() {}

    public CaLamViec(int maCa ,String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc, String ghiChu) {
    	this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.ghiChu = ghiChu;
    }

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }
    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

}