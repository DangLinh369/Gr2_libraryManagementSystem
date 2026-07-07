package ui;

import service.BookService;
import service.BorrowService;
import service.MemberService;
import service.ReportService;

import java.util.Scanner;

public class MainUI {

    private BookMenu bookMenu;
    private MemberMenu memberMenu;
    private BorrowMenu borrowMenu;
    private ReportMenu reportMenu;

    private BookService bookService;
    private MemberService memberService;
    private BorrowService borrowService;
    private ReportService reportService;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        MainUI app = new MainUI();
        app.init();
        app.start();
    }

    private void init() {
        bookService = new BookService();
        memberService = new MemberService();
        borrowService = new BorrowService(bookService, memberService);

        bookService.loadFromFile();
        memberService.loadFromFile();
        borrowService.loadFromFile();

        bookMenu = new BookMenu(bookService, scanner);
        memberMenu = new MemberMenu(memberService, borrowService, scanner);
        borrowMenu = new BorrowMenu(borrowService, scanner);

        reportService = new ReportService(borrowService);
        reportMenu = new ReportMenu(reportService, scanner);
    }

    private void start() {
        int choice;
        do {
            System.out.println("\n======================================");
            System.out.println("LIBRARY MANAGEMENT SYSTEM");
            System.out.println("======================================");
            System.out.println("1. Manage Books");
            System.out.println("2. Manage Members");
            System.out.println("3. Borrowing/Returning");
            System.out.println("4. Reports");
            System.out.println("5. Exit");
            System.out.println("--------------------------------------");
            System.out.print("Choose an option: ");
            choice = readInt();

            switch (choice) {
                case 1: bookMenu.show(); break;
                case 2: memberMenu.show(); break;
                case 3: borrowMenu.show(); break;
                case 4: reportMenu.show(); break;
                case 5: exitAndSave(); break;
                default: System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

    private void exitAndSave() {
        //luu het du lieu truoc khi thoat
        bookService.saveToFile();
        memberService.saveToFile();
        borrowService.saveToFile();
        System.out.println("Data saved. Goodbye!");
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
