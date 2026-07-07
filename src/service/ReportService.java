package service;

import model.Book;
import model.BorrowingTransaction;
import model.Member;

import java.util.List;

public class ReportService {

    private final BorrowService borrowService;

    public ReportService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    //sach dang duoc muon ( chua tra )
    public List<BorrowingTransaction> getCurrentlyBorrowedTransactions() {
        return borrowService.getCurrentlyBorrowedTransactions();
    }

    //sach tre han
    public List<BorrowingTransaction> getOverdueTransactions() {
        return borrowService.getOverdueTransactions();
    }

    //sach pho bien
    public List<Book> getPopularBooks() {
        return borrowService.getPopularBooks();
    }

    //thanh vien muon nhieu nhat
    public List<Member> getTopBorrowingMembers() {
        return borrowService.getTopBorrowingMembers();
    }

    //tong so luot muon
    public int countBorrowings(String memberID) {
        return borrowService.getBorrowHistory(memberID).size();
    }
}
