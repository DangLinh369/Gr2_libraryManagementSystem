package model;


public abstract class Member {
    private String memberID;
    private String name;
    private String phone;
    private String email;

    public Member(String memberID, String name, String phone, String email) {
        this.memberID = memberID; //BR1: id gan 1 lan, khong co setter
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

    //tien phat qua han - moi loai member tinh khac nhau
    public abstract double calcFine(int overdueDays);

    //so sach toi da duoc muon cung luc
    public abstract int getBorrowLimit();

    @Override
    public String toString() {
        return String.format("%-6s %-20s %-12s %-25s [%s]",
                memberID, name, phone, email, getClass().getSimpleName());
    }

    public String toFileLine() {
        //luu kem loai member de doc file biet tao dung class con
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

