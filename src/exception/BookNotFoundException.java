package exception;

/**
 * Ném ra khi tìm bookID không tồn tại trong hệ thống (BookService, BorrowService).
 */
public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String bookID) {
        super("Không tìm thấy sách với ID: " + bookID);
    }
}
