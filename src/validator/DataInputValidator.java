package validator;

import exception.InvalidInputException;
import model.Book;
import model.Member;
import java.time.LocalDate;

public class DataInputValidator {

    /**
     * BR2: title, author, genre không được rỗng.
     * BR1: bookID không được rỗng (tính duy nhất được check riêng ở BookService vì cần
     * truy cập danh sách hiện có).
     */
    public static void validateBook(Book book) throws InvalidInputException {
        if (book.getBookID() == null || book.getBookID().trim().isEmpty()) {
            throw new InvalidInputException("Book ID không được để trống.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title không được để trống."); // BR2
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidInputException("Author không được để trống."); // BR2
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            throw new InvalidInputException("Genre không được để trống."); // BR2
        }
        if (book.getPublicationYear() <= 0) {
            throw new InvalidInputException("Publication year không hợp lệ.");
        }
        if (book.getQuantity() < 0) {
            throw new InvalidInputException("Quantity không được âm.");
        }
    }

    /**
     * BR1: memberID không được rỗng. Validate cơ bản các field còn lại.
     */
    public static void validateMember(Member member) throws InvalidInputException {
        if (member.getMemberID() == null || member.getMemberID().trim().isEmpty()) {
            throw new InvalidInputException("Member ID không được để trống.");
        }
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new InvalidInputException("Tên thành viên không được để trống.");
        }
        if (member.getEmail() != null && !member.getEmail().isEmpty()
                && !member.getEmail().contains("@")) {
            throw new InvalidInputException("Email không hợp lệ.");
        }
    }

    /**
     * BR4: sách phải available (quantity > 0).
     * BR5: member không được mượn vượt quá borrowLimit.
     * (BR3: member tồn tại được check tại MemberService.getMemberByID() trước khi gọi hàm này)
     */
    public static void validateBorrow(Book book, Member member, int currentBorrowedCount)
            throws InvalidInputException {
        if (!book.isAvailable()) {
            throw new InvalidInputException("Sách '" + book.getTitle() + "' hiện đã hết hàng."); // BR4
        }
        if (currentBorrowedCount >= member.getBorrowLimit()) {
            throw new InvalidInputException(
                    "Thành viên đã đạt giới hạn mượn (" + member.getBorrowLimit() + " sách)."); // BR5
        }
    }

    /**
     * BR6: borrowDate phải <= ngày hiện tại; returnDate phải sau borrowDate.
     */
    public static void validateDate(LocalDate borrowDate) throws InvalidInputException {
        if (borrowDate == null) {
            throw new InvalidInputException("Ngày mượn không được để trống.");
        }
        if (borrowDate.isAfter(LocalDate.now())) {
            throw new InvalidInputException("Ngày mượn không được ở tương lai."); // BR6
        }
    }

    public static void validateReturnDate(LocalDate borrowDate, LocalDate returnDate)
            throws InvalidInputException {
        if (returnDate == null) {
            throw new InvalidInputException("Ngày trả không được để trống.");
        }
        if (!returnDate.isAfter(borrowDate)) {
            throw new InvalidInputException("Ngày trả phải sau ngày mượn."); // BR6
        }
    }
}
