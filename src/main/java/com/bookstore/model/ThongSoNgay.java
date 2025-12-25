package com.bookstore.model;

import java.time.LocalDate;

public class ThongSoNgay {
    private LocalDate ngay;
    private double doanhThu;

    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) { this.ngay = ngay; }
    public double getDoanhThu() { return doanhThu; }
    public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }
}