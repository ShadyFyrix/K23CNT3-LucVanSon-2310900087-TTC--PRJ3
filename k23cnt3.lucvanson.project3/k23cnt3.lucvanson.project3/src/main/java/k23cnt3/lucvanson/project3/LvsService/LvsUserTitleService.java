package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsUserTitle;

/**
 * Service for calculating and updating user titles based on activity
 */
public interface LvsUserTitleService {

    /**
     * Calculate appropriate title for a user based on their monthly activity
     * 
     * @param lvsUserId User ID
     * @return Calculated title
     */
    LvsUserTitle lvsCalculateUserTitle(Long lvsUserId);

    /**
     * Update user's title based on current activity
     * 
     * @param lvsUserId User ID
     * @return Updated user
     */
    LvsUser lvsUpdateUserTitle(Long lvsUserId);

    /**
     * Update titles for all users (scheduled monthly)
     */
    void lvsUpdateAllUserTitles();

    /**
     * Get monthly activity stats for a user
     * 
     * @param lvsUserId User ID
     * @return Map with activity counts
     */
    java.util.Map<String, Long> lvsGetMonthlyActivityStats(Long lvsUserId);
}
