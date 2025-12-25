package com.bookstore.model;

import java.util.ArrayList;
import java.util.List;

public class GioHang {
    private List<ChiTietHoaDon> bookItems = new ArrayList<>();
    private List<ChiTietHoaDonVPP> vppItems = new ArrayList<>();
    private double originalTotal;
    private double discountTotal;
    private double finalTotal;


    public void addBook(Sach book, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0");

        // Tính giá đơn vị đã áp dụng khuyến mãi sản phẩm 
        double unitPrice = computeFinalUnitPrice(book.getDonGia(), book.getKhuyenMai());

        // Kiểm tra xem sách đã tồn tại trong giỏ hàng chưa
        for (ChiTietHoaDon item : bookItems) {
            if (item.getMaSach() == book.getMaSach()) {
                int newQuantity = item.getSoLuong() + quantity;
                if (newQuantity > book.getSoLuong()) {
                    System.out.println("Lỗi: Vượt quá số lượng tồn kho!");
                    return;
                }
                item.setSoLuong(newQuantity); // setSoLuong sẽ cập nhật thanhTien dựa trên donGia trong item
                return;
            }
        }

        // Nếu không tìm thấy, thêm mới
        if (quantity > book.getSoLuong()) {
            System.out.println("Lỗi: Vượt quá số lượng tồn kho!");
            return;
        }
        // chi tiết hóa đơn lưu donGia là unitPrice (đã giảm nếu có)
        ChiTietHoaDon item = new ChiTietHoaDon(0, book.getMaSach(), quantity, unitPrice);
        bookItems.add(item);
    }

    public void decreaseBook(ChiTietHoaDon item) { // Giảm số lượng sách trong giỏ
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

        double unitPrice = computeFinalUnitPrice(vpp.getDonGia(), vpp.getKhuyenMai());

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
        ChiTietHoaDonVPP item = new ChiTietHoaDonVPP(0, vpp.getMaVPP(), quantity, unitPrice);
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

    public double getOrderLevelDiscount() {
        double total = getTotal();
        if (getBookCount() >= 10) {
            return total * 0.10; // 10% giảm nếu mua >= 10 sách
        }
        return 0;
    }
    // Tổng tiền khuyến mãi (các loại có thể cộng dồn)
    public double getDiscount() {
        return getOrderLevelDiscount();
    }

    public double getFinalTotal() { // Tổng tiền sau khuyến mãi
        return getTotal() - getDiscount();
    }

    public int getBookCount() { // Tổng số lượng sách trong giỏ
        return bookItems.stream().mapToInt(ChiTietHoaDon::getSoLuong).sum();
    }

    public List<ChiTietHoaDon> getBookItems() { return bookItems; }
    public List<ChiTietHoaDonVPP> getVppItems() { return vppItems; }

    public void clear() {
        bookItems.clear();
        vppItems.clear();
    }
    public double getOriginalTotal() { // Tổng tiền chưa áp dụng khuyến mãi
        double total = 0;
        // SÁCH
        for (ChiTietHoaDon ct : bookItems) {
            total += ct.getDonGia() * ct.getSoLuong();
        }
        // VĂN PHÒNG PHẨM
        for (ChiTietHoaDonVPP ct : vppItems) {
            total += ct.getDonGia() * ct.getSoLuong();
        }

        return total;
    }
    public double getDiscountTotal() {
        return getOriginalTotal() - getTotal();
    }
    // Tính giá đơn vị sau khi áp dụng khuyến mãi sản phẩm
    private double computeFinalUnitPrice(double originalPrice, String promo) {
        if (promo == null) return originalPrice;
        promo = promo.trim().toUpperCase();
        if (promo.startsWith("GIAM_")) {
            try {
                int p = Integer.parseInt(promo.replace("GIAM_", ""));
                double finalPrice = originalPrice * (1 - p / 100.0);
                return Math.max(0, finalPrice);
            } catch (Exception ex) {
                return originalPrice;
            }
        }
        return originalPrice;
    }
}
