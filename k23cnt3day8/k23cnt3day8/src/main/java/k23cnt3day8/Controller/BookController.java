package k23cnt3day8.Controller;

import k23cnt3day8.entity.Author;
import k23cnt3day8.entity.Book;
import k23cnt3day8.Service.AuthorService;
import k23cnt3day8.Service.BookService;
import k23cnt3day8.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/book-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    @PostMapping("/save")
    public String saveBook(
            @ModelAttribute Book book,
            @RequestParam(required = false) List<Integer> authorIds,
            @RequestParam(required = false) Integer mainAuthorId,
            @RequestParam("imageBook") MultipartFile imageFile) {

        try {
            // Xử lý ảnh
            if (!imageFile.isEmpty()) {
                // Xóa ảnh cũ nếu có (trong trường hợp edit)
                if (book.getId() != null) {
                    Book existingBook = bookService.getBookById(book.getId());
                    if (existingBook.getImgUrl() != null) {
                        fileStorageUtil.deleteFile(existingBook.getImgUrl());
                    }
                }

                // Lưu ảnh mới vào thư mục "books"
                String imageUrl = fileStorageUtil.storeFile(imageFile, "books");
                book.setImgUrl(imageUrl);

            } else if (book.getId() != null) {
                // Giữ ảnh cũ nếu không upload ảnh mới
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

            // Gán chủ biên nếu được chọn và nằm trong danh sách authors
            if (mainAuthorId != null && authorIds.contains(mainAuthorId)) {
                Author mainAuthor = authorService.getAuthorById(mainAuthorId);
                book.setMainAuthor(mainAuthor);
            } else {
                book.setMainAuthor(null);
            }
        } else {
            book.setAuthors(new ArrayList<>());
            book.setMainAuthor(null);
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id) {
        Book book = bookService.getBookById(id);

        // Xóa file ảnh trước khi xóa book
        if (book.getImgUrl() != null) {
            fileStorageUtil.deleteFile(book.getImgUrl());
        }

        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @GetMapping("/detail/{id}")
    public String viewBookDetail(@PathVariable Integer id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "books/book-detail";
    }
}