package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPost;
import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsPostService;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for search results page
 */
@Controller
@RequestMapping("/LvsUser")
public class LvsUserSearchController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsPostService lvsPostService;

    /**
     * Display search results page
     */
    @GetMapping("/LvsSearch")
    public String lvsShowSearchResults(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String lvsKeyword,
            @RequestParam(value = "type", required = false, defaultValue = "all") String lvsType,
            @RequestParam(value = "page", defaultValue = "0") int lvsPage,
            Model lvsModel) {

        int lvsPageSize = 12;
        Pageable lvsPageable = PageRequest.of(lvsPage, lvsPageSize);

        // Search based on type filter
        Page<LvsProject> lvsProjectPage = null;
        Page<LvsUser> lvsUserPage = null;
        Page<LvsPost> lvsPostPage = null;

        // Only search projects if showing all or projects tab
        if ("all".equals(lvsType) || "projects".equals(lvsType)) {
            lvsProjectPage = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        }

        // Only search users if showing all or users tab
        if ("all".equals(lvsType) || "users".equals(lvsType)) {
            lvsUserPage = lvsUserService.lvsSearchUsers(lvsKeyword, lvsPageable);
        }

        // Only search posts if showing all or posts tab
        if ("all".equals(lvsType) || "posts".equals(lvsType)) {
            lvsPostPage = lvsPostService.lvsSearchPosts(lvsKeyword, lvsPageable);
        }

        // Add to model
        lvsModel.addAttribute("LvsKeyword", lvsKeyword);
        lvsModel.addAttribute("LvsProjects", lvsProjectPage != null ? lvsProjectPage.getContent() : null);
        lvsModel.addAttribute("projectPage", lvsProjectPage);
        lvsModel.addAttribute("LvsUsers", lvsUserPage != null ? lvsUserPage.getContent() : null);
        lvsModel.addAttribute("userPage", lvsUserPage);
        lvsModel.addAttribute("LvsPosts", lvsPostPage != null ? lvsPostPage.getContent() : null);
        lvsModel.addAttribute("postPage", lvsPostPage);
        lvsModel.addAttribute("LvsSearchType", lvsType);

        return "LvsAreas/LvsUsers/LvsSearchResults";
    }
}
