package ui;

import exception.BookNotFoundException;
import exception.InvalidInputException;
import exception.MemberNotFoundException;
import model.BorrowingTransaction;
import service.BorrowService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;


public class BorrowMenu {

    private BorrowService borrowService;
    private Scanner scanner;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BorrowMenu(BorrowService borrowService, Scanner scanner) {
        this.borrowService = borrowService;
        this.scanner = scanner;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n----------- BORROWING / RETURNING -----------");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View Borrowed Books");
            System.out.println("4. View Member History");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            choice = readInt();

            switch (choice) {
                case 1: borrowBook(); break;
                case 2: returnBook(); break;
                case 3: viewBorrowedBooks(); break;
                case 4: viewMemberHistory(); break;
                case 0: break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void borrowBook() {
        System.out.println("----------- BORROW BOOK -----------");
        System.out.print("Member ID: ");
        String memberID = scanner.nextLine();
        System.out.print("Book ID: ");
        String bookID = scanner.nextLine();
        LocalDate borrowDate = readDate("Borrow Date (DD/MM/YYYY): ");

        System.out.print("[1] Confirm [2] Cancel: ");
        int confirm = readInt();
        if (confirm != 1) {
            System.out.println("Borrow cancelled.");
            return;
        }

        try {
            BorrowingTransaction tx = borrowService.borrowBook(memberID, bookID, borrowDate);
            System.out.println("Book '" + tx.getBook().getTitle() + "' borrowed by '"
                    + tx.getMember().getName() + "' successfully. Due date: "
                    + tx.getDueDate().format(FMT));
        } catch (BookNotFoundException | MemberNotFoundException | InvalidInputException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.println("----------- RETURN BOOK -----------");
        System.out.print("Member ID: ");
        String memberID = scanner.nextLine();
        System.out.print("Book ID: ");
        String bookID = scanner.nextLine();
        LocalDate returnDate = readDate("Return Date (DD/MM/YYYY): ");

        System.out.print("[1] Confirm [2] Cancel: ");
        int confirm = readInt();
        if (confirm != 1) {
            System.out.println("Return cancelled.");
            return;
        }

        try {
            //BR7: member tu tinh phat theo loai cua no
            double fine = borrowService.returnBook(memberID, bookID, returnDate);
            if (fine <= 0) {
                System.out.println("Book returned successfully. No overdue fine.");
            } else {
                System.out.println("Book returned successfully. Overdue fine: " + (long) fine + " VND.");
            }
        } catch (BookNotFoundException | MemberNotFoundException | InvalidInputException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    private void viewBorrowedBooks() {
        System.out.println("----------- CURRENTLY BORROWED BOOKS -----------");
        List<BorrowingTransaction> list = borrowService.getCurrentlyBorrowedTransactions();
        if (list.isEmpty()) {
            System.out.println("(No borrowed books)");
            return;
        }
        printTable(list);
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    private void viewMemberHistory() {
        System.out.println("----------- BORROWING HISTORY -----------");
        System.out.print("Enter Member ID: ");
        String memberID = scanner.nextLine();
        List<BorrowingTransaction> list = borrowService.getBorrowHistory(memberID);
        if (list.isEmpty()) {
            System.out.println("No transactions found for this member.");
            return;
        }
        printTable(list);
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    private void printTable(List<BorrowingTransaction> list) {
        //toString cua transaction da can cot san
        System.out.printf("%-8s %-20s %-20s %-12s %-12s %-12s%n",
                "Txn", "Book", "Member", "Borrow", "Due", "Return");
        System.out.println("--------------------------------------------------------------------------------");
        for (BorrowingTransaction t : list) {
            System.out.println(t);
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please use DD/MM/YYYY (e.g., 28/04/2026).");
            }
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
