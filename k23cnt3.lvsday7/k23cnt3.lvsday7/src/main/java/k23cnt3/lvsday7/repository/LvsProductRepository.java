package k23cnt3.lvsday7.repository;

import k23cnt3.lvsday7.entity.LvsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository

public interface LvsProductRepository extends
        JpaRepository<LvsProduct, Long> {
}
