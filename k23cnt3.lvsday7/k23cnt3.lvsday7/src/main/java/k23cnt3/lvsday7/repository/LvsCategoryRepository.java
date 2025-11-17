package k23cnt3.lvsday7.repository;

import k23cnt3.lvsday7.entity.LvsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LvsCategoryRepository extends JpaRepository<LvsCategory, Long> {
}