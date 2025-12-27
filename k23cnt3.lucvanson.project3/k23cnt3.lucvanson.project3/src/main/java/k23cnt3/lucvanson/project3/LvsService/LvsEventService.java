package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent.LvsEventType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserEvent;

import java.util.List;

public interface LvsEventService {

    // Event CRUD
    LvsEvent lvsSaveEvent(LvsEvent lvsEvent);

    LvsEvent lvsGetEventById(Long lvsEventId);

    List<LvsEvent> lvsGetAllEvents();

    List<LvsEvent> lvsGetActiveEvents();

    void lvsDeleteEvent(Long lvsEventId);

    // Event types
    List<LvsEvent> lvsGetEventsByType(LvsEventType lvsType);

    // User event participation
    LvsUserEvent lvsRecordEventCompletion(LvsUser lvsUser, LvsEvent lvsEvent);

    boolean lvsHasCompletedEventToday(LvsUser lvsUser, LvsEvent lvsEvent);

    List<LvsUserEvent> lvsGetUserEventHistory(LvsUser lvsUser);

    List<LvsUserEvent> lvsGetUnclaimedRewards(LvsUser lvsUser);

    // Claim rewards
    void lvsClaimReward(LvsUserEvent lvsUserEvent);

    void lvsClaimAllRewards(LvsUser lvsUser);

    // Daily login
    LvsUserEvent lvsProcessDailyLogin(LvsUser lvsUser);

    int lvsGetLoginStreak(LvsUser lvsUser);
}
