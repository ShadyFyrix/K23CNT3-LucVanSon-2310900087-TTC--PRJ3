package k23cn3.lvsday4.service;

import k23cn3.lvsday4.dto.LvsUsersDTO;
import k23cn3.lvsday4.entity.LvsUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LvsUsersService {
    List<LvsUser> lvsUserList = new ArrayList<>();

    public LvsUsersService() {
        // Sử dụng constructor với tham số
        lvsUserList.add(new LvsUser(1L, "user1", "pass1", "John Doe",
                LocalDate.parse("1990-01-01"), "john@example.com", "1234567890", 34, true));
        lvsUserList.add(new LvsUser(2L, "user2", "pass2", "Jane Smith",
                LocalDate.parse("1992-05-15"), "jane@example.com", "0987654321", 32, false));
        lvsUserList.add(new LvsUser(3L, "user3", "pass3", "Alice Johnson",
                LocalDate.parse("1985-11-22"), "alice@example.com", "1122334455", 39, true));
        lvsUserList.add(new LvsUser(4L, "user4", "pass4", "Bob Brown",
                LocalDate.parse("1988-03-18"), "bob@example.com", "6677889900", 36, true));
        lvsUserList.add(new LvsUser(5L, "user5", "pass5", "Charlie White",
                LocalDate.parse("1995-09-30"), "charlie@example.com", "4433221100", 29, false));
    }

    public List<LvsUser> lvsFindAll() {
        return lvsUserList;
    }

    public Boolean lvsCreate(LvsUsersDTO lvsUsersDTO) {
        try {
            LvsUser lvsUser = new LvsUser();
            lvsUser.setId((long) (lvsUserList.size() + 1));
            lvsUser.setUsername(lvsUsersDTO.getUsername());
            lvsUser.setPassword(lvsUsersDTO.getPassword());
            lvsUser.setEmail(lvsUsersDTO.getEmail());
            lvsUser.setFullName(lvsUsersDTO.getFullName());
            lvsUser.setPhone(lvsUsersDTO.getPhone());
            lvsUser.setAge(lvsUsersDTO.getAge());
            lvsUser.setBirthDay(lvsUsersDTO.getBirthday());
            lvsUser.setStatus(lvsUsersDTO.getStatus());
            lvsUserList.add(lvsUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}