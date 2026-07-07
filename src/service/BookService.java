package service;

import exception.BookNotFoundException;
import exception.InvalidInputException;
import model.Book;
import validator.DataInputValidator;

import java.io.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//quan ly danh sach sach (CRUD + luu file)
public class BookService {

    private static final String FILE_PATH = "data/books.txt";

    //BR1: dinh dang Book ID quy uoc "B" + so, vd B001 (validator.DataInputValidator
    //hien chi check ID khong rong, chua check dinh dang => bo sung o day, cung nhom
    //voi cho check trung ID da co san ben duoi, giu dung pattern "Service tu lam them
    //cac check ma validator chua co", khong dong cham file DataInputValidator.java)
    private static final Pattern BOOK_ID_PATTERN = Pattern.compile("^B\\d{3,}$");

    //Gutenberg press era, used to reject unrealistic publication years like "2" or "100"
    private static final int MIN_REASONABLE_YEAR = 1400;

    private ArrayList<Book> books = new ArrayList<>();

    //validate then check duplicate id before adding
    public void addBook(Book book) throws InvalidInputException {
        if (book == null) {
            throw new InvalidInputException("Book must not be null.");
        }
        DataInputValidator.validateBook(book); // BR2, BR9
        validateBookIdFormat(book.getBookID()); // BR1
        validatePublicationYearNotInFuture(book.getPublicationYear());
        if (bookIdExists(book.getBookID())) {
            throw new InvalidInputException("Book ID '" + book.getBookID() + "' already exists."); // BR1
        }
        books.add(book);
    }

    public void updateBook(Book updated) throws BookNotFoundException, InvalidInputException {
        if (updated == null) {
            throw new InvalidInputException("Book must not be null.");
        }
        DataInputValidator.validateBook(updated);
        validatePublicationYearNotInFuture(updated.getPublicationYear());
        Book existing = findByID(updated.getBookID());
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setGenre(updated.getGenre());
        existing.setPublicationYear(updated.getPublicationYear());
        existing.setQuantity(updated.getQuantity());
    }

    //BR1: is there already a book with this ID? Exposed publicly so BookMenu can
    //re-prompt for a different ID right when the user types a duplicate one,
    //instead of only failing at the final save step.
    public boolean bookIdExists(String bookID) {
        if (bookID == null) return false;
        for (Book b : books) {
            if (b.getBookID().equalsIgnoreCase(bookID)) return true;
        }
        return false;
    }

    //BR1: Book ID format check, exposed so BookMenu can re-prompt immediately on a bad format
    public static boolean isValidBookIdFormat(String bookID) {
        return bookID != null && BOOK_ID_PATTERN.matcher(bookID.trim()).matches();
    }

    private void validateBookIdFormat(String bookID) throws InvalidInputException {
        if (!isValidBookIdFormat(bookID)) {
            throw new InvalidInputException("Book ID must be in the format letter B followed by digits, e.g. B001.");
        }
    }

    //Publication year range check, exposed so BookMenu can re-prompt immediately on a bad year
    public static boolean isValidPublicationYear(int publicationYear) {
        int currentYear = Year.now().getValue();
        return publicationYear >= MIN_REASONABLE_YEAR && publicationYear <= currentYear;
    }

    public static int minPublicationYear() {
        return MIN_REASONABLE_YEAR;
    }

    //Publication year must be reasonable: not in the future AND not unrealistically small (e.g. "2")
    //(validator.DataInputValidator currently only checks <= 0, so a year like 2 or 100 still gets through)
    private void validatePublicationYearNotInFuture(int publicationYear) throws InvalidInputException {
        int currentYear = Year.now().getValue();
        if (publicationYear > currentYear) {
            throw new InvalidInputException("Publication year cannot be later than the current year (" + currentYear + ").");
        }
        if (publicationYear < MIN_REASONABLE_YEAR) {
            throw new InvalidInputException(
                    "Publication year is not valid (must be a full 4-digit year, from " + MIN_REASONABLE_YEAR + " onward).");
        }
    }

    public void deleteBook(String bookID) throws BookNotFoundException {
        Book book = findByID(bookID);
        //NOTE: per the requirements, a book should only be removable if it is NOT currently borrowed.
        //BookService has no access to borrowing-transaction data (that lives in BorrowService), so this
        //check cannot be enforced here. Flagged for the team, not fixed here to avoid breaking the architecture.
        books.remove(book);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> result = new ArrayList<>();
        if (keyword == null) {
            return result;
        }
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
        if (bookID != null) {
            for (Book b : books) {
                if (b.getBookID().equalsIgnoreCase(bookID)) return b;
            }
        }
        throw new BookNotFoundException(bookID);
    }

    //save the whole list to file
    public void saveToFile() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // create the "data/" folder if missing, to avoid IOException
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Book b : books) {
                bw.write(b.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving book file: " + e.getMessage());
        }
    }

    //read the file if it exists - File I/O validation: one bad line must not crash the whole app
    public void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            books.clear();
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                try {
                    books.add(Book.fromFileLine(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping line " + lineNumber + " in book file (invalid data): " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading book file: " + e.getMessage());
        }
    }
}