package exception;

//nem khi khong tim thay bookID
public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String bookID) {
        super("Không tìm thấy sách với ID: " + bookID);
    }
}
