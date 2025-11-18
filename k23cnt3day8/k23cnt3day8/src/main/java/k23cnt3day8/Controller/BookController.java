package k23cnt3day8.Controller;

import k23cnt3day8.entity.Author;
import k23cnt3day8.entity.Book;
import k23cnt3day8.Service.AuthorService;
import k23cnt3day8.Service.BookService;
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
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    // Thư mục lưu file ảnh (nên đặt ngoài src để chạy JAR)
    private static final String UPLOAD_DIR = "uploads/images/";

    // -------------------- Hiển thị danh sách sách --------------------
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/book-list";
    }

    // -------------------- Form thêm sách mới --------------------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    // -------------------- Lưu sách (thêm mới hoặc cập nhật) --------------------
    @PostMapping("/save")
    public String saveBook(
            @ModelAttribute Book book,
            @RequestParam(required = false) List<Integer> authorIds,
            @RequestParam("imageBook") MultipartFile imageFile) {

        // Thư mục lưu ảnh sách
        Path uploadPath = Paths.get("D:/book_images/books/"); // <-- thêm /books

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (!imageFile.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = book.getCode() + fileExtension;

                Path filePath = uploadPath.resolve(newFileName);

                // Nếu file đã tồn tại, xóa để tránh FileAlreadyExistsException
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }

                Files.copy(imageFile.getInputStream(), filePath);

                // Lưu URL để hiển thị
                book.setImgUrl("/uploads/images/" + newFileName);
            } else if (book.getId() != null) {
                Book existingBook = bookService.getBookById(book.getId());
                book.setImgUrl(existingBook.getImgUrl());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Gán authors
        if (authorIds != null && !authorIds.isEmpty()) {
            List<Author> authors = authorService.findAllById(authorIds);
            book.setAuthors(authors);
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }



    // -------------------- Form sửa sách --------------------
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    // -------------------- Xóa sách --------------------
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
