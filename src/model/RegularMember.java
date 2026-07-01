package model;

/**
 * RegularMember - kế thừa Member.
 * BR5: giới hạn mượn 3 sách/lần (theo SYSTEM INTERFACE mục Business Rules: "e.g., 3 books at a time").
 * BR7: phí phạt mặc định 5,000 VND/ngày trễ hạn.
 */
public class RegularMember extends Member {

    private static final int BORROW_LIMIT = 3;
    private static final double FINE_PER_DAY = 5000;

    public RegularMember(String memberID, String name, String phone, String email) {
        super(memberID, name, phone, email);
    }

    @Override
    public double calcFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * FINE_PER_DAY; // BR7
    }

    @Override
    public int getBorrowLimit() {
        return BORROW_LIMIT; // BR5
    }
}
