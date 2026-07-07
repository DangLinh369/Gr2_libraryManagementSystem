package validator;

import exception.InvalidInputException;
import model.Book;
import model.Member;
import java.time.LocalDate;

public class DataInputValidator {

    //BR2: book fields must not be empty (duplicate ID check lives in BookService)
    public static void validateBook(Book book) throws InvalidInputException {
        if (book.getBookID() == null || book.getBookID().trim().isEmpty()) {
            throw new InvalidInputException("Book ID must not be empty.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title must not be empty."); // BR2
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidInputException("Author must not be empty."); // BR2
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            throw new InvalidInputException("Genre must not be empty."); // BR2
        }
        if (book.getPublicationYear() <= 0) {
            throw new InvalidInputException("Publication year is not valid.");
        }
        if (book.getQuantity() < 0) {
            throw new InvalidInputException("Quantity must not be negative.");
        }
    }

    //basic check for id, name, email
    public static void validateMember(Member member) throws InvalidInputException {
        if (member.getMemberID() == null || member.getMemberID().trim().isEmpty()) {
            throw new InvalidInputException("Member ID must not be empty.");
        }
        if (!member.getMemberID().matches("M\\d+$")) {
            throw new InvalidInputException("Member ID must be in the format letter M followed by digits, e.g. M001.");
        }
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new InvalidInputException("Member name must not be empty.");
        }
        if (member.getEmail() != null && !member.getEmail().isEmpty()
                && !member.getEmail().contains("@")) {
            throw new InvalidInputException("Email is not valid.");
        }
    }

    //BR4: book must be in stock, BR5: member must not exceed borrow limit
    public static void validateBorrow(Book book, Member member, int currentBorrowedCount)
            throws InvalidInputException {
        if (!book.isAvailable()) {
            throw new InvalidInputException("Book '" + book.getTitle() + "' is currently out of stock."); // BR4
        }
        if (currentBorrowedCount >= member.getBorrowLimit()) {
            throw new InvalidInputException(
                    "Member has reached the borrowing limit (" + member.getBorrowLimit() + " books)."); // BR5
        }
    }

    //BR6: borrow date must not be in the future
    public static void validateDate(LocalDate borrowDate) throws InvalidInputException {
        if (borrowDate == null) {
            throw new InvalidInputException("Borrow date must not be empty.");
        }
        if (borrowDate.isAfter(LocalDate.now())) {
            throw new InvalidInputException("Borrow date must not be in the future."); // BR6
        }
    }

    public static void validateReturnDate(LocalDate borrowDate, LocalDate returnDate)
            throws InvalidInputException {
        if (returnDate == null) {
            throw new InvalidInputException("Return date must not be empty.");
        }
        if (!returnDate.isAfter(borrowDate)) {
            throw new InvalidInputException("Return date must be after the borrow date."); // BR6
        }
    }
}