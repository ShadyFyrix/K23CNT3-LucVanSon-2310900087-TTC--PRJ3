package k23cnt3.day03.service;

import k23cnt3.day03.entity.LvsStudent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LvsServiceStudent {
    List<LvsStudent> lvsStudents = new ArrayList<LvsStudent>();

    public LvsServiceStudent() {
        lvsStudents.addAll(Arrays.asList(
                new LvsStudent(1L, "Devmaster 1", 20, "Non", "Số 25 VNP", "0978611889", "chungtrinhj@gmail.com"),
                new LvsStudent(2L, "Devmaster 2", 25, "Non", "Số 25 VNP", "0978611889", "contact@devmaster.edu.vn"),
                new LvsStudent(3L, "Devmaster 3", 22, "Non", "Số 25 VNP", "0978611889", "chungtrinhj@gmail.com")
        ));
    }

    // Lấy toàn bộ danh sách sinh viên
    public List<LvsStudent> lvsGetStudents() {
        return lvsStudents;
    }

    // Lấy sinh viên theo id
    public LvsStudent lvsGetStudent(Long lvsId) {
        return lvsStudents.stream()
                .filter(lvsStudent -> lvsStudent.getLvsId().equals(lvsId))
                .findFirst().orElse(null);
    }

    // Thêm mới một sinh viên
    public LvsStudent lvsAddStudent(LvsStudent lvsStudent) {
        lvsStudents.add(lvsStudent);
        return lvsStudent;
    }

    // Cập nhật thông tin sinh viên
    public LvsStudent lvsUpdateStudent(Long lvsId, LvsStudent lvsStudent) {
        LvsStudent lvsCheck = lvsGetStudent(lvsId);
        if (lvsCheck == null) {
            return null;
        }

        lvsStudents.forEach(lvsItem -> {
            if (lvsItem.getLvsId().equals(lvsId)) {
                lvsItem.setLvsName(lvsStudent.getLvsName());
                lvsItem.setLvsAddress(lvsStudent.getLvsAddress());
                lvsItem.setLvsEmail(lvsStudent.getLvsEmail());
                lvsItem.setLvsPhone(lvsStudent.getLvsPhone());
                lvsItem.setLvsAge(lvsStudent.getLvsAge());
                lvsItem.setLvsGender(lvsStudent.getLvsGender());
            }
        });
        return lvsStudent;
    }

    // Xóa thông tin sinh viên
    public boolean lvsDeleteStudent(Long lvsId) {
        LvsStudent lvsCheck = lvsGetStudent(lvsId);
        return lvsStudents.remove(lvsCheck);
    }
}