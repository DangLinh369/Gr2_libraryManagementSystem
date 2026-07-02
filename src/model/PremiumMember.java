package model;

/**
 * PremiumMember - kế thừa Member.
 * Khác biệt so với RegularMember (đáp ứng yêu cầu mục 5: "RegularMember vs PremiumMember
 * với borrowing limit hoặc fine rate khác nhau"):
 * - Giới hạn mượn cao hơn (5 sách thay vì 3) - BR5.
 * - Phí phạt thấp hơn: 2,000 VND/ngày so với 5,000 của Regular - BR7
 *   (theo đúng Business Rules trong report Lab 1/2 đã nộp).
 */
public class PremiumMember extends Member {

    private static final int BORROW_LIMIT = 5;
    private static final double FINE_PER_DAY = 2000; // BR7: Premium 2,000 VND/ngày (report Lab1/2)

    public PremiumMember(String memberID, String name, String phone, String email) {
        super(memberID, name, phone, email);
    }

    @Override
    public double calcFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * FINE_PER_DAY; // BR7 (mức ưu đãi riêng cho Premium)
    }

    @Override
    public int getBorrowLimit() {
        return BORROW_LIMIT; // BR5 (giới hạn cao hơn Regular)
    }
}
