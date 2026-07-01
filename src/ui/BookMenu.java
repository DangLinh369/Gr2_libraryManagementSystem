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
            System.out.println("\n----------- QUẢN LÝ SÁCH -----------");
            System.out.println("1. Thêm sách");
            System.out.println("2. Cập nhật sách");
            System.out.println("3. Xóa sách");
            System.out.println("4. Xem tất cả sách");
            System.out.println("5. Tìm kiếm sách");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            choice = readInt();

            switch (choice) {
                case 1: addBook(); break;
                case 2: updateBook(); break;
                case 3: deleteBook(); break;
                case 4: viewAllBooks(); break;
                case 5: searchBooks(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (choice != 0);
    }

    // Task B1 - Add Book
    private void addBook() {
        System.out.println("----------- THÊM SÁCH -----------");
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

        try {
            bookService.addBook(new Book(id, title, author, genre, year, qty));
            System.out.println("=> Thêm sách thành công."); // theo SYSTEM INTERFACE: "Book added successfully."
        } catch (InvalidInputException e) {
            System.out.println("=> Thất bại: " + e.getMessage());
        }
    }

    // Task B2 - Update Book
    private void updateBook() {
        System.out.println("----------- CẬP NHẬT SÁCH -----------");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine();
        try {
            Book existing = bookService.findByID(id);
            System.out.println("Current Information: " + existing);
            System.out.print("New title (Enter để giữ nguyên): ");
            String title = scanner.nextLine();
            System.out.print("New author (Enter để giữ nguyên): ");
            String author = scanner.nextLine();
            System.out.print("New genre (Enter để giữ nguyên): ");
            String genre = scanner.nextLine();
            System.out.print("New quantity (Enter để giữ nguyên): ");
            String qtyStr = scanner.nextLine();

            Book updated = new Book(
                    id,
                    title.isEmpty() ? existing.getTitle() : title,
                    author.isEmpty() ? existing.getAuthor() : author,
                    genre.isEmpty() ? existing.getGenre() : genre,
                    existing.getPublicationYear(),
                    qtyStr.isEmpty() ? existing.getQuantity() : Integer.parseInt(qtyStr)
            );
            bookService.updateBook(updated);
            System.out.println("=> Cập nhật sách thành công.");
        } catch (BookNotFoundException | InvalidInputException e) {
            System.out.println("=> Thất bại: " + e.getMessage());
        }
    }

    // Task Book Management #3 - Delete Book
    private void deleteBook() {
        System.out.print("Enter Book ID cần xóa: ");
        String id = scanner.nextLine();
        try {
            bookService.deleteBook(id);
            System.out.println("=> Xóa sách thành công.");
        } catch (BookNotFoundException e) {
            System.out.println("=> Thất bại: " + e.getMessage());
        }
    }

    // Task B3 - View All Books
    private void viewAllBooks() {
        System.out.println("----------- BOOK LIST -----------");
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("(Chưa có sách nào)");
            return;
        }
        for (Book b : books) System.out.println(b);
    }

    // Task Book Management #5 - Search
    private void searchBooks() {
        System.out.print("Nhập từ khóa (title/author/genre): ");
        String kw = scanner.nextLine();
        List<Book> result = bookService.searchBooks(kw);
        if (result.isEmpty()) {
            System.out.println("Không tìm thấy sách phù hợp.");
            return;
        }
        for (Book b : result) System.out.println(b);
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số hợp lệ: ");
            }
        }
    }
}
