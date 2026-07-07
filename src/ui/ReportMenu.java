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

    //sach dang duoc muon ( chua tra )
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

    //sach tre han kem tien phat
    private void overdueReport() {
        System.out.println("----------- OVERDUE BOOKS -----------");
        List<BorrowingTransaction> overdue = reportService.getOverdueTransactions();
        if (overdue.isEmpty()) {
            System.out.println("(No overdue books)");
            return;
        }
        String rowFormat = "%-9s %-26s %-10s %-16s %-12s %-13s %-11s%n";
        System.out.printf(rowFormat, "Book ID", "Title", "Member ID", "Member Name",
                "Due Date", "Days Overdue", "Fine (VND)");
        System.out.println("----------------------------------------------------");
        for (BorrowingTransaction t : overdue) {
            double fine = t.getMember().calcFine(t.getOverdueDays());
            System.out.printf(rowFormat,
                    t.getBook().getBookID(), t.getBook().getTitle(),
                    t.getMember().getMemberID(), t.getMember().getName(),
                    t.getDueDate().format(FMT), t.getOverdueDays(), (long) fine);
        }
        System.out.println("----------------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    //sach pho bien ( da xep san giam dan )
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

    //thanh vien muon nhieu nhat
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

    //xuat 2 bao cao chinh ra file
    private void exportReports() {
        String filename = "data/report.txt";
        //mo file trong try de tu dong close
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
