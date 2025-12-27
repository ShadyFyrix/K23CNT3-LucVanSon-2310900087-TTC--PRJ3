package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest;
import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest.LvsQuestType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserQuest;
import k23cnt3.lucvanson.project3.LvsRepository.LvsQuestRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserQuestRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsQuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LvsQuestServiceImpl implements LvsQuestService {

    @Autowired
    private LvsQuestRepository lvsQuestRepository;

    @Autowired
    private LvsUserQuestRepository lvsUserQuestRepository;

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Override
    @Transactional
    public void lvsTrackActivity(LvsUser lvsUser, LvsQuestType lvsQuestType) {
        if (lvsUser == null)
            return;

        // Find all active quests of this type
        List<LvsQuest> quests = lvsQuestRepository.findByLvsQuestTypeAndLvsIsActiveTrue(lvsQuestType);

        for (LvsQuest quest : quests) {
            // Get or create user quest progress
            Optional<LvsUserQuest> existingOpt = lvsUserQuestRepository.findByLvsUserAndLvsQuest(lvsUser, quest);

            LvsUserQuest userQuest;
            if (existingOpt.isPresent()) {
                userQuest = existingOpt.get();

                // Skip if already completed
                if (userQuest.getLvsCompleted()) {
                    continue;
                }
            } else {
                // ✅ AUTO-CREATE: Create new quest progress when user first performs this
                // activity
                userQuest = new LvsUserQuest(lvsUser, quest);
            }

            // Increment count
            userQuest.setLvsCurrentCount(userQuest.getLvsCurrentCount() + 1);

            // Check if completed
            if (userQuest.getLvsCurrentCount() >= quest.getLvsTargetCount()) {
                userQuest.setLvsCompleted(true);
                userQuest.setLvsCompletedAt(LocalDateTime.now());
            }

            lvsUserQuestRepository.save(userQuest);
        }
    }

    /**
     * Initialize all active quests for a user (called on first login or manually)
     */
    @Transactional
    public void lvsInitializeUserQuests(LvsUser lvsUser) {
        List<LvsQuest> allActiveQuests = lvsQuestRepository.findByLvsIsActiveTrue();

        for (LvsQuest quest : allActiveQuests) {
            // Check if user already has this quest
            Optional<LvsUserQuest> existing = lvsUserQuestRepository.findByLvsUserAndLvsQuest(lvsUser, quest);

            if (existing.isEmpty()) {
                // Create new quest progress with 0 count
                LvsUserQuest userQuest = new LvsUserQuest(lvsUser, quest);
                lvsUserQuestRepository.save(userQuest);
            }
        }
    }

    @Override
    public List<LvsUserQuest> lvsGetUserActiveQuests(LvsUser lvsUser) {
        return lvsUserQuestRepository.findByLvsUserAndLvsCompletedFalse(lvsUser);
    }

    @Override
    public List<LvsUserQuest> lvsGetUserCompletedQuests(LvsUser lvsUser) {
        return lvsUserQuestRepository.findByLvsUserAndLvsCompletedTrue(lvsUser);
    }

    @Override
    public List<LvsUserQuest> lvsGetUnclaimedQuests(LvsUser lvsUser) {
        return lvsUserQuestRepository.findByLvsUserAndLvsCompletedTrueAndLvsClaimedFalse(lvsUser);
    }

    @Override
    @Transactional
    public void lvsClaimQuestReward(Long lvsUserQuestId) {
        Optional<LvsUserQuest> userQuestOpt = lvsUserQuestRepository.findById(lvsUserQuestId);

        if (userQuestOpt.isPresent()) {
            LvsUserQuest userQuest = userQuestOpt.get();

            // Check if completed and not claimed
            if (userQuest.getLvsCompleted() && !userQuest.getLvsClaimed()) {
                // Add coins to user
                LvsUser user = userQuest.getLvsUser();
                Integer reward = userQuest.getLvsQuest().getLvsRewardCoins();
                user.setLvsCoin(user.getLvsCoin() + reward);
                lvsUserRepository.save(user);

                // Mark as claimed
                userQuest.setLvsClaimed(true);
                userQuest.setLvsClaimedAt(LocalDateTime.now());
                lvsUserQuestRepository.save(userQuest);
            }
        }
    }

    @Override
    @Transactional
    public void lvsClaimAllQuestRewards(LvsUser lvsUser) {
        List<LvsUserQuest> unclaimed = lvsGetUnclaimedQuests(lvsUser);

        for (LvsUserQuest userQuest : unclaimed) {
            lvsClaimQuestReward(userQuest.getLvsUserQuestId());
        }
    }

    @Override
    public List<LvsQuest> lvsGetAllQuests() {
        return lvsQuestRepository.findAll();
    }

    @Override
    @Transactional
    public LvsQuest lvsSaveQuest(LvsQuest lvsQuest) {
        return lvsQuestRepository.save(lvsQuest);
    }

    @Override
    @Transactional
    public void lvsDeleteQuest(Long lvsQuestId) {
        lvsQuestRepository.deleteById(lvsQuestId);
    }
}
