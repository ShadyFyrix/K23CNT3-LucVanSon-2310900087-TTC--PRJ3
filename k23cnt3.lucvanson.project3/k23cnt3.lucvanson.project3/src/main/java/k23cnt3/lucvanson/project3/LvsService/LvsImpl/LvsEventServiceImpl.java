package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent.LvsEventType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserEvent;
import k23cnt3.lucvanson.project3.LvsRepository.LvsEventRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserEventRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class LvsEventServiceImpl implements LvsEventService {

    @Autowired
    private LvsEventRepository lvsEventRepository;

    @Autowired
    private LvsUserEventRepository lvsUserEventRepository;

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Override
    public LvsEvent lvsSaveEvent(LvsEvent lvsEvent) {
        return lvsEventRepository.save(lvsEvent);
    }

    @Override
    public LvsEvent lvsGetEventById(Long lvsEventId) {
        return lvsEventRepository.findById(lvsEventId).orElse(null);
    }

    @Override
    public List<LvsEvent> lvsGetAllEvents() {
        return lvsEventRepository.findAll();
    }

    @Override
    public List<LvsEvent> lvsGetActiveEvents() {
        return lvsEventRepository.findByLvsIsActiveTrue();
    }

    @Override
    public void lvsDeleteEvent(Long lvsEventId) {
        lvsEventRepository.deleteById(lvsEventId);
    }

    @Override
    public List<LvsEvent> lvsGetEventsByType(LvsEventType lvsType) {
        return lvsEventRepository.findByLvsTypeAndLvsIsActiveTrue(lvsType);
    }

    @Override
    @Transactional
    public LvsUserEvent lvsRecordEventCompletion(LvsUser lvsUser, LvsEvent lvsEvent) {
        // Create user event record
        LvsUserEvent userEvent = new LvsUserEvent(lvsUser, lvsEvent, lvsEvent.getLvsRewardCoins());
        return lvsUserEventRepository.save(userEvent);
    }

    @Override
    public boolean lvsHasCompletedEventToday(LvsUser lvsUser, LvsEvent lvsEvent) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        Optional<LvsUserEvent> existing = lvsUserEventRepository
                .findByLvsUserAndLvsEventAndLvsCompletedAtAfter(lvsUser, lvsEvent, startOfDay);
        return existing.isPresent();
    }

    @Override
    public List<LvsUserEvent> lvsGetUserEventHistory(LvsUser lvsUser) {
        return lvsUserEventRepository.findByLvsUserOrderByLvsCompletedAtDesc(lvsUser);
    }

    @Override
    public List<LvsUserEvent> lvsGetUnclaimedRewards(LvsUser lvsUser) {
        return lvsUserEventRepository.findByLvsUserAndLvsClaimedFalse(lvsUser);
    }

    @Override
    @Transactional
    public void lvsClaimReward(LvsUserEvent lvsUserEvent) {
        if (!lvsUserEvent.getLvsClaimed()) {
            // Add coins to user
            LvsUser user = lvsUserEvent.getLvsUser();
            user.setLvsCoin(user.getLvsCoin() + lvsUserEvent.getLvsCoinsEarned());
            lvsUserRepository.save(user);

            // Mark as claimed
            lvsUserEvent.setLvsClaimed(true);
            lvsUserEvent.setLvsClaimedAt(LocalDateTime.now());
            lvsUserEventRepository.save(lvsUserEvent);
        }
    }

    @Override
    @Transactional
    public void lvsClaimAllRewards(LvsUser lvsUser) {
        List<LvsUserEvent> unclaimed = lvsGetUnclaimedRewards(lvsUser);
        for (LvsUserEvent userEvent : unclaimed) {
            lvsClaimReward(userEvent);
        }
    }

    @Override
    @Transactional
    public LvsUserEvent lvsProcessDailyLogin(LvsUser lvsUser) {
        // Get or create daily login event
        List<LvsEvent> loginEvents = lvsGetEventsByType(LvsEventType.DAILY_LOGIN);
        if (loginEvents.isEmpty()) {
            // Create default daily login event
            LvsEvent loginEvent = new LvsEvent();
            loginEvent.setLvsEventName("Daily Login");
            loginEvent.setLvsDescription("Login every day to earn coins!");
            loginEvent.setLvsType(LvsEventType.DAILY_LOGIN);
            loginEvent.setLvsRewardCoins(10); // Base reward
            loginEvent.setLvsIsActive(true);
            loginEvent = lvsSaveEvent(loginEvent);
            loginEvents.add(loginEvent);
        }

        LvsEvent loginEvent = loginEvents.get(0);

        // Check if already logged in today
        if (lvsHasCompletedEventToday(lvsUser, loginEvent)) {
            return null; // Already claimed today
        }

        // Calculate streak (BEFORE recording today's login)
        int currentStreak = lvsGetLoginStreak(lvsUser) + 1; // +1 for today

        // Base reward
        int baseCoins = loginEvent.getLvsRewardCoins();
        int streakBonus = currentStreak; // +1 coin per day streak
        int totalCoins = baseCoins + streakBonus;

        // Record completion
        LvsUserEvent userEvent = new LvsUserEvent(lvsUser, loginEvent, totalCoins);
        userEvent = lvsUserEventRepository.save(userEvent);

        // Auto-claim the reward
        lvsClaimReward(userEvent);

        // Check for milestone bonus
        lvsCheckAndAwardMilestone(lvsUser, currentStreak);

        return userEvent;
    }

    /**
     * Check if user reached a milestone and award bonus
     */
    @Transactional
    private void lvsCheckAndAwardMilestone(LvsUser lvsUser, int streak) {
        // Find milestone event for this streak
        List<LvsEvent> allEvents = lvsEventRepository.findAll();

        for (LvsEvent event : allEvents) {
            if (event.getLvsMilestone() != null &&
                    event.getLvsMilestone().equals(streak) &&
                    event.getLvsIsActive()) {

                // Award milestone bonus
                int milestoneCoins = event.getLvsMilestoneCoins() != null ? event.getLvsMilestoneCoins() : 0;

                if (milestoneCoins > 0) {
                    // Create milestone achievement record
                    LvsUserEvent milestoneEvent = new LvsUserEvent(lvsUser, event, milestoneCoins);
                    milestoneEvent = lvsUserEventRepository.save(milestoneEvent);

                    // Auto-claim milestone reward
                    lvsClaimReward(milestoneEvent);
                }

                break; // Only one milestone per streak
            }
        }
    }

    @Override
    public int lvsGetLoginStreak(LvsUser lvsUser) {
        List<LvsUserEvent> loginHistory = lvsUserEventRepository
                .findByLvsUserAndLvsEvent_LvsTypeOrderByLvsCompletedAtDesc(lvsUser, LvsEventType.DAILY_LOGIN);

        if (loginHistory.isEmpty()) {
            return 0;
        }

        int streak = 1;
        LocalDate yesterday = LocalDate.now().minusDays(1);

        for (int i = 0; i < loginHistory.size() - 1; i++) {
            LocalDate currentDate = loginHistory.get(i).getLvsCompletedAt().toLocalDate();
            LocalDate previousDate = loginHistory.get(i + 1).getLvsCompletedAt().toLocalDate();

            if (currentDate.minusDays(1).equals(previousDate)) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}
