package model;

//member thuong: muon toi da 3 cuon, phat 5000/ngay (BR5, BR7)
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
