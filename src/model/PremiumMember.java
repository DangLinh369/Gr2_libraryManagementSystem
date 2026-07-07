package model;

//member premium: muon toi da 5 cuon, phat 2000/ngay (BR5, BR7)
public class PremiumMember extends Member {

    private static final int BORROW_LIMIT = 5;
    private static final double FINE_PER_DAY = 2000; // BR7

    public PremiumMember(String memberID, String name, String phone, String email) {
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
