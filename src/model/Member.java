package model;


public abstract class Member {
    private String memberID;
    private String name;
    private String phone;
    private String email;

    public Member(String memberID, String name, String phone, String email) {
        this.memberID = memberID; // BR1: gán 1 lần, không có setMemberID()
        this.name = capitalizeWords(name);
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
     * tiền phạt quá hạn - mỗi loại member tính khác nhau (override ở subclass).
     */
    public abstract double calcFine(int overdueDays);

    /**
     * số sách tối đa được mượn cùng lúc - mỗi loại member khác nhau (override ở subclass).
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
    
    private String capitalizeWords(String input){
        if (input==null || input.trim().isEmpty())
            return input;
        
        String [] words = input.trim().toLowerCase().split("\\s+");
        
        StringBuilder sb = new StringBuilder();
        for (String w : words){
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1))
                    .append(" ");
            
        }
    
    return sb.toString().trim();
    } 
}

