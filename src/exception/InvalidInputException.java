package exception;

//nem khi input khong hop le (BR9)
public class InvalidInputException extends LibraryException {
    public InvalidInputException(String message) {
        super(message);
    }
}
