package ui;

import exception.BookNotFoundException;
import exception.InvalidInputException;
import model.Book;
import service.BookService;

import java.util.List;
import java.util.Scanner;

/**
 * BookMenu - hiển thị submenu Quản lý sách (Task B1-B5).
 * Chỉ phụ thuộc vào BookService, không truy cập trực tiếp Book/ArrayList
 * (đúng nguyên tắc layering: UI -> Service -> Entity).
 */
public class BookMenu {

    private BookService bookService;
    private Scanner scanner;

    // Dùng chung 1 Scanner duy nhất với MainUI và các Menu khác để tránh
    // lỗi mất dữ liệu khi nhiều Scanner cùng đọc System.in.
    public BookMenu(BookService bookService, Scanner scanner) {
        this.bookService = bookService;
        this.scanner = scanner;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n----------- BOOK MANAGEMENT -----------");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. View All Books");
            System.out.println("5. Search Books");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            choice = readInt();

            switch (choice) {
                case 1: addBook(); break;
                case 2: updateBook(); break;
                case 3: deleteBook(); break;
                case 4: viewAllBooks(); break;
                case 5: searchBooks(); break;
                case 0: break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // Task B1 - Add Book
    private void addBook() {
        System.out.println("----------- ADD BOOK -----------");
        System.out.print("Book ID: ");
        String id = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Publication Year: ");
        int year = readInt();
        System.out.print("Quantity: ");
        int qty = readInt();

        // Bước xác nhận theo đúng SYSTEM INTERFACE: "[1] Save [2] Cancel"
        System.out.print("[1] Save [2] Cancel: ");
        int confirm = readInt();
        if (confirm != 1) {
            System.out.println("Add book cancelled.");
            return;
        }

        try {
            bookService.addBook(new Book(id, title, author, genre, year, qty));
            System.out.println("Book added successfully."); // theo SYSTEM INTERFACE: "Book added successfully."
        } catch (InvalidInputException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    // Task B2 - Update Book
    private void updateBook() {
        System.out.println("----------- UPDATE BOOK -----------");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine();
        try {
            Book existing = bookService.findByID(id);

            // Hiển thị Current Information nhiều dòng, đúng mẫu SYSTEM INTERFACE
            System.out.println("Current Information:");
            System.out.println("Title: " + existing.getTitle());
            System.out.println("Author: " + existing.getAuthor());
            System.out.println("Genre: " + existing.getGenre());
            System.out.println("Publication Year: " + existing.getPublicationYear());
            System.out.println("Quantity: " + existing.getQuantity());

            // Theo SYSTEM INTERFACE, Task B2 chỉ cho phép cập nhật Quantity
            System.out.print("Enter new Quantity (leave blank to skip): ");
            String qtyStr = scanner.nextLine();

            // Bước xác nhận: "[1] Update [2] Cancel"
            System.out.print("[1] Update [2] Cancel: ");
            int confirm = readInt();
            if (confirm != 1) {
                System.out.println("Update cancelled.");
                return;
            }

            Book updated = new Book(
                    id,
                    existing.getTitle(),
                    existing.getAuthor(),
                    existing.getGenre(),
                    existing.getPublicationYear(),
                    qtyStr.isEmpty() ? existing.getQuantity() : Integer.parseInt(qtyStr)
            );
            bookService.updateBook(updated);
            System.out.println("Book updated successfully.");
        } catch (BookNotFoundException | InvalidInputException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    // Task Book Management #3 - Delete Book
    // Lưu ý: đề bài (SYSTEM INTERFACE) không có mẫu chi tiết cho Delete Book,
    // nên phần này được viết theo style chung (header + confirm + output message)
    // để nhất quán với Add/Update Book.
    private void deleteBook() {
        System.out.println("----------- DELETE BOOK -----------");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine();

        System.out.print("[1] Delete [2] Cancel: ");
        int confirm = readInt();
        if (confirm != 1) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            bookService.deleteBook(id);
            System.out.println("Book deleted successfully.");
        } catch (BookNotFoundException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    // Task B3 - View All Books
    private void viewAllBooks() {
        System.out.println("----------- BOOK LIST -----------");
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("(No books available)");
            return;
        }

        // Format bảng đúng mẫu SYSTEM INTERFACE: header + dòng kẻ + dữ liệu căn cột
        // Dùng getBookID() theo đúng model.Book/BookService thực tế.
        String rowFormat = "%-6s%-20s%-20s%-12s%-6s%-5s%n";
        System.out.printf(rowFormat, "ID", "Title", "Author", "Genre", "Year", "Qty");
        System.out.println("------------------------------------------------------------------");
        for (Book b : books) {
            System.out.printf(rowFormat,
                    b.getBookID(), b.getTitle(), b.getAuthor(), b.getGenre(),
                    b.getPublicationYear(), b.getQuantity());
        }
        System.out.println("------------------------------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    // Task Book Management #5 - Search
    // Lưu ý: đề bài không có mẫu SYSTEM INTERFACE chi tiết cho Search,
    // nên phần hiển thị kết quả dùng chung format bảng như View All Books.
    private void searchBooks() {
        System.out.println("----------- SEARCH BOOKS -----------");
        System.out.print("Enter keyword (title/author/genre): ");
        String kw = scanner.nextLine();
        List<Book> result = bookService.searchBooks(kw);
        if (result.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }

        String rowFormat = "%-6s%-20s%-20s%-12s%-6s%-5s%n";
        System.out.printf(rowFormat, "ID", "Title", "Author", "Genre", "Year", "Qty");
        System.out.println("------------------------------------------------------------------");
        for (Book b : result) {
            System.out.printf(rowFormat,
                    b.getBookID(), b.getTitle(), b.getAuthor(), b.getGenre(),
                    b.getPublicationYear(), b.getQuantity());
        }
        System.out.println("------------------------------------------------------------------");
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
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