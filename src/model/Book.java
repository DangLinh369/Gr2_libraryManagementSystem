package model;

/**
 * Entity Book - thể hiện Encapsulation (yêu cầu mục 5 - OOP Requirements).
 * BR1: bookID là unique, không được thay đổi sau khi tạo (không có setter cho bookID).
 * BR2: title, author, genre không được rỗng (validate ở DataInputValidator trước khi addBook).
 * BR4 + BR8: quantity (stock) được kiểm tra trước khi mượn, giảm khi mượn, tăng khi trả.
 */
public class Book {
    private String bookID;
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
    private int quantity;

    public Book(String bookID, String title, String author, String genre, int publicationYear, int quantity) {
        this.bookID = bookID; // BR1: gán 1 lần duy nhất tại constructor, không có setBookID()
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.quantity = quantity;
    }

    // Không có setter cho bookID -> đảm bảo BR1 (unique ID không thể sửa)
    public String getBookID() { return bookID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAvailable() {
        return quantity > 0;
    }

    /**
     * BR8: giảm stock khi mượn sách. Gọi từ BorrowService.borrowBook().
     */
    public void decreaseQuantity() {
        if (quantity > 0) quantity--;
    }

    /**
     * BR8: tăng stock khi trả sách. Gọi từ BorrowService.returnBook().
     */
    public void increaseQuantity() {
        quantity++;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-25s %-20s %-12s %-6d %-4d",
                bookID, title, author, genre, publicationYear, quantity);
    }

    /**
     * Dùng để parse 1 dòng dữ liệu đọc từ file text (file I/O - Milestone 4).
     * Format: bookID|title|author|genre|publicationYear|quantity
     */
    public String toFileLine() {
        return bookID + "|" + title + "|" + author + "|" + genre + "|" + publicationYear + "|" + quantity;
    }

    public static Book fromFileLine(String line) {
        String[] parts = line.split("\\|");
        return new Book(parts[0], parts[1], parts[2], parts[3],
                Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }
}
