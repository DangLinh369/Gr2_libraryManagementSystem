package Program;

import Entities.Book;
import Entities.BorrowingTransaction;
import Entities.Member;
import Services.ReportService;
import Utilities.DataInput;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportMenu {

    private final ReportService reportService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportMenu(ReportService reportService) {
        this.reportService = reportService;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- REPORTS ---");
            System.out.println("1. Currently Borrowed Books");
            System.out.println("2. Overdue Books");
            System.out.println("3. Most Popular Books");
            System.out.println("4. Top Borrowing Members");
            System.out.println("5. Export Reports to File");
            System.out.println("6. Back to Main Menu");
            int choice = DataInput.getInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1: borrowedReport();   break;
                    case 2: overdueReport();    break;
                    case 3: popularReport();    break;
                    case 4: topMembersReport(); break;
                    case 5: exportReports();    break;
                    case 6: return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void borrowedReport() {
        System.out.println("----------- CURRENTLY BORROWED BOOKS -----------");
        List<BorrowingTransaction> list = reportService.getCurrentlyBorrowedTransactions();
        if (list.isEmpty()) {
            System.out.println("No borrowed books.");
            return;
        }
        System.out.println("Book ID | Title | Member | Due Date");
        System.out.println("--------------------------------------------------");
        for (BorrowingTransaction t : list) {
            System.out.println(t.getBook().getBookId() + " | " + t.getBook().getTitle()
                    + " | " + t.getMember().getName()
                    + " | " + t.getDueDate().format(FMT));
        }
    }

    private void overdueReport() {
        System.out.println("----------- OVERDUE BOOKS -----------");
        List<BorrowingTransaction> overdue = reportService.getOverdueTransactions();
        if (overdue.isEmpty()) {
            System.out.println("No overdue books.");
            return;
        }
        System.out.println("Book ID | Title | Member ID | Member Name | Due Date | Days Overdue | Fine (VND)");
        System.out.println("--------------------------------------------------");
        for (BorrowingTransaction t : overdue) {
           
            double fine = t.getMember().calcFine(t.getOverdueDays());
            System.out.println(t.getBook().getBookId() + " | " + t.getBook().getTitle()
                    + " | " + t.getMember().getMemberId() + " | " + t.getMember().getName()
                    + " | " + t.getDueDate().format(FMT) + " | " + t.getOverdueDays()
                    + " | " + (long) fine);
        }
    }

    private void popularReport() {
        System.out.println("----------- MOST POPULAR BOOKS -----------");
        System.out.println("Book ID | Title | Times Borrowed");
        System.out.println("--------------------------------------------------");
        for (Book b : reportService.getPopularBooks()) {
            System.out.println(b.getBookId() + " | " + b.getTitle() + " | " + b.getBorrowCount());
        }
    }

    private void topMembersReport() {
        System.out.println("----------- TOP BORROWING MEMBERS -----------");
        System.out.println("ID | Name | Borrowings");
        System.out.println("--------------------------------------------------");
        for (Member m : reportService.getTopBorrowingMembers()) {
            int count = reportService.countBorrowings(m.getMemberId());
            System.out.println(m.getMemberId() + " | " + m.getName() + " | " + count);
        }
    }
    
    private void exportReports() throws Exception {
        String filename = "report.txt";
        try (FileWriter w = new FileWriter(filename)) {
            w.write("OVERDUE BOOKS\n");
            for (BorrowingTransaction t : reportService.getOverdueTransactions()) {
                w.write(t.getBook().getBookId() + " - " + t.getBook().getTitle()
                        + " | Days overdue: " + t.getOverdueDays()
                        + " | Fine: " + (long) t.getMember().calcFine(t.getOverdueDays()) + " VND\n");
            }
            w.write("\nMOST POPULAR BOOKS\n");
            for (Book b : reportService.getPopularBooks()) {
                w.write(b.getBookId() + " - " + b.getTitle()
                        + " | Times borrowed: " + b.getBorrowCount() + "\n");
            }
        }
        System.out.println("Reports exported to " + filename);
    }
}
