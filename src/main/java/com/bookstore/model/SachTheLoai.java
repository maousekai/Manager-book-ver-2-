package com.bookstore.model;

public class SachTheLoai {
	private int maSach;
	private int maTL;
	
	public SachTheLoai() {}
	public SachTheLoai(int maSach, int maTL) {
		this.maSach = maSach;
		this.maTL = maTL;
	}
	public int getMaSach() { return maSach; }
	public void setMaSach(int maSach) { this.maSach = maSach; }
	public int getMaTL() { return maTL; }
	public void setMaTL(int maTL) { this.maTL = maTL; }
}
