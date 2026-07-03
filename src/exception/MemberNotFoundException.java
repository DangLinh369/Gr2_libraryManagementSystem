package exception;


 //Ném ra khi tìm memberID không tồn tại trong hệ thống.
 
public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String memberID) {
        super("Không tìm thấy thành viên với ID: " + memberID);
    }
}
