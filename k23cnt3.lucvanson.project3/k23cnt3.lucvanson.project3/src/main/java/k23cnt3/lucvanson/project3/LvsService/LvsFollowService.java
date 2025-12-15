package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsFollow;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface cho quản lý theo dõi
 * Xử lý follow, unfollow, lấy danh sách follower/following
 */
public interface LvsFollowService {

    // Theo dõi user
    boolean lvsFollowUser(Long lvsFollowerId, Long lvsFollowingId);

    // Bỏ theo dõi
    boolean lvsUnfollowUser(Long lvsFollowerId, Long lvsFollowingId);

    // Kiểm tra đang theo dõi
    boolean lvsIsFollowing(Long lvsFollowerId, Long lvsFollowingId);

    // Lấy danh sách follower
    Page<LvsUser> lvsGetFollowers(Long lvsUserId, Pageable lvsPageable);

    // Lấy danh sách following
    Page<LvsUser> lvsGetFollowing(Long lvsUserId, Pageable lvsPageable);

    // Lấy số lượng follower
    int lvsGetFollowerCount(Long lvsUserId);

    // Lấy số lượng following
    int lvsGetFollowingCount(Long lvsUserId);

    // Lấy gợi ý follow
    Page<LvsUser> lvsGetFollowSuggestions(Long lvsUserId, Pageable lvsPageable);

    // Xóa follower
    boolean lvsRemoveFollower(Long lvsUserId, Long lvsFollowerId);

    // Lấy thông tin follow
    LvsFollow lvsGetFollowInfo(Long lvsFollowerId, Long lvsFollowingId);

    // Lấy tất cả follow
    Page<LvsFollow> lvsGetAllFollows(Pageable lvsPageable);

    // Lấy follow theo follower
    Page<LvsFollow> lvsGetFollowsByFollower(Long lvsFollowerId, Pageable lvsPageable);

    // Lấy follow theo following
    Page<LvsFollow> lvsGetFollowsByFollowing(Long lvsFollowingId, Pageable lvsPageable);
}