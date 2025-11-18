package k23cnt3day8.Service;



import k23cnt3day8.entity.Author;
import k23cnt3day8.Repositoty.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    // Lấy tất cả tác giả
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // Lưu hoặc cập nhật tác giả
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    // Lấy tác giả theo id
    public Author getAuthorById(Integer id) {
        return authorRepository.findById(id).orElse(null);
    }

    // Xóa tác giả theo id
    public void deleteAuthor(Integer id) {
        authorRepository.deleteById(id);
    }
    // --- Thêm phương thức này để controller có thể gọi ---
    public List<Author> findAllById(List<Integer> ids) {
        return authorRepository.findAllById(ids);
    }
}

