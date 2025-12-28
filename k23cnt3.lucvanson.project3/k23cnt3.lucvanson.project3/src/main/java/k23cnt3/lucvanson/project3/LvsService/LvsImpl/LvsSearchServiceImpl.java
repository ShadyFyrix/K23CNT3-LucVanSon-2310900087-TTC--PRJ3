package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import k23cnt3.lucvanson.project3.LvsService.LvsSearchService;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation cho tìm kiếm toàn văn
 * Xử lý tìm kiếm trên nhiều entity
 */
@Service
@Transactional
public class LvsSearchServiceImpl implements LvsSearchService {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Tìm kiếm toàn bộ hệ thống
     * 
     * @param lvsKeyword Từ khóa tìm kiếm
     * @param lvsLimit   Giới hạn kết quả
     * @return Map kết quả tìm kiếm
     */
    @Override
    public Map<String, Object> lvsGlobalSearch(String lvsKeyword, int lvsLimit) {
        Map<String, Object> lvsResults = new HashMap<>();

        try {
            // Tìm kiếm dự án
            Pageable lvsPageable = PageRequest.of(0, lvsLimit);
            Page<LvsProject> lvsProjectPage = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);

            List<Map<String, Object>> lvsProjects = lvsProjectPage.getContent().stream()
                    .map(project -> {
                        Map<String, Object> projectMap = new HashMap<>();
                        projectMap.put("lvsProjectId", project.getLvsProjectId());
                        projectMap.put("lvsTitle", project.getLvsProjectName());
                        projectMap.put("lvsPrice", project.getLvsPrice());
                        projectMap.put("lvsDescription", project.getLvsDescription());
                        projectMap.put("lvsThumbnailUrl", project.getLvsThumbnailUrl());
                        return projectMap;
                    })
                    .collect(Collectors.toList());
            lvsResults.put("projects", lvsProjects);

            // Tìm kiếm người dùng
            Page<LvsUser> lvsUserPage = lvsUserService.lvsSearchUsers(lvsKeyword, lvsPageable);

            List<Map<String, Object>> lvsUsers = lvsUserPage.getContent().stream()
                    .map(user -> {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("lvsUserId", user.getLvsUserId());
                        userMap.put("lvsUsername", user.getLvsUsername());
                        userMap.put("lvsRole", user.getLvsRole().name());
                        userMap.put("lvsAvatarUrl", user.getLvsAvatarUrl());
                        return userMap;
                    })
                    .collect(Collectors.toList());
            lvsResults.put("users", lvsUsers);

            // Thống kê
            Map<String, Integer> lvsStats = new HashMap<>();
            lvsStats.put("projects", lvsProjects.size());
            lvsStats.put("users", lvsUsers.size());
            lvsStats.put("total", lvsProjects.size() + lvsUsers.size());
            lvsResults.put("stats", lvsStats);

        } catch (Exception e) {
            // Fallback to empty results on error
            lvsResults.put("projects", new ArrayList<>());
            lvsResults.put("users", new ArrayList<>());
            lvsResults.put("stats", Map.of("projects", 0, "users", 0, "total", 0));
        }

        return lvsResults;
    }

    /**
     * Tìm kiếm dự án nâng cao
     * 
     * @param lvsKeyword    Từ khóa
     * @param lvsCategoryId ID danh mục
     * @param lvsMinPrice   Giá tối thiểu
     * @param lvsMaxPrice   Giá tối đa
     * @param lvsSortBy     Sắp xếp theo
     * @param lvsPageable   Thông tin phân trang
     * @return Trang kết quả
     */
    @Override
    public Page<Map<String, Object>> lvsAdvancedProjectSearch(
            String lvsKeyword,
            Integer lvsCategoryId,
            Double lvsMinPrice,
            Double lvsMaxPrice,
            String lvsSortBy,
            Pageable lvsPageable) {

        // TODO: Triển khai tìm kiếm nâng cao cho dự án
        // Sử dụng Specification hoặc QueryDSL

        // Hiện tại trả về kết quả mẫu
        List<Map<String, Object>> lvsResults = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Map<String, Object> lvsProject = new HashMap<>();
            lvsProject.put("id", i + 1);
            lvsProject.put("name", "Dự án tìm kiếm " + lvsKeyword + " " + (i + 1));
            lvsProject.put("description", "Mô tả dự án " + lvsKeyword);
            lvsProject.put("price", 100000.0 + i * 10000);
            lvsProject.put("category", "Danh mục " + (i % 3 + 1));
            lvsProject.put("rating", 4.0 + (i % 5) * 0.2);
            lvsProject.put("createdAt", new Date());
            lvsResults.add(lvsProject);
        }

        // Lọc theo category
        if (lvsCategoryId != null) {
            lvsResults = lvsResults.stream()
                    .filter(p -> p.get("category").toString().contains(lvsCategoryId.toString()))
                    .toList();
        }

        // Lọc theo giá
        if (lvsMinPrice != null) {
            lvsResults = lvsResults.stream()
                    .filter(p -> (Double) p.get("price") >= lvsMinPrice)
                    .toList();
        }

        if (lvsMaxPrice != null) {
            lvsResults = lvsResults.stream()
                    .filter(p -> (Double) p.get("price") <= lvsMaxPrice)
                    .toList();
        }

        // Sắp xếp
        if (lvsSortBy != null) {
            switch (lvsSortBy.toLowerCase()) {
                case "price_asc":
                    lvsResults.sort(Comparator.comparing(p -> (Double) p.get("price")));
                    break;
                case "price_desc":
                    lvsResults.sort((p1, p2) -> ((Double) p2.get("price")).compareTo((Double) p1.get("price")));
                    break;
                case "rating_desc":
                    lvsResults.sort((p1, p2) -> ((Double) p2.get("rating")).compareTo((Double) p1.get("rating")));
                    break;
                case "newest":
                    lvsResults.sort((p1, p2) -> ((Date) p2.get("createdAt")).compareTo((Date) p1.get("createdAt")));
                    break;
            }
        }

        // TODO: Chuyển đổi thành Page
        return Page.empty(lvsPageable);
    }

    /**
     * Tìm kiếm bài viết nâng cao
     * 
     * @param lvsKeyword  Từ khóa
     * @param lvsPostType Loại bài viết
     * @param lvsTags     Tags
     * @param lvsSortBy   Sắp xếp theo
     * @param lvsPageable Thông tin phân trang
     * @return Trang kết quả
     */
    @Override
    public Page<Map<String, Object>> lvsAdvancedPostSearch(
            String lvsKeyword,
            String lvsPostType,
            String lvsTags,
            String lvsSortBy,
            Pageable lvsPageable) {

        // TODO: Triển khai tìm kiếm nâng cao cho bài viết
        return Page.empty(lvsPageable);
    }

    /**
     * Tìm kiếm user nâng cao
     * 
     * @param lvsKeyword  Từ khóa
     * @param lvsRole     Vai trò
     * @param lvsStatus   Trạng thái
     * @param lvsPageable Thông tin phân trang
     * @return Trang kết quả
     */
    @Override
    public Page<Map<String, Object>> lvsAdvancedUserSearch(
            String lvsKeyword,
            String lvsRole,
            String lvsStatus,
            Pageable lvsPageable) {

        // TODO: Triển khai tìm kiếm nâng cao cho user
        return Page.empty(lvsPageable);
    }

    /**
     * Tìm kiếm theo tag
     * 
     * @param lvsTag   Tag cần tìm
     * @param lvsLimit Giới hạn kết quả
     * @return Danh sách kết quả
     */
    @Override
    public List<Map<String, Object>> lvsSearchByTag(String lvsTag, int lvsLimit) {
        List<Map<String, Object>> lvsResults = new ArrayList<>();

        // TODO: Tìm kiếm theo tag thực tế
        for (int i = 1; i <= Math.min(10, lvsLimit); i++) {
            Map<String, Object> lvsItem = new HashMap<>();
            lvsItem.put("id", i);
            lvsItem.put("title", "Kết quả tag: " + lvsTag + " " + i);
            lvsItem.put("type", i % 2 == 0 ? "project" : "post");
            lvsItem.put("tags", Arrays.asList(lvsTag, "tag" + i));
            lvsResults.add(lvsItem);
        }

        return lvsResults;
    }

    /**
     * Tìm kiếm gợi ý
     * 
     * @param lvsKeyword Từ khóa
     * @return Danh sách gợi ý
     */
    @Override
    public List<String> lvsGetSearchSuggestions(String lvsKeyword) {
        List<String> lvsSuggestions = new ArrayList<>();

        if (lvsKeyword == null || lvsKeyword.trim().isEmpty()) {
            // Trả về gợi ý mặc định
            lvsSuggestions.add("Dự án Java");
            lvsSuggestions.add("Website bán hàng");
            lvsSuggestions.add("Ứng dụng di động");
            lvsSuggestions.add("Hướng dẫn Spring Boot");
            lvsSuggestions.add("Thiết kế UI/UX");
        } else {
            // TODO: Truy vấn từ cơ sở dữ liệu
            // Hiện tại tạo gợi ý mẫu
            String[] lvsCommonSearches = {
                    "dự án", "bài viết", "hướng dẫn", "mã nguồn",
                    "thiết kế", "phát triển", "lập trình", "android", "ios"
            };

            for (String lvsSearch : lvsCommonSearches) {
                if (lvsSearch.contains(lvsKeyword.toLowerCase()) ||
                        lvsKeyword.toLowerCase().contains(lvsSearch)) {
                    lvsSuggestions.add(lvsSearch.substring(0, 1).toUpperCase() + lvsSearch.substring(1));
                }
            }

            // Thêm từ khóa với các hậu tố
            lvsSuggestions.add(lvsKeyword + " dự án");
            lvsSuggestions.add(lvsKeyword + " tutorial");
            lvsSuggestions.add(lvsKeyword + " source code");
            lvsSuggestions.add("how to " + lvsKeyword);
        }

        // Giới hạn số lượng gợi ý
        return lvsSuggestions.stream().distinct().limit(10).toList();
    }

    /**
     * Lịch sử tìm kiếm
     * 
     * @param lvsUserId ID người dùng
     * @return Danh sách lịch sử
     */
    @Override
    public List<String> lvsGetSearchHistory(Long lvsUserId) {
        // TODO: Lưu và truy xuất lịch sử tìm kiếm từ cơ sở dữ liệu
        List<String> lvsHistory = new ArrayList<>();
        lvsHistory.add("Spring Boot");
        lvsHistory.add("ReactJS");
        lvsHistory.add("Mobile App");
        lvsHistory.add("E-commerce");
        return lvsHistory;
    }

    /**
     * Xóa lịch sử tìm kiếm
     * 
     * @param lvsUserId ID người dùng
     */
    @Override
    public void lvsClearSearchHistory(Long lvsUserId) {
        // TODO: Xóa lịch sử tìm kiếm từ cơ sở dữ liệu
        System.out.println("Xóa lịch sử tìm kiếm cho user: " + lvsUserId);
    }

    /**
     * Thống kê tìm kiếm
     * 
     * @return Map thống kê
     */
    @Override
    public Map<String, Long> lvsGetSearchStats() {
        Map<String, Long> lvsStats = new HashMap<>();

        // TODO: Thống kê từ cơ sở dữ liệu
        lvsStats.put("totalSearches", 1234L);
        lvsStats.put("todaySearches", 45L);
        lvsStats.put("popularKeyword", 156L); // Số lần tìm kiếm từ khóa phổ biến nhất

        // Top keywords
        lvsStats.put("keyword_spring_boot", 89L);
        lvsStats.put("keyword_react", 76L);
        lvsStats.put("keyword_mobile", 65L);
        lvsStats.put("keyword_web", 54L);
        lvsStats.put("keyword_design", 43L);

        return lvsStats;
    }
}
