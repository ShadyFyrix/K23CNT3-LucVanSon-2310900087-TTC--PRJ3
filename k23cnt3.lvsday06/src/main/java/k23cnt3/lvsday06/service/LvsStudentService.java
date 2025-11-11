package k23cnt3.lvsday06.service;

import k23cnt3.lvsday06.dto.LvsStudentDTO;
import k23cnt3.lvsday06.entity.LvsStudent;
import k23cnt3.lvsday06.repository.LvsStudentRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class LvsStudentService {

    private LvsStudentRepository lvsStudentRepository;

    @Autowired
    public LvsStudentService(LvsStudentRepository lvsStudentRepository) {
        this.lvsStudentRepository = lvsStudentRepository;
    }

    public List<LvsStudent> findAll() {
        return lvsStudentRepository.findAll();
    }

    public Optional<LvsStudentDTO> findById(Long id) {
        Optional<LvsStudent> studentOpt = lvsStudentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return Optional.empty();
        }

        LvsStudent student = studentOpt.get();
        LvsStudentDTO studentDTO = LvsStudentDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .age(student.getAge())
                .build();
        return Optional.of(studentDTO);
    }

    public Boolean save(LvsStudentDTO studentDTO) {
        LvsStudent student = LvsStudent.builder()
                .name(studentDTO.getName())
                .email(studentDTO.getEmail())
                .age(studentDTO.getAge())
                .build();
        try {
            lvsStudentRepository.save(student);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // SỬA LẠI: Trả về LvsStudentDTO thay vì LvsStudent
    public LvsStudentDTO updateLvsStudentById(Long id, LvsStudentDTO updatedLvsStudent) {
        LvsStudent student = lvsStudentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));

        student.setName(updatedLvsStudent.getName());
        student.setEmail(updatedLvsStudent.getEmail());
        student.setAge(updatedLvsStudent.getAge());

        LvsStudent savedStudent = lvsStudentRepository.save(student);

        return LvsStudentDTO.builder()
                .id(savedStudent.getId())
                .name(savedStudent.getName())
                .email(savedStudent.getEmail())
                .age(savedStudent.getAge())
                .build();
    }

    public void deleteLvsStudent(Long id) {
        lvsStudentRepository.deleteById(id);
    }
}