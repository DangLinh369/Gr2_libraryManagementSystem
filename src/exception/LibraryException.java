package exception;


 //LibraryException - lớp cha cho mọi exception nghiệp vụ trong hệ thống.
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
