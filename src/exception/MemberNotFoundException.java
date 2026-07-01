package exception;

/**
 * Ném ra khi tìm memberID không tồn tại trong hệ thống.
 * Liên quan BR3: member phải tồn tại trước khi được mượn sách.
 */
public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String memberID) {
        super("Không tìm thấy thành viên với ID: " + memberID);
    }
}
