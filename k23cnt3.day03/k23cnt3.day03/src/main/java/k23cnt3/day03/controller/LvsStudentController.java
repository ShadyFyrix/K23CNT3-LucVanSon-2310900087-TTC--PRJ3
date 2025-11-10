package k23cnt3.day03.controller;

import k23cnt3.day03.entity.LvsStudent;
import k23cnt3.day03.service.LvsServiceStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LvsStudentController {

    @Autowired
    private LvsServiceStudent LvsServiceStudent;

    @GetMapping("/student-list")
    public List<LvsStudent> lvsGetAllStudents() {
        return LvsServiceStudent.lvsGetStudents();
    }

    @GetMapping("/student/{id}")
    public LvsStudent lvsGetStudentById(@PathVariable String id) {
        Long lvsParam = Long.parseLong(id);
        return LvsServiceStudent.lvsGetStudent(lvsParam);
    }

    @PostMapping("/student-add")
    public LvsStudent lvsAddStudent(@RequestBody LvsStudent lvsStudent) {
        return LvsServiceStudent.lvsAddStudent(lvsStudent);
    }

    @PutMapping("/student/{id}")
    public LvsStudent lvsUpdateStudent(@PathVariable String id, @RequestBody LvsStudent lvsStudent) {
        Long lvsParam = Long.parseLong(id);
        return LvsServiceStudent.lvsUpdateStudent(lvsParam, lvsStudent);
    }

    @DeleteMapping("/student/{id}")
    public boolean lvsDeleteStudent(@PathVariable String id) {
        Long lvsParam = Long.parseLong(id);
        return LvsServiceStudent.lvsDeleteStudent(lvsParam);
    }
}