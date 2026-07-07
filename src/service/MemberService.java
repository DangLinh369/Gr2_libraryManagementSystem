package service;

import exception.InvalidInputException;
import exception.MemberNotFoundException;
import model.Member;
import model.PremiumMember;
import model.RegularMember;
import validator.DataInputValidator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MemberService {

    private static final String FILE_PATH = "data/members.txt";
    private ArrayList<Member> members = new ArrayList<>();

    public void addMember(Member member) throws InvalidInputException {
        DataInputValidator.validateMember(member); // BR9
        for (Member m : members) {
            if (m.getMemberID().equalsIgnoreCase(member.getMemberID())) {
                throw new InvalidInputException("Member ID '" + member.getMemberID() + "' đã tồn tại."); // BR1
            }
        }
        members.add(member);
    }

    public void updateMember(Member updated) throws MemberNotFoundException, InvalidInputException {
        DataInputValidator.validateMember(updated);
        Member existing = findByID(updated.getMemberID());
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
    }

    public void deleteMember(String memberID) throws MemberNotFoundException {
        Member member = findByID(memberID);
        members.remove(member);
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> result = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (Member m : members) {
            if (m.getName().toLowerCase().contains(kw) || m.getMemberID().toLowerCase().contains(kw)) {
                result.add(m);
            }
        }
        return result;
    }

    public Member findByID(String memberID) throws MemberNotFoundException {
        for (Member m : members) {
            if (m.getMemberID().equalsIgnoreCase(memberID)) return m;
        }
        throw new MemberNotFoundException(memberID); // BR3
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Member m : members) {
                bw.write(m.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi lưu file thành viên: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            members.clear();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                String type = parts[0];
                String id = parts[1], name = parts[2], phone = parts[3], email = parts[4];
                //tao dung class con theo type luu trong file
                if (type.equals("PremiumMember")) {
                    members.add(new PremiumMember(id, name, phone, email));
                } else {
                    members.add(new RegularMember(id, name, phone, email));
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file thành viên: " + e.getMessage());
        }
    }
}
