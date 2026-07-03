package service;

import model.Book;
import model.BorrowingTransaction;
import model.Member;

import java.util.List;

/**
 * ReportService - lớp báo cáo, không tự giữ dữ liệu
 * Mọi số liệu đều xin từ BorrowService (nơi giữ danh sách giao dịch),
 * ReportMenu chỉ cần gọi các hàm get... ở đây rồi in ra.
 */
public class ReportService {

    private final BorrowService borrowService;

    public ReportService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**sách đang được mượn (chưa trả). */
    public List<BorrowingTransaction> getCurrentlyBorrowedTransactions() {
        return borrowService.getCurrentlyBorrowedTransactions();
    }

    /**  sách trễ hạn (BR7). */
    public List<BorrowingTransaction> getOverdueTransactions() {
        return borrowService.getOverdueTransactions();
    }

    /**sách phổ biến - xếp giảm dần theo số lần mượn. */
    public List<Book> getPopularBooks() {
        return borrowService.getPopularBooks();
    }

    /**: thành viên mượn nhiều nhất - xếp giảm dần theo tổng lượt mượn. */
    public List<Member> getTopBorrowingMembers() {
        return borrowService.getTopBorrowingMembers();
    }

    /** Tổng số lượt mượn (kể cả đã trả) */
    public int countBorrowings(String memberID) {
        return borrowService.getBorrowHistory(memberID).size();
    }
}
