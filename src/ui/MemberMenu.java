package ui;

import exception.InvalidInputException;
import exception.MemberNotFoundException;
import model.Member;
import model.PremiumMember;
import model.RegularMember;
import service.BorrowService;
import service.MemberService;

import java.util.List;
import java.util.Scanner;

/**
 * MemberMenu - hiển thị submenu Quản lý thành viên (Task M1-M2 + các Functional Requirement
 * Member Management #1-5). Cùng pattern với BookMenu: UI -> Service -> Entity.
 * Cần thêm BorrowService để kiểm tra BR3 khi xóa: "Remove a member
 * (only if no outstanding borrowed books)".
 */
public class MemberMenu {

    private MemberService memberService;
    private BorrowService borrowService;
    private Scanner scanner;

    public MemberMenu(MemberService memberService, BorrowService borrowService, Scanner scanner) {
        this.memberService = memberService;
        this.borrowService = borrowService;
        this.scanner = scanner;
    }

    public void show() {
        int choice;
        do {
            System.out.println("\n----------- MEMBER MANAGEMENT -----------");
            System.out.println("1. Add Member");
            System.out.println("2. Update Member");
            System.out.println("3. Delete Member");
            System.out.println("4. View All Members");
            System.out.println("5. Search Members");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            choice = readInt();

            switch (choice) {
                case 1: addMember(); break;
                case 2: updateMember(); break;
                case 3: deleteMember(); break;
                case 4: viewAllMembers(); break;
                case 5: searchMembers(); break;
                case 0: break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // Task M1 - Add Member (theo SYSTEM INTERFACE: nhập thông tin + chọn loại + confirm)
    private void addMember() {
        System.out.println("----------- ADD MEMBER -----------");
        System.out.print("Member ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.println("Membership Type: [1] Regular (limit 3, fine 5,000/day) [2] Premium (limit 5, fine 2,000/day)");
        System.out.print("Choose type: ");
        int type = readInt();

        System.out.print("[1] Save [2] Cancel: ");
        int confirm = readInt();
        if (confirm != 1) {
            System.out.println("Add member cancelled.");
            return;
        }

        try {
            // tạo đúng loại theo lựa chọn nhưng lưu bằng kiểu cha Member (upcasting)
            Member member = (type == 2)
                    ? new PremiumMember(id, name, phone, email)
                    : new RegularMember(id, name, phone, email);
            memberService.addMember(member);
            System.out.println("Member added successfully.");
        } catch (InvalidInputException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    // Task M2 - Update Member (blank = giữ giá trị cũ, giống pattern Update Book)
    private void updateMember() {
        System.out.println("----------- UPDATE MEMBER -----------");
        System.out.print("Enter Member ID: ");
        String id = scanner.nextLine();
        try {
            Member existing = memberService.findByID(id);

            System.out.println("Current Information:");
            System.out.println("Name: " + existing.getName());
            System.out.println("Phone: " + existing.getPhone());
            System.out.println("Email: " + existing.getEmail());

            System.out.print("New Name (leave blank to skip): ");
            String name = scanner.nextLine();
            System.out.print("New Phone (leave blank to skip): ");
            String phone = scanner.nextLine();
            System.out.print("New Email (leave blank to skip): ");
            String email = scanner.nextLine();

            System.out.print("[1] Update [2] Cancel: ");
            int confirm = readInt();
            if (confirm != 1) {
                System.out.println("Update cancelled.");
                return;
            }

            // Giữ nguyên loại thành viên cũ (Regular/Premium), chỉ thay thông tin liên hệ
            Member updated = (existing instanceof PremiumMember)
                    ? new PremiumMember(id,
                        name.isEmpty() ? existing.getName() : name,
                        phone.isEmpty() ? existing.getPhone() : phone,
                        email.isEmpty() ? existing.getEmail() : email)
                    : new RegularMember(id,
                        name.isEmpty() ? existing.getName() : name,
                        phone.isEmpty() ? existing.getPhone() : phone,
                        email.isEmpty() ? existing.getEmail() : email);
            memberService.updateMember(updated);
            System.out.println("Member updated successfully.");
        } catch (MemberNotFoundException | InvalidInputException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    // Functional Requirement Member Management #3 - Delete Member
    // BR3: chỉ xóa được khi thành viên KHÔNG còn sách đang mượn.
    private void deleteMember() {
        System.out.println("----------- DELETE MEMBER -----------");
        System.out.print("Enter Member ID: ");
        String id = scanner.nextLine();

        System.out.print("[1] Delete [2] Cancel: ");
        int confirm = readInt();
        if (confirm != 1) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            // BR3: không xóa thành viên còn nợ sách
            if (borrowService.countCurrentlyBorrowedByMember(id) > 0) {
                System.out.println("=> Failed: Member still has outstanding borrowed books.");
                return;
            }
            memberService.deleteMember(id);
            System.out.println("Member deleted successfully.");
        } catch (MemberNotFoundException e) {
            System.out.println("=> Failed: " + e.getMessage());
        }
    }

    // Functional Requirement Member Management #4 - View All Members
    private void viewAllMembers() {
        System.out.println("----------- MEMBER LIST -----------");
        List<Member> members = memberService.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("(No members available)");
            return;
        }
        printTable(members);
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    // Functional Requirement Member Management #5 - Search by name or ID
    private void searchMembers() {
        System.out.println("----------- SEARCH MEMBERS -----------");
        System.out.print("Enter keyword (name/ID): ");
        String kw = scanner.nextLine();
        List<Member> result = memberService.searchMembers(kw);
        if (result.isEmpty()) {
            System.out.println("No matching members found.");
            return;
        }
        printTable(result);
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    private void printTable(List<Member> members) {
        String rowFormat = "%-8s%-20s%-14s%-25s%-10s%-9s%n";
        System.out.printf(rowFormat, "ID", "Name", "Phone", "Email", "Type", "Borrowed");
        System.out.println("--------------------------------------------------------------------------");
        for (Member m : members) {
            // m có thể là Regular hoặc Premium, lấy tên class thật ra để hiển thị cột Type
            String type = m.getClass().getSimpleName().replace("Member", "");
            System.out.printf(rowFormat,
                    m.getMemberID(), m.getName(), m.getPhone(), m.getEmail(), type,
                    borrowService.countCurrentlyBorrowedByMember(m.getMemberID()));
        }
        System.out.println("--------------------------------------------------------------------------");
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
