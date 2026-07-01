package model;

/**
 * Abstract class Member - thể hiện Generalization (Milestone 3).
 * Các subclass RegularMember / PremiumMember override calcFine() và getBorrowLimit()
 * -> đây chính là Polymorphism mà mục 5 (OOP Requirements) yêu cầu.
 * BR1: memberID là unique, không đổi sau khi tạo.
 * BR5: borrowing limit khác nhau tùy loại member (đa hình qua getBorrowLimit()).
 * BR7: overdue fine khác nhau tùy loại member (đa hình qua calcFine()).
 */
public abstract class Member {
    private String memberID;
    private String name;
    private String phone;
    private String email;

    public Member(String memberID, String name, String phone, String email) {
        this.memberID = memberID; // BR1: gán 1 lần, không có setMemberID()
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getMemberID() { return memberID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * BR7: tiền phạt quá hạn - mỗi loại member tính khác nhau (override ở subclass).
     */
    public abstract double calcFine(int overdueDays);

    /**
     * BR5: số sách tối đa được mượn cùng lúc - mỗi loại member khác nhau (override ở subclass).
     */
    public abstract int getBorrowLimit();

    @Override
    public String toString() {
        return String.format("%-6s %-20s %-12s %-25s [%s]",
                memberID, name, phone, email, getClass().getSimpleName());
    }

    public String toFileLine() {
        // Lưu kèm loại member để khi đọc lại file biết tạo RegularMember hay PremiumMember
        return getClass().getSimpleName() + "|" + memberID + "|" + name + "|" + phone + "|" + email;
    }
}
