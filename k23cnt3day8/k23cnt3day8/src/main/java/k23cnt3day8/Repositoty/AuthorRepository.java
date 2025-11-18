package k23cnt3day8.Repositoty;

import k23cnt3day8.entity.Author;
import
        org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AuthorRepository extends
        JpaRepository<Author,Integer> {

}