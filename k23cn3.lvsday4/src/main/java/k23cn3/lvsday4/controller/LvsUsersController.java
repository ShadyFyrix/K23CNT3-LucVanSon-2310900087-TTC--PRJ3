package k23cn3.lvsday4.controller;

import k23cn3.lvsday4.dto.LvsUsersDTO;
import k23cn3.lvsday4.entity.LvsUser;
import k23cn3.lvsday4.service.LvsUsersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class LvsUsersController {
    LvsUsersService lvsUsersService;

    @GetMapping("/user-list")
    public List<LvsUser> lvsGetAllUsers() {
        return lvsUsersService.lvsFindAll();
    }

    @PostMapping("/user-add")
    public ResponseEntity<String> lvsAddUser(@Valid @RequestBody LvsUsersDTO lvsUser) {
        boolean result = lvsUsersService.lvsCreate(lvsUser);
        if (result) {
            return ResponseEntity.ok("User created successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to create user");
        }
    }
}