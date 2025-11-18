package k23cnt3day8.Controller;

import k23cnt3day8.entity.Author;
import k23cnt3day8.Service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    private static final String UPLOAD_DIR = "D:/book_images/authors/";


    // -------------------- Danh sách authors --------------------
    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "authors/author-list";
    }

    // -------------------- Form thêm author mới --------------------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/author-form";
    }

    // -------------------- Lưu author (thêm mới hoặc cập nhật) --------------------
    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute Author author,
                             @RequestParam("imageAuthor") MultipartFile imageFile) {

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (!imageFile.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // Thêm timestamp để tránh trùng tên file
                String newFileName = author.getName().replaceAll("\\s+", "_")
                        + "_" + System.currentTimeMillis() + fileExtension;

                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(imageFile.getInputStream(), filePath);

                // Lưu URL để hiển thị
                author.setImgUrl("/uploads/authors/" + newFileName);
            } else if (author.getId() != null) {
                // Edit mà không upload ảnh mới -> giữ ảnh cũ
                Author existingAuthor = authorService.getAuthorById(author.getId());
                author.setImgUrl(existingAuthor.getImgUrl());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    // -------------------- Form sửa author --------------------
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        return "authors/author-form";
    }

    // -------------------- Xóa author --------------------
    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Integer id) {
        // Xóa file ảnh trước khi xóa database
        Author author = authorService.getAuthorById(id);
        if (author.getImgUrl() != null) {
            Path filePath = Paths.get(UPLOAD_DIR + author.getImgUrl().substring(author.getImgUrl().lastIndexOf("/") + 1));
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        authorService.deleteAuthor(id);
        return "redirect:/authors";
    }
}
