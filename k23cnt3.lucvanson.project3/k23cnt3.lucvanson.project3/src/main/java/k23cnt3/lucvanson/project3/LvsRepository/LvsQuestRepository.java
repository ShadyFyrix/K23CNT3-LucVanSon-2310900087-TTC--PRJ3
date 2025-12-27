package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest;
import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest.LvsQuestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LvsQuestRepository extends JpaRepository<LvsQuest, Long> {

    // Find active quests by type
    List<LvsQuest> findByLvsQuestTypeAndLvsIsActiveTrue(LvsQuestType lvsQuestType);

    // Find all active quests
    List<LvsQuest> findByLvsIsActiveTrue();

    // Find quests by type (including inactive)
    List<LvsQuest> findByLvsQuestType(LvsQuestType lvsQuestType);
}
