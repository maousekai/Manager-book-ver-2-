package com.bookstore.model;

public class TheLoai {
    private int maTL;
    private String tenTL;
    
    public TheLoai() {}
    
	public TheLoai(int maTL ,String tenTL) {
		this.maTL = maTL;
		this.tenTL = tenTL;
	}
		public int getMaTL() { return maTL; }
		public void setMaTL(int maTL) { this.maTL = maTL; }
		public String getTenTL() { return tenTL; }
		public void setTenTL(String tenTL) { this.tenTL = tenTL; }

}