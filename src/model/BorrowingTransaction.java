package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * BorrowingTransaction - entity lưu thông tin 1 lần mượn sách.
 * BR6: borrowDate phải <= ngày hiện tại; returnDate phải > borrowDate (validate ở DataInputValidator).
 * BR10: dùng để tính sách phổ biến (popular books) thông qua đếm số transaction theo bookID.
 */
public class BorrowingTransaction {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // BR7: số ngày mặc định trước khi quá hạn (due date = borrowDate + 7 ngày)
    private static final int DEFAULT_BORROW_DAYS = 7;

    private String transactionID;
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null nếu sách chưa được trả

    public BorrowingTransaction(String transactionID, Book book, Member member, LocalDate borrowDate) {
        this.transactionID = transactionID;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate; // BR6: phải là ngày hiện tại hoặc trong quá khứ (check trước khi tạo)
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
        // BR6: return date phải sau borrow date (validate trước khi gọi hàm này)
        this.returnDate = returnDate;
    }

    /**
     * Sách đang trễ hạn nếu chưa trả VÀ đã qua dueDate.
     */
    public boolean isOverdue() {
        if (returnDate != null) return false; // đã trả thì không còn overdue
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Số ngày trễ hạn - dùng để tính fine (BR7) qua Member.calcFine(overdueDays).
     * Nếu đã trả: tính theo returnDate. Nếu chưa trả: tính theo ngày hiện tại.
     */
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
