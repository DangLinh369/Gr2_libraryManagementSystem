package model;

//entity sach - quantity la so ban con trong kho
public class Book {
    private String bookID;
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
    private int quantity;

    public Book(String bookID, String title, String author, String genre, int publicationYear, int quantity) {
        this.bookID = bookID; //BR1: id gan 1 lan duy nhat
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.quantity = quantity;
    }

    //khong co setter cho id (BR1)
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

    //BR8: giam kho khi muon
    public void decreaseQuantity() {
        if (quantity > 0) quantity--;
    }

    //BR8: tang kho khi tra
    public void increaseQuantity() {
        quantity++;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-25s %-20s %-12s %-6d %-4d",
                bookID, title, author, genre, publicationYear, quantity);
    }

    //1 dong trong file books.txt, cach nhau dau |
    public String toFileLine() {
        return bookID + "|" + title + "|" + author + "|" + genre + "|" + publicationYear + "|" + quantity;
    }

    //BR9 + File I/O validation: a single bad line must not crash the whole program on load.
    //If data is malformed, throw a clear IllegalArgumentException for BookService to catch and skip,
    //instead of letting a raw NumberFormatException/ArrayIndexOutOfBoundsException crash the app.
    public static Book fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Book data line is empty.");
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Book data line is missing fields (expected 6, got " + parts.length + "): " + line);
        }
        String bookID = parts[0].trim();
        String title = parts[1].trim();
        String author = parts[2].trim();
        String genre = parts[3].trim();
        if (bookID.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            throw new IllegalArgumentException("Book data line has an empty required field: " + line);
        }
        int publicationYear;
        int quantity;
        try {
            publicationYear = Integer.parseInt(parts[4].trim());
            quantity = Integer.parseInt(parts[5].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Publication year or Quantity is not a valid number: " + line);
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Negative quantity in data file: " + line);
        }
        return new Book(bookID, title, author, genre, publicationYear, quantity);
    }
}