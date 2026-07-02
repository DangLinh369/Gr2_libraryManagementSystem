package ui;

import service.BookService;
import service.BorrowService;
import service.MemberService;
import service.ReportService;

import java.util.Scanner;

/**
 * MainUI - entry point của hệ thống, chứa Main Menu (SYSTEM INTERFACE mục 1).
 * Composition: MainUI tạo và sở hữu BookMenu, MemberMenu, BorrowMenu, ReportMenu
 * (Menu sống/chết cùng MainUI, không tồn tại độc lập).
 */
public class MainUI {

    private BookMenu bookMenu;
//    private MemberMenu memberMenu;
//    private BorrowMenu borrowMenu;
//    private ReportMenu reportMenu;

    private BookService bookService;
//    private MemberService memberService;
//    private BorrowService borrowService;
//    private ReportService reportService;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        MainUI app = new MainUI();
        app.init();
        app.start();
    }

    private void init() {
        bookService = new BookService();
//        memberService = new MemberService();
//        borrowService = new BorrowService(bookService, memberService);

        // File I/O - Milestone 4: load dữ liệu cũ khi khởi động (nếu có)
        bookService.loadFromFile();
//        memberService.loadFromFile();
//        borrowService.loadFromFile();

        bookMenu = new BookMenu(bookService, scanner);
//        memberMenu = new MemberMenu(memberService, scanner);
//        borrowMenu = new BorrowMenu(borrowService, scanner);

        // Theo UML: ReportService phụ thuộc BorrowService (để lấy transactions),
        // ReportMenu phụ thuộc ReportService, cùng pattern với các Menu khác.
//        reportService = new ReportService(borrowService);
//        reportMenu = new ReportMenu(reportService, scanner);
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
//                case 2: memberMenu.show(); break;
//                case 3: borrowMenu.show(); break;
//                case 4: reportMenu.show(); break;
                case 5: exitAndSave(); break;
                default: System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

    private void exitAndSave() {
        // File I/O - lưu toàn bộ dữ liệu trước khi thoát
        bookService.saveToFile();
//        memberService.saveToFile();
//        borrowService.saveToFile();
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