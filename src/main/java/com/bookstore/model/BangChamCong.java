package com.bookstore.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class BangChamCong {
    private int maChamCong;
    private int maNV;
    private int maCa;
    private String tenNV;
    private LocalDate ngayLam;
    private LocalTime gioVao;
    private LocalTime gioRa;
    private String trangThai;
    private double gioLam;
    private String ghiChu;

    public BangChamCong() {}
    public BangChamCong(int maChamCong,int maNV, int maCa, LocalDate ngayLam, LocalTime gioVao, LocalTime gioRa, String trangThai, double gioLam, String ghiChu) {
    	this.maChamCong = maChamCong;
		this.maNV = maNV;
		this.maCa = maCa;
		this.ngayLam = ngayLam;
		this.gioVao = gioVao;
		this.gioRa = gioRa;
		this.trangThai = trangThai;
		this.gioLam = gioLam;
		this.ghiChu = ghiChu;
	}
    
    public int getMaChamCong() {
		return maChamCong;
	}
    	public void setMaChamCong(int maChamCong) {
    				this.maChamCong = maChamCong;
    	}
 	public int getMaNV() {return maNV;}
 	public void setMaNV(int maNV) {this.maNV = maNV;}
 	public int getMaCa() {return maCa;}
 	public void setMaCa(int maCa) {this.maCa = maCa;}
 	public LocalDate getNgayLam() {return ngayLam;}
 	public void setNgayLam(LocalDate ngayLam) {this.ngayLam = ngayLam;}
 	public String getTenNV() { return tenNV; }
 	public void setTenNV(String tenNV) { this.tenNV = tenNV; }
 	public LocalTime getGioVao() {return gioVao;}
 	public void setGioVao(LocalTime gioVao) {this.gioVao = gioVao;}
 	public LocalTime getGioRa() {return gioRa;}
 	public void setGioRa(LocalTime gioRa) {this.gioRa = gioRa;}
 	public String getTrangThai() {return trangThai;}
 	public void setTrangThai(String trangThai) {this.trangThai = trangThai;}
 	public double getGioLam() {return gioLam;}
 	public void setGioLam(double gioLam) {this.gioLam = gioLam;}
 	public String getGhiChu() {return ghiChu;}
 	public void setGhiChu(String ghiChu) {this.ghiChu = ghiChu;}

}