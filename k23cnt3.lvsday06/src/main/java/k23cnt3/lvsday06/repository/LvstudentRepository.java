package k23cnt3.lvsday06.repository;

import k23cnt3.lvsday06.entity.LvsStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LvsStudentRepository extends JpaRepository<LvsStudent, Long> {
}