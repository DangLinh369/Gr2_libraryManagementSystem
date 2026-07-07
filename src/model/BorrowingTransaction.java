package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BorrowingTransaction {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final int DEFAULT_BORROW_DAYS = 14;

    private String transactionID;
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; //null = chua tra

    public BorrowingTransaction(String transactionID, Book book, Member member, LocalDate borrowDate) {
        this.transactionID = transactionID;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate; //BR6: ngay muon da duoc check truoc khi tao
        this.dueDate = borrowDate.plusDays(DEFAULT_BORROW_DAYS);
        this.returnDate = null;
    }

    public String getTransactionID() { return transactionID; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) {
        //BR6: ngay tra da duoc validate truoc khi goi
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        LocalDate checkDate = (returnDate != null) ? returnDate : LocalDate.now();
        return checkDate.isAfter(dueDate);
    }

    //so ngay tre: da tra thi tinh theo ngay tra, chua tra thi tinh den hom nay
    public int getOverdueDays() {
        LocalDate compareDate = (returnDate != null) ? returnDate : LocalDate.now();
        if (compareDate.isAfter(dueDate)) {
            return (int) (compareDate.toEpochDay() - dueDate.toEpochDay());
        }
        return 0;
    }

    @Override
    public String toString() {
        String returnStr = (returnDate != null) ? returnDate.format(FMT) : "Chưa trả";
        return String.format("%-8s %-20s %-20s %-12s %-12s %-12s",
                transactionID, book.getTitle(), member.getName(),
                borrowDate.format(FMT), dueDate.format(FMT), returnStr);
    }

    public String toFileLine() {
        String returnStr = (returnDate != null) ? returnDate.toString() : "null";
        return transactionID + "|" + book.getBookID() + "|" + member.getMemberID() + "|"
                + borrowDate + "|" + dueDate + "|" + returnStr;
    }
}
