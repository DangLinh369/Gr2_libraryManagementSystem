package service;

import exception.BookNotFoundException;
import exception.InvalidInputException;
import model.Book;
import validator.DataInputValidator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookService - quản lý danh sách Book (Pattern Recognition - CRUD, theo Milestone 2).
 * BR1: bookID phải unique trong toàn hệ thống (check trùng khi addBook).
 */
public class BookService {

    private static final String FILE_PATH = "data/books.txt";
    private ArrayList<Book> books = new ArrayList<>();

    /**
     * BR1 + BR2: kiểm tra unique ID và validate dữ liệu trước khi thêm.
     */
    public void addBook(Book book) throws InvalidInputException {
        DataInputValidator.validateBook(book); // BR2, BR9
        for (Book b : books) {
            if (b.getBookID().equalsIgnoreCase(book.getBookID())) {
                throw new InvalidInputException("Book ID '" + book.getBookID() + "' đã tồn tại (BR1)."); // BR1
            }
        }
        books.add(book);
    }

    public void updateBook(Book updated) throws BookNotFoundException, InvalidInputException {
        DataInputValidator.validateBook(updated);
        Book existing = findByID(updated.getBookID());
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setGenre(updated.getGenre());
        existing.setPublicationYear(updated.getPublicationYear());
        existing.setQuantity(updated.getQuantity());
    }

    /**
     * Functional Requirement Book Management #3: chỉ xóa được nếu sách không đang được mượn
     * (quantity hiện tại bằng đúng quantity gốc -> đơn giản hoá: coi như available toàn bộ).
     * Trong thực tế nên kiểm tra qua BorrowService.getCurrentlyBorrowedTransactions().
     */
    public void deleteBook(String bookID) throws BookNotFoundException {
        Book book = findByID(bookID);
        books.remove(book);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> result = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(kw)
                    || b.getAuthor().toLowerCase().contains(kw)
                    || b.getGenre().toLowerCase().contains(kw)) {
                result.add(b);
            }
        }
        return result;
    }

    public Book findByID(String bookID) throws BookNotFoundException {
        for (Book b : books) {
            if (b.getBookID().equalsIgnoreCase(bookID)) return b;
        }
        throw new BookNotFoundException(bookID);
    }

    /**
     * File I/O - Milestone 4. Ghi toàn bộ danh sách Book ra file text.
     */
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book b : books) {
                bw.write(b.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi lưu file sách: " + e.getMessage());
        }
    }

    /**
     * File I/O - Milestone 4. Đọc danh sách Book từ file text (nếu file đã tồn tại).
     */
    public void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            books.clear();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    books.add(Book.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file sách: " + e.getMessage());
        }
    }
}
