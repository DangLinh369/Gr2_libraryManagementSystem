package exception;

/**
 * LibraryException - lớp cha cho mọi exception nghiệp vụ trong hệ thống.
 * Đáp ứng yêu cầu mục 5 (OOP Requirements): "Exception handling (e.g., for invalid input,
 * book not found, member not found)".
 * Dùng checked exception để bắt buộc xử lý ở tầng Menu/UI (try-catch theo Milestone 4).
 */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
