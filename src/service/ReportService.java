package service;

import model.Book;
import model.BorrowingTransaction;
import model.Member;

import java.util.List;

/**
 * ReportService - lớp báo cáo, không tự giữ dữ liệu nào cả.
 * Mọi số liệu đều xin từ BorrowService (nơi giữ danh sách giao dịch),
 * ReportMenu chỉ cần gọi các hàm get... ở đây rồi in ra.
 */
public class ReportService {

    private final BorrowService borrowService;

    public ReportService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /** Báo cáo 1 (Reporting #1): sách đang được mượn (chưa trả). */
    public List<BorrowingTransaction> getCurrentlyBorrowedTransactions() {
        return borrowService.getCurrentlyBorrowedTransactions();
    }

    /** Báo cáo 2 (Reporting #2 / Task R1): sách trễ hạn (BR7). */
    public List<BorrowingTransaction> getOverdueTransactions() {
        return borrowService.getOverdueTransactions();
    }

    /** Báo cáo 3 (Reporting #3 / Task R2, BR10): sách phổ biến - xếp giảm dần theo số lần mượn. */
    public List<Book> getPopularBooks() {
        return borrowService.getPopularBooks();
    }

    /** Báo cáo 4 (Reporting #4): thành viên mượn nhiều nhất - xếp giảm dần theo tổng lượt mượn. */
    public List<Member> getTopBorrowingMembers() {
        return borrowService.getTopBorrowingMembers();
    }

    /** Tổng số lượt mượn (kể cả đã trả) của 1 thành viên - dùng cho báo cáo 4. */
    public int countBorrowings(String memberID) {
        return borrowService.getBorrowHistory(memberID).size();
    }
}
