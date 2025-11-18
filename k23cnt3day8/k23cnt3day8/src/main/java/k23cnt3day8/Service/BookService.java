package k23cnt3day8.Service;

import k23cnt3day8.entity.Book;
import k23cnt3day8.Repositoty.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Lấy tất cả sách
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Lưu hoặc cập nhật sách
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Lấy sách theo id
    public Book getBookById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    // Xóa sách theo id
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }
}
