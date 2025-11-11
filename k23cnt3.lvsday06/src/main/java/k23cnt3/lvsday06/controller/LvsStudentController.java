package k23cnt3.lvsday06.controller;

import k23cnt3.lvsday06.dto.LvsStudentDTO;
import k23cnt3.lvsday06.entity.LvsStudent;
import k23cnt3.lvsday06.service.LvsStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/students")
public class LvsStudentController {

    @Autowired
    private LvsStudentService lvsStudentService;

    public LvsStudentController(LvsStudentService lvsStudentService) {
        this.lvsStudentService = lvsStudentService;
    }

    @GetMapping
    public String getLvsStudents(Model model) {
        model.addAttribute("students", lvsStudentService.findAll());
        return "students/student-list";
    }

    @GetMapping("/add-new")
    public String addNewLvsStudent(Model model) {
        model.addAttribute("student", new LvsStudentDTO());
        return "students/student-add";
    }

    @GetMapping("/edit/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Optional<LvsStudentDTO> studentOpt = lvsStudentService.findById(id);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid student Id:" + id);
        }
        model.addAttribute("student", studentOpt.get());
        return "students/student-edit";
    }

    @PostMapping
    public String saveLvsStudent(@ModelAttribute("student") LvsStudentDTO student) {
        lvsStudentService.save(student);
        return "redirect:/students";
    }

    @PostMapping("/update/{id}")
    public String updateLvsStudent(@PathVariable(value = "id") Long id,
                                   @ModelAttribute("student") LvsStudentDTO student) {
        lvsStudentService.updateLvsStudentById(id, student);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteLvsStudent(@PathVariable(value = "id") Long id) {
        lvsStudentService.deleteLvsStudent(id);
        return "redirect:/students";
    }
}