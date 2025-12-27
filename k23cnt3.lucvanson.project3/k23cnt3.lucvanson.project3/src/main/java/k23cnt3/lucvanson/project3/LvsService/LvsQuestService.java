package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest;
import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest.LvsQuestType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserQuest;

import java.util.List;

public interface LvsQuestService {

    // Track user activities (auto-increment quest progress)
    void lvsTrackActivity(LvsUser lvsUser, LvsQuestType lvsQuestType);

    // Get user's active quests
    List<LvsUserQuest> lvsGetUserActiveQuests(LvsUser lvsUser);

    // Get user's completed quests
    List<LvsUserQuest> lvsGetUserCompletedQuests(LvsUser lvsUser);

    // Get unclaimed rewards
    List<LvsUserQuest> lvsGetUnclaimedQuests(LvsUser lvsUser);

    // Claim quest reward
    void lvsClaimQuestReward(Long lvsUserQuestId);

    // Claim all rewards
    void lvsClaimAllQuestRewards(LvsUser lvsUser);

    // Admin: Get all quests
    List<LvsQuest> lvsGetAllQuests();

    // Admin: Save quest
    LvsQuest lvsSaveQuest(LvsQuest lvsQuest);

    // Admin: Delete quest
    void lvsDeleteQuest(Long lvsQuestId);
}
