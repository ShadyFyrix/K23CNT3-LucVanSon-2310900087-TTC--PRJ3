package k23cnt3.lucvanson.project3.LvsService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho tìm kiếm toàn văn
 * Xử lý tìm kiếm trên nhiều entity
 */
public interface LvsSearchService {

    // Tìm kiếm toàn bộ hệ thống
    Map<String, Object> lvsGlobalSearch(String lvsKeyword, int lvsLimit);

    // Tìm kiếm dự án nâng cao
    Page<Map<String, Object>> lvsAdvancedProjectSearch(
            String lvsKeyword,
            Integer lvsCategoryId,
            Double lvsMinPrice,
            Double lvsMaxPrice,
            String lvsSortBy,
            Pageable lvsPageable
    );

    // Tìm kiếm bài viết nâng cao
    Page<Map<String, Object>> lvsAdvancedPostSearch(
            String lvsKeyword,
            String lvsPostType,
            String lvsTags,
            String lvsSortBy,
            Pageable lvsPageable
    );

    // Tìm kiếm user nâng cao
    Page<Map<String, Object>> lvsAdvancedUserSearch(
            String lvsKeyword,
            String lvsRole,
            String lvsStatus,
            Pageable lvsPageable
    );

    // Tìm kiếm theo tag
    List<Map<String, Object>> lvsSearchByTag(String lvsTag, int lvsLimit);

    // Tìm kiếm gợi ý
    List<String> lvsGetSearchSuggestions(String lvsKeyword);

    // Lịch sử tìm kiếm
    List<String> lvsGetSearchHistory(Long lvsUserId);

    // Xóa lịch sử tìm kiếm
    void lvsClearSearchHistory(Long lvsUserId);

    // Thống kê tìm kiếm
    Map<String, Long> lvsGetSearchStats();
}