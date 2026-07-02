package services;

import Entities.Book;
import Entities.BorrowingTransaction;
import Entities.Member;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private final BorrowService borrowService;

    public ReportService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    public List<BorrowingTransaction> getCurrentlyBorrowedTransactions() {
        List<BorrowingTransaction> result = new ArrayList<>();
        for (BorrowingTransaction t : borrowService.getAllTransactions()) {
            if (t.getReturnDate() == null) {
                result.add(t);
            }
        }
        return result;
    }

    public List<BorrowingTransaction> getOverdueTransactions() {
        List<BorrowingTransaction> result = new ArrayList<>();
        for (BorrowingTransaction t : borrowService.getAllTransactions()) {
            if (t.isOverdue()) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Book> getPopularBooks() {

        List<Book> sorted = new ArrayList<>(borrowService.getBookService().getAllBooks());
        for (int i = 0; i < sorted.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getBorrowCount() > sorted.get(maxIndex).getBorrowCount()) {
                    maxIndex = j;
                }
            }
            Book temp = sorted.get(i);
            sorted.set(i, sorted.get(maxIndex));
            sorted.set(maxIndex, temp);
        }
        return sorted;
    }

    public List<Member> getTopBorrowingMembers() {
        List<Member> sorted = new ArrayList<>(borrowService.getMemberService().getAllMembers());
        for (int i = 0; i < sorted.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (countBorrowings(sorted.get(j).getMemberId())
                        > countBorrowings(sorted.get(maxIndex).getMemberId())) {
                    maxIndex = j;
                }
            }
            Member temp = sorted.get(i);
            sorted.set(i, sorted.get(maxIndex));
            sorted.set(maxIndex, temp);
        }
        return sorted;
    }

    public int countBorrowings(String memberId) {
        return borrowService.getBorrowHistory(memberId).size();
    }
}

