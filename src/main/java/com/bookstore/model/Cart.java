package com.bookstore.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<ChiTietHoaDon> bookItems = new ArrayList<>();
    private List<ChiTietHoaDonVPP> vppItems = new ArrayList<>();

    public void addBook(Sach book, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        
        // Kiểm tra xem sách đã tồn tại trong giỏ hàng chưa
        for (ChiTietHoaDon item : bookItems) {
            if (item.getMaSach() == book.getMaSach()) {
                int newQuantity = item.getSoLuong() + quantity;
                if (newQuantity > book.getSoLuong()) {
                    // Hiển thị lỗi hoặc thông báo (ví dụ: sử dụng AlertUtil)
                    System.out.println("Lỗi: Vượt quá số lượng tồn kho!");
                    return; 
                }
                item.setSoLuong(newQuantity); // Cập nhật số lượng và thành tiền (đã tự động trong setSoLuong)
                return;
            }
        }
        
        // Nếu không tìm thấy, thêm mới (với kiểm tra tồn kho ban đầu)
        if (quantity > book.getSoLuong()) {
             System.out.println("Lỗi: Vượt quá số lượng tồn kho!");
             return;
        }
        ChiTietHoaDon item = new ChiTietHoaDon(0, book.getMaSach(), quantity, book.getDonGia());
        bookItems.add(item);
    }
    public void decreaseBook(ChiTietHoaDon item) {
        for (ChiTietHoaDon ct : bookItems) {
            if (ct.getMaSach() == item.getMaSach()) {
                if (ct.getSoLuong() > 1) {
                    ct.setSoLuong(ct.getSoLuong() - 1);
                } else {
                    // Nếu số lượng là 1, xóa luôn
                    bookItems.remove(ct);
                }
                return;
            }
        }
    }

    public void removeBook(ChiTietHoaDon item) {
        bookItems.removeIf(ct -> ct.getMaSach() == item.getMaSach());
    }

    public void addVPP(VanPhongPham vpp, int quantity) {
		if (quantity <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
		
		for (ChiTietHoaDonVPP item : vppItems) {
			if (item.getMaVPP() == vpp.getMaVPP()) {
				int newQuantity = item.getSoLuong() + quantity;
				if (newQuantity > vpp.getSoLuong()) {
					System.out.println("Lỗi: Vượt quá số lượng tồn kho!");
					return; 
				}
				item.setSoLuong(newQuantity);
				return;
			}
		}
		
		if (quantity > vpp.getSoLuong()) {
			 System.out.println("Lỗi: Vượt quá số lượng tồn kho!");
			 return;
		}
		ChiTietHoaDonVPP item = new ChiTietHoaDonVPP(0, vpp.getMaVPP(), quantity, vpp.getDonGia());
		vppItems.add(item);
    }
    public void decreaseVPP(ChiTietHoaDonVPP item) {
        vppItems.stream()
            .filter(ct -> ct.getMaVPP() == item.getMaVPP())
            .findFirst()
            .ifPresent(ct -> {
                if (ct.getSoLuong() > 1) {
                    ct.setSoLuong(ct.getSoLuong() - 1);
                } else {
                    vppItems.remove(ct);
                }
            });
    }

    public void removeVPP(ChiTietHoaDonVPP item) {
        vppItems.removeIf(ct -> ct.getMaVPP() == item.getMaVPP());
    }
    public double getTotal() {
        return bookItems.stream().mapToDouble(ChiTietHoaDon::getThanhTien).sum() +
               vppItems.stream().mapToDouble(ChiTietHoaDonVPP::getThanhTien).sum();
    }

    public int getBookCount() {
        return bookItems.stream().mapToInt(ChiTietHoaDon::getSoLuong).sum();
    }

    public List<ChiTietHoaDon> getBookItems() { return bookItems; }
    public List<ChiTietHoaDonVPP> getVppItems() { return vppItems; }

    public void clear() {
        bookItems.clear();
        vppItems.clear();
    }
}