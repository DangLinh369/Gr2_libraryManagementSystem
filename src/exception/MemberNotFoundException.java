package exception;


//nem khi khong tim thay memberID
public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String memberID) {
        super("Không tìm thấy thành viên với ID: " + memberID);
    }
}
