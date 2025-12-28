package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LvsUserQuestRepository extends JpaRepository<LvsUserQuest, Long> {

    // Find user's quest progress
    Optional<LvsUserQuest> findByLvsUserAndLvsQuest(LvsUser lvsUser, LvsQuest lvsQuest);

    // Find all user's quests
    List<LvsUserQuest> findByLvsUser(LvsUser lvsUser);

    // Find user's active (incomplete) quests
    List<LvsUserQuest> findByLvsUserAndLvsCompletedFalse(LvsUser lvsUser);

    // Find user's completed quests
    List<LvsUserQuest> findByLvsUserAndLvsCompletedTrue(LvsUser lvsUser);

    // Find user's unclaimed rewards
    List<LvsUserQuest> findByLvsUserAndLvsCompletedTrueAndLvsClaimedFalse(LvsUser lvsUser);

    // Count completed quests
    long countByLvsUserAndLvsCompletedTrue(LvsUser lvsUser);

    // Check if user already has this quest
    boolean existsByLvsUserAndLvsQuest(LvsUser lvsUser, LvsQuest lvsQuest);

    // Find incomplete quests by quest type (enum)
    List<LvsUserQuest> findByLvsUserAndLvsCompletedFalseAndLvsQuest_LvsQuestType(
            LvsUser lvsUser, LvsQuest.LvsQuestType lvsQuestType);
}
