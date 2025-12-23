package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsRole;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsUserTitle;
import k23cnt3.lucvanson.project3.LvsRepository.*;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import k23cnt3.lucvanson.project3.LvsService.LvsUserTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of User Title Service
 * Calculates and updates user titles based on monthly activity
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsUserTitleServiceImpl implements LvsUserTitleService {

    private final LvsUserService lvsUserService;
    private final LvsPostRepository lvsPostRepository;
    private final LvsCommentRepository lvsCommentRepository;
    private final LvsProjectRepository lvsProjectRepository;
    private final LvsOrderRepository lvsOrderRepository;

    @Override
    public LvsUserTitle lvsCalculateUserTitle(Long lvsUserId) {
        LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
        if (lvsUser == null) {
            return LvsUserTitle.NEWBIE;
        }

        // Staff titles take priority
        if (lvsUser.getLvsRole() == LvsRole.ADMIN) {
            return LvsUserTitle.ADMIN;
        }
        if (lvsUser.getLvsRole() == LvsRole.MODERATOR) {
            return LvsUserTitle.MODERATOR;
        }

        // Get monthly activity stats
        Map<String, Long> lvsStats = lvsGetMonthlyActivityStats(lvsUserId);
        long lvsPosts = lvsStats.get("posts");
        long lvsComments = lvsStats.get("comments");
        long lvsProjects = lvsStats.get("projects");
        long lvsSales = lvsStats.get("sales");

        // Calculate title based on activity thresholds
        // LEGEND: 50+ posts + 100+ comments + 20+ projects
        if (lvsPosts >= 50 && lvsComments >= 100 && lvsProjects >= 20) {
            return LvsUserTitle.LEGEND;
        }

        // MASTER_CREATOR: 10+ projects + 20+ sales
        if (lvsProjects >= 10 && lvsSales >= 20) {
            return LvsUserTitle.MASTER_CREATOR;
        }

        // INFLUENCER: 20+ posts + 50+ comments
        if (lvsPosts >= 20 && lvsComments >= 50) {
            return LvsUserTitle.INFLUENCER;
        }

        // SELLER: 10+ sales
        if (lvsSales >= 10) {
            return LvsUserTitle.SELLER;
        }

        // CREATOR: 3+ projects
        if (lvsProjects >= 3) {
            return LvsUserTitle.CREATOR;
        }

        // CONTRIBUTOR: 5+ posts
        if (lvsPosts >= 5) {
            return LvsUserTitle.CONTRIBUTOR;
        }

        // ACTIVE_MEMBER: 10+ comments
        if (lvsComments >= 10) {
            return LvsUserTitle.ACTIVE_MEMBER;
        }

        // Default
        return LvsUserTitle.NEWBIE;
    }

    @Override
    public LvsUser lvsUpdateUserTitle(Long lvsUserId) {
        LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
        if (lvsUser == null) {
            return null;
        }

        LvsUserTitle lvsNewTitle = lvsCalculateUserTitle(lvsUserId);
        lvsUser.setLvsTitle(lvsNewTitle);

        return lvsUserService.lvsUpdateUser(lvsUser);
    }

    @Override
    public void lvsUpdateAllUserTitles() {
        // Get all users (using pageable to avoid loading too many at once)
        Pageable lvsPageable = PageRequest.of(0, 1000);
        Page<LvsUser> lvsUsersPage = lvsUserService.lvsGetAllUsers(lvsPageable);
        List<LvsUser> lvsUsers = lvsUsersPage.getContent();

        for (LvsUser lvsUser : lvsUsers) {
            try {
                lvsUpdateUserTitle(lvsUser.getLvsUserId());
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Error updating title for user " + lvsUser.getLvsUserId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public Map<String, Long> lvsGetMonthlyActivityStats(Long lvsUserId) {
        Map<String, Long> lvsStats = new HashMap<>();

        // Calculate date range for current month
        LocalDateTime lvsMonthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime lvsMonthEnd = LocalDateTime.now();

        // Count posts created this month
        long lvsPosts = lvsPostRepository.countByLvsUser_LvsUserIdAndLvsCreatedAtBetween(
                lvsUserId, lvsMonthStart, lvsMonthEnd);

        // Count comments made this month
        long lvsComments = lvsCommentRepository.countByLvsUser_LvsUserIdAndLvsCreatedAtBetween(
                lvsUserId, lvsMonthStart, lvsMonthEnd);

        // Count projects published this month
        long lvsProjects = lvsProjectRepository.countByLvsUser_LvsUserIdAndLvsCreatedAtBetween(
                lvsUserId, lvsMonthStart, lvsMonthEnd);

        // TODO: Count completed sales this month (requires LvsOrderRepository method)
        // For now, set to 0 to avoid errors
        long lvsSales = 0;
        // long lvsSales = lvsOrderRepository.countCompletedOrdersBySellerAndDateRange(
        // lvsUserId, lvsMonthStart, lvsMonthEnd);

        lvsStats.put("posts", lvsPosts);
        lvsStats.put("comments", lvsComments);
        lvsStats.put("projects", lvsProjects);
        lvsStats.put("sales", lvsSales);

        return lvsStats;
    }
}
