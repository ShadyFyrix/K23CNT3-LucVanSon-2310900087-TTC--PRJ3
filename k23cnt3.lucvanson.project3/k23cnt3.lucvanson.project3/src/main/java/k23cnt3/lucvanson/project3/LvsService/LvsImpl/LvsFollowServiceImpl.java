package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsFollow;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsFollowRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service implementation cho quản lý theo dõi
 * Xử lý follow, unfollow, lấy danh sách follower/following
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsFollowServiceImpl implements LvsFollowService {

    private final LvsFollowRepository lvsFollowRepository;
    private final LvsUserRepository lvsUserRepository;

    /**
     * Theo dõi user
     * 
     * @param lvsFollowerId  ID người theo dõi
     * @param lvsFollowingId ID người được theo dõi
     * @return true nếu thành công
     */
    @Override
    public boolean lvsFollowUser(Long lvsFollowerId, Long lvsFollowingId) {
        // Không thể tự theo dõi chính mình
        if (lvsFollowerId.equals(lvsFollowingId)) {
            return false;
        }

        // Kiểm tra đã theo dõi chưa
        if (lvsIsFollowing(lvsFollowerId, lvsFollowingId)) {
            return false;
        }

        LvsUser lvsFollower = lvsUserRepository.findById(lvsFollowerId).orElse(null);
        LvsUser lvsFollowing = lvsUserRepository.findById(lvsFollowingId).orElse(null);

        if (lvsFollower == null || lvsFollowing == null) {
            return false;
        }

        LvsFollow lvsFollow = new LvsFollow();
        lvsFollow.setLvsFollower(lvsFollower);
        lvsFollow.setLvsFollowing(lvsFollowing);
        lvsFollow.setLvsCreatedAt(LocalDateTime.now());

        lvsFollowRepository.save(lvsFollow);
        return true;
    }

    /**
     * Bỏ theo dõi
     * 
     * @param lvsFollowerId  ID người theo dõi
     * @param lvsFollowingId ID người được theo dõi
     * @return true nếu thành công
     */
    @Override
    public boolean lvsUnfollowUser(Long lvsFollowerId, Long lvsFollowingId) {
        LvsFollow lvsFollow = lvsFollowRepository
                .findByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(lvsFollowerId, lvsFollowingId)
                .orElse(null);

        if (lvsFollow != null) {
            lvsFollowRepository.delete(lvsFollow);
            return true;
        }

        return false;
    }

    /**
     * Kiểm tra đang theo dõi
     * 
     * @param lvsFollowerId  ID người theo dõi
     * @param lvsFollowingId ID người được theo dõi
     * @return true nếu đang theo dõi
     */
    @Override
    public boolean lvsIsFollowing(Long lvsFollowerId, Long lvsFollowingId) {
        return lvsFollowRepository.existsByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(lvsFollowerId,
                lvsFollowingId);
    }

    /**
     * Lấy danh sách follower
     * 
     * @param lvsUserId   ID user
     * @param lvsPageable Thông tin phân trang
     * @return Trang follower
     */
    @Override
    public Page<LvsUser> lvsGetFollowers(Long lvsUserId, Pageable lvsPageable) {
        Page<LvsFollow> lvsFollows = lvsFollowRepository.findByLvsFollowing_LvsUserId(lvsUserId, lvsPageable);
        return lvsFollows.map(LvsFollow::getLvsFollower);
    }

    /**
     * Lấy danh sách following
     * 
     * @param lvsUserId   ID user
     * @param lvsPageable Thông tin phân trang
     * @return Trang following
     */
    @Override
    public Page<LvsUser> lvsGetFollowing(Long lvsUserId, Pageable lvsPageable) {
        Page<LvsFollow> lvsFollows = lvsFollowRepository.findByLvsFollower_LvsUserId(lvsUserId, lvsPageable);
        return lvsFollows.map(LvsFollow::getLvsFollowing);
    }

    /**
     * Lấy số lượng follower
     * 
     * @param lvsUserId ID user
     * @return Số lượng follower
     */
    @Override
    public int lvsGetFollowerCount(Long lvsUserId) {
        return lvsFollowRepository.countByLvsFollowing_LvsUserId(lvsUserId).intValue();
    }

    /**
     * Lấy số lượng following
     * 
     * @param lvsUserId ID user
     * @return Số lượng following
     */
    @Override
    public int lvsGetFollowingCount(Long lvsUserId) {
        return lvsFollowRepository.countByLvsFollower_LvsUserId(lvsUserId).intValue();
    }

    /**
     * Lấy gợi ý follow
     * 
     * @param lvsUserId   ID user
     * @param lvsPageable Thông tin phân trang
     * @return Trang gợi ý
     */
    @Override
    public Page<LvsUser> lvsGetFollowSuggestions(Long lvsUserId, Pageable lvsPageable) {
        // Lấy danh sách user mà user hiện tại đang follow
        List<LvsUser> lvsFollowing = lvsGetFollowing(lvsUserId, Pageable.unpaged()).getContent();

        // Tìm user mà những người đang follow cũng follow
        Set<Long> lvsSuggestionIds = new HashSet<>();

        for (LvsUser lvsUser : lvsFollowing) {
            List<LvsUser> lvsTheirFollowing = lvsGetFollowing(lvsUser.getLvsUserId(), Pageable.unpaged()).getContent();
            for (LvsUser lvsSuggestedUser : lvsTheirFollowing) {
                // Không gợi ý chính mình hoặc người đã follow
                if (!lvsSuggestedUser.getLvsUserId().equals(lvsUserId) &&
                        !lvsFollowing.contains(lvsSuggestedUser)) {
                    lvsSuggestionIds.add(lvsSuggestedUser.getLvsUserId());
                }
            }
        }

        // Nếu không đủ, lấy thêm user ngẫu nhiên
        if (lvsSuggestionIds.isEmpty()) {
            List<LvsUser> lvsAllUsers = lvsUserRepository.findAll();
            for (LvsUser lvsUser : lvsAllUsers) {
                if (!lvsUser.getLvsUserId().equals(lvsUserId) &&
                        !lvsFollowing.contains(lvsUser) &&
                        lvsSuggestionIds.size() < 10) {
                    lvsSuggestionIds.add(lvsUser.getLvsUserId());
                }
            }
        }

        // TODO: Fix findByIdIn method - currently not implemented
        return Page.empty(lvsPageable);
    }

    /**
     * Xóa follower
     * 
     * @param lvsUserId     ID user
     * @param lvsFollowerId ID follower cần xóa
     * @return true nếu thành công
     */
    @Override
    public boolean lvsRemoveFollower(Long lvsUserId, Long lvsFollowerId) {
        LvsFollow lvsFollow = lvsFollowRepository
                .findByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(lvsFollowerId, lvsUserId)
                .orElse(null);

        if (lvsFollow != null) {
            lvsFollowRepository.delete(lvsFollow);
            return true;
        }

        return false;
    }

    /**
     * Lấy thông tin follow
     * 
     * @param lvsFollowerId  ID người theo dõi
     * @param lvsFollowingId ID người được theo dõi
     * @return Thông tin follow
     */
    @Override
    public LvsFollow lvsGetFollowInfo(Long lvsFollowerId, Long lvsFollowingId) {
        return lvsFollowRepository
                .findByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(lvsFollowerId, lvsFollowingId)
                .orElse(null);
    }

    /**
     * Lấy tất cả follow với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang follow
     */
    @Override
    public Page<LvsFollow> lvsGetAllFollows(Pageable lvsPageable) {
        return lvsFollowRepository.findAll(lvsPageable);
    }

    /**
     * Lấy follow theo follower
     * 
     * @param lvsFollowerId ID người theo dõi
     * @param lvsPageable   Thông tin phân trang
     * @return Trang follow
     */
    @Override
    public Page<LvsFollow> lvsGetFollowsByFollower(Long lvsFollowerId, Pageable lvsPageable) {
        return lvsFollowRepository.findByLvsFollower_LvsUserId(lvsFollowerId, lvsPageable);
    }

    /**
     * Lấy follow theo following
     * 
     * @param lvsFollowingId ID người được theo dõi
     * @param lvsPageable    Thông tin phân trang
     * @return Trang follow
     */
    @Override
    public Page<LvsFollow> lvsGetFollowsByFollowing(Long lvsFollowingId, Pageable lvsPageable) {
        return lvsFollowRepository.findByLvsFollowing_LvsUserId(lvsFollowingId, lvsPageable);
    }
}
