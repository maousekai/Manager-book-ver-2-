package com.bookstore.model;

public class SachTacGia {
    private int maSach;
    private int maTG;
    
    public SachTacGia() {}
    public SachTacGia(int maSach, int maTG) {
		this.maSach = maSach;
		this.maTG = maTG;
	}
    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }
    public int getMaTG() { return maTG; }
    public void setMaTG(int maTG) { this.maTG = maTG; }

}