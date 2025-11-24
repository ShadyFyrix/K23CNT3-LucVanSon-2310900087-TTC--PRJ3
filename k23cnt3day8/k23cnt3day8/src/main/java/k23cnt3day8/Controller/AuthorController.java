package k23cnt3day8.Controller;

import k23cnt3day8.entity.Author;
import k23cnt3day8.Service.AuthorService;
import k23cnt3day8.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "authors/author-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/author-form";
    }

    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute Author author,
                             @RequestParam("imageAuthor") MultipartFile imageFile) {

        try {
            if (!imageFile.isEmpty()) {
                // Xóa ảnh cũ nếu có
                if (author.getId() != null) {
                    Author existingAuthor = authorService.getAuthorById(author.getId());
                    if (existingAuthor.getImgUrl() != null) {
                        fileStorageUtil.deleteFile(existingAuthor.getImgUrl());
                    }
                }

                // Lưu ảnh mới vào thư mục "authors"
                String imageUrl = fileStorageUtil.storeFile(imageFile, "authors");
                author.setImgUrl(imageUrl);
            } else if (author.getId() != null) {
                // Giữ ảnh cũ
                Author existingAuthor = authorService.getAuthorById(author.getId());
                author.setImgUrl(existingAuthor.getImgUrl());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        return "authors/author-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Integer id) {
        Author author = authorService.getAuthorById(id);

        // Xóa file ảnh
        if (author.getImgUrl() != null) {
            fileStorageUtil.deleteFile(author.getImgUrl());
        }

        authorService.deleteAuthor(id);
        return "redirect:/authors";
    }
}