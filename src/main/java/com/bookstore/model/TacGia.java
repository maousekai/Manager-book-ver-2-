package com.bookstore.model;

public class TacGia {
    private int maTG;
    private String tenTG;
    private String ghiChu;
    
    public TacGia() {}
    public TacGia(int maTG ,String tenTG, String ghiChu) {
    			this.maTG = maTG;
    			this.tenTG = tenTG;
    			this.ghiChu = ghiChu;
    }
    public int getMaTG() { return maTG; }
    public void setMaTG(int maTG) { this.maTG = maTG; }
    public String getTenTG() { return tenTG; }
    public void setTenTG(String tenTG) { this.tenTG = tenTG; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

}