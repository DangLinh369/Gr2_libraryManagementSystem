package service;

import exception.BookNotFoundException;
import exception.InvalidInputException;
import exception.MemberNotFoundException;
import model.Book;
import model.BorrowingTransaction;
import model.Member;
import validator.DataInputValidator;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * BorrowService - xử lý nghiệp vụ mượn/trả sách, báo cáo (Reporting).
 * Phụ thuộc vào BookService và MemberService (composition - đã thể hiện trong UML).
 * BR3, BR4, BR5, BR6, BR7, BR8, BR10 đều được áp dụng tại đây.
 */
public class BorrowService {

    private static final String FILE_PATH = "data/transactions.txt";
    private ArrayList<BorrowingTransaction> txs = new ArrayList<>();
    private BookService bookService;
    private MemberService memberService;

    private int transactionCounter = 1;

    public BorrowService(BookService bookService, MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
    }

    /**
     * Task L1 - Borrow Book.
     * BR3: member phải tồn tại (findByID ném MemberNotFoundException nếu không có).
     * BR4: book phải available.
     * BR5: member chưa vượt borrow limit.
     * BR6: borrowDate hợp lệ.
     * BR8: giảm stock sách sau khi mượn thành công.
     */
    public BorrowingTransaction borrowBook(String memberID, String bookID, LocalDate borrowDate)
            throws BookNotFoundException, MemberNotFoundException, InvalidInputException {

        Member member = memberService.findByID(memberID); // BR3
        Book book = bookService.findByID(bookID);

        DataInputValidator.validateDate(borrowDate); // BR6
        int currentBorrowed = countCurrentlyBorrowedByMember(memberID);
        DataInputValidator.validateBorrow(book, member, currentBorrowed); // BR4, BR5

        String txID = "TX" + String.format("%04d", transactionCounter++);
        BorrowingTransaction tx = new BorrowingTransaction(txID, book, member, borrowDate);
        txs.add(tx);

        book.decreaseQuantity(); // BR8
        return tx;
    }

    /**
     * Task L2/L3 - Return Book.
     * BR6: returnDate phải sau borrowDate.
     * BR7: tính phí phạt nếu trả trễ qua member.calcFine() (đa hình).
     * BR8: tăng stock sách sau khi trả.
     */
    public double returnBook(String memberID, String bookID, LocalDate returnDate)
            throws BookNotFoundException, MemberNotFoundException, InvalidInputException {

        BorrowingTransaction tx = findOpenTransaction(memberID, bookID);
        if (tx == null) {
            throw new BookNotFoundException(bookID + " (không có giao dịch mượn nào đang mở cho sách này)");
        }
        DataInputValidator.validateReturnDate(tx.getBorrowDate(), returnDate); // BR6

        tx.setReturnDate(returnDate);
        tx.getBook().increaseQuantity(); // BR8

        int overdueDays = tx.getOverdueDays();
        return tx.getMember().calcFine(overdueDays); // BR7 - đa hình theo loại Member
    }

    private BorrowingTransaction findOpenTransaction(String memberID, String bookID) {
        for (BorrowingTransaction t : txs) {
            if (t.getReturnDate() == null
                    && t.getMember().getMemberID().equalsIgnoreCase(memberID)
                    && t.getBook().getBookID().equalsIgnoreCase(bookID)) {
                return t;
            }
        }
        return null;
    }

    private int countCurrentlyBorrowedByMember(String memberID) {
        int count = 0;
        for (BorrowingTransaction t : txs) {
            if (t.getReturnDate() == null && t.getMember().getMemberID().equalsIgnoreCase(memberID)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Reporting #2 - sách quá hạn (Task R1).
     */
    public List<BorrowingTransaction> getOverdueTransactions() {
        List<BorrowingTransaction> result = new ArrayList<>();
        for (BorrowingTransaction t : txs) {
            if (t.isOverdue()) result.add(t);
        }
        return result;
    }

    /**
     * Reporting #3 - sách phổ biến nhất theo BR10 (số lần mượn nhiều nhất), Task R2.
     */
    public List<Book> getPopularBooks() {
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Book> bookMap = new HashMap<>();
        for (BorrowingTransaction t : txs) {
            String id = t.getBook().getBookID();
            countMap.put(id, countMap.getOrDefault(id, 0) + 1); // BR10
            bookMap.put(id, t.getBook());
        }
        List<String> ids = new ArrayList<>(countMap.keySet());
        ids.sort((a, b) -> countMap.get(b) - countMap.get(a));
        List<Book> result = new ArrayList<>();
        for (String id : ids) result.add(bookMap.get(id));
        return result;
    }

    /**
     * Borrowing Management #4 - lịch sử mượn của 1 thành viên.
     */
    public List<BorrowingTransaction> getBorrowHistory(String memberID) {
        List<BorrowingTransaction> result = new ArrayList<>();
        for (BorrowingTransaction t : txs) {
            if (t.getMember().getMemberID().equalsIgnoreCase(memberID)) result.add(t);
        }
        return result;
    }

    /**
     * Borrowing Management #3 / Reporting #1 - tất cả sách đang được mượn (chưa trả).
     * Khác với getOverdueTransactions(): đây là tập hợp lớn hơn (bao gồm cả sách chưa quá hạn).
     */
    public List<BorrowingTransaction> getCurrentlyBorrowedTransactions() {
        List<BorrowingTransaction> result = new ArrayList<>();
        for (BorrowingTransaction t : txs) {
            if (t.getReturnDate() == null) result.add(t);
        }
        return result;
    }

    /**
     * Reporting #4 - thành viên mượn nhiều nhất.
     */
    public List<Member> getTopBorrowingMembers() {
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Member> memberMap = new HashMap<>();
        for (BorrowingTransaction t : txs) {
            String id = t.getMember().getMemberID();
            countMap.put(id, countMap.getOrDefault(id, 0) + 1);
            memberMap.put(id, t.getMember());
        }
        List<String> ids = new ArrayList<>(countMap.keySet());
        ids.sort((a, b) -> countMap.get(b) - countMap.get(a));
        List<Member> result = new ArrayList<>();
        for (String id : ids) result.add(memberMap.get(id));
        return result;
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (BorrowingTransaction t : txs) {
                bw.write(t.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi lưu file giao dịch: " + e.getMessage());
        }
    }

    /**
     * Đọc lại transaction từ file - cần bookService và memberService đã load xong trước đó
     * để map lại đúng object Book/Member (tránh tạo bản sao trùng lặp trong memory).
     */
    public void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            txs.clear();
            int maxCounter = 0;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                String txID = p[0];
                Book book = bookService.findByID(p[1]);
                Member member = memberService.findByID(p[2]);
                LocalDate borrowDate = LocalDate.parse(p[3]);
                BorrowingTransaction t = new BorrowingTransaction(txID, book, member, borrowDate);
                if (!p[5].equals("null")) {
                    t.setReturnDate(LocalDate.parse(p[5]));
                }
                txs.add(t);
                int num = Integer.parseInt(txID.replace("TX", ""));
                if (num > maxCounter) maxCounter = num;
            }
            transactionCounter = maxCounter + 1;
        } catch (IOException | BookNotFoundException | MemberNotFoundException e) {
            System.out.println("Lỗi khi đọc file giao dịch: " + e.getMessage());
        }
    }
}
