package ui;

import model.Book;
import model.BorrowingTransaction;
import model.Member;
import service.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * ReportMenu - hiển thị submenu Báo cáo (Reporting #1-4, Task R1-R2) - phần của Nam.
 * Chỉ lo phần HIỂN THỊ; dữ liệu báo cáo do ReportService cung cấp
 * (đúng nguyên tắc layering: UI -> Service, giống các Menu khác).
 */
public class ReportMenu {

    private ReportService reportService;
    private Scanner scanner;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportMenu(ReportService reportService, Scanner scanner) {
        this.reportService = reportService;
        this.scanner = scanner;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n----------- REPORTS -----------");
            System.out.println("1. Currently Borrowed Books");
            System.out.println("2. Overdue Books");
            System.out.println("3. Most Popular Books");
            System.out.println("4. Top Borrowing Members");
            System.out.println("5. Export Reports to File");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            choice = readInt();

            switch (choice) {
                case 1: borrowedReport(); break;
                case 2: overdueReport(); break;
                case 3: popularReport(); break;
                case 4: topMembersReport(); break;
                case 5: exportReports(); break;
                case 0: break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // Reporting #1 - danh sách sách đang được mượn (chưa trả)
    private void borrowedReport() {
        System.out.println("----------- CURRENTLY BORROWED BOOKS -----------");
        List<BorrowingTransaction> list = reportService.getCurrentlyBorrowedTransactions();
        if (list.isEmpty()) {
            System.out.println("(No borrowed books)");
            return;
        }
        // %-26s = can trai trong 26 ky tu -> du lieu moi dong thang cot voi tieu de
        String rowFormat = "%-9s %-26s %-20s %-12s%n";
        System.out.printf(rowFormat, "Book ID", "Title", "Member", "Due Date");
        System.out.println("----------------------------------------------------------------------");
        for (BorrowingTransaction t : list) {
            System.out.printf(rowFormat,
                    t.getBook().getBookID(), t.getBook().getTitle(),
                    t.getMember().getName(), t.getDueDate().format(FMT));
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    // Reporting #2 / Task R1 - sách trễ hạn kèm tiền phạt dự kiến
    private void overdueReport() {
        System.out.println("----------- OVERDUE BOOKS -----------");
        List<BorrowingTransaction> overdue = reportService.getOverdueTransactions();
        if (overdue.isEmpty()) {
            System.out.println("(No overdue books)");
            return;
        }
        // Cung 1 rowFormat cho tieu de va du lieu -> cac cot luon thang hang
        String rowFormat = "%-9s %-26s %-10s %-16s %-12s %-13s %-11s%n";
        System.out.printf(rowFormat, "Book ID", "Title", "Member ID", "Member Name",
                "Due Date", "Days Overdue", "Fine (VND)");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (BorrowingTransaction t : overdue) {
            // ĐA HÌNH (BR7): member thật sự (Regular/Premium) tự chọn công thức tính phạt
            double fine = t.getMember().calcFine(t.getOverdueDays());
            System.out.printf(rowFormat,
                    t.getBook().getBookID(), t.getBook().getTitle(),
                    t.getMember().getMemberID(), t.getMember().getName(),
                    t.getDueDate().format(FMT), t.getOverdueDays(), (long) fine);
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    // Reporting #3 / Task R2 (BR10) - sách phổ biến nhất (BorrowService đã xếp sẵn giảm dần)
    private void popularReport() {
        System.out.println("----------- MOST POPULAR BOOKS -----------");
        List<Book> books = reportService.getPopularBooks();
        if (books.isEmpty()) {
            System.out.println("(No borrowing transactions yet)");
            return;
        }
        String rowFormat = "%-6s %-10s %-26s%n";
        System.out.printf(rowFormat, "Rank", "Book ID", "Title");
        System.out.println("--------------------------------------------");
        int rank = 1;
        for (Book b : books) {
            System.out.printf(rowFormat, rank, b.getBookID(), b.getTitle());
            rank++;
        }
        System.out.println("--------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    // Reporting #4 - thành viên mượn nhiều nhất
    private void topMembersReport() {
        System.out.println("----------- TOP BORROWING MEMBERS -----------");
        List<Member> members = reportService.getTopBorrowingMembers();
        if (members.isEmpty()) {
            System.out.println("(No borrowing transactions yet)");
            return;
        }
        String rowFormat = "%-8s %-20s %-12s%n";
        System.out.printf(rowFormat, "ID", "Name", "Borrowings");
        System.out.println("------------------------------------------");
        for (Member m : members) {
            int count = reportService.countBorrowings(m.getMemberID());
            System.out.printf(rowFormat, m.getMemberID(), m.getName(), count);
        }
        System.out.println("------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    // File I/O (Milestone 4) - xuất báo cáo ra file text data/report.txt
    private void exportReports() {
        String filename = "data/report.txt";
        // try-with-resources: hết khối try Java TỰ ĐỘNG đóng file, kể cả khi có lỗi
        try (FileWriter w = new FileWriter(filename)) {
            w.write("OVERDUE BOOKS\n");
            for (BorrowingTransaction t : reportService.getOverdueTransactions()) {
                w.write(t.getBook().getBookID() + " - " + t.getBook().getTitle()
                        + " | Days overdue: " + t.getOverdueDays()
                        + " | Fine: " + (long) t.getMember().calcFine(t.getOverdueDays()) + " VND\n");
            }
            w.write("\nMOST POPULAR BOOKS\n");
            for (Book b : reportService.getPopularBooks()) {
                w.write(b.getBookID() + " - " + b.getTitle() + "\n");
            }
            System.out.println("Reports exported to " + filename);
        } catch (IOException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
