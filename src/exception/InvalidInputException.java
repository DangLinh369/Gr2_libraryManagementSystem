package exception;

/**
 * Ném ra khi input không hợp lệ (BR9: All inputs must be validated before processing).
 * Dùng bởi DataInputValidator khi validateBook(), validateMember(), validateBorrow(), validateDate().
 */
public class InvalidInputException extends LibraryException {
    public InvalidInputException(String message) {
        super(message);
    }
}
