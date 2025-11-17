package k23cnt3.lvsday7.service;

import k23cnt3.lvsday7.entity.LvsCategory;
import k23cnt3.lvsday7.repository.LvsCategoryRepository; // ← ĐÃ SỬA PACKAGE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LvsCategoryService {

    @Autowired
    private LvsCategoryRepository categoryRepository;

    public LvsCategoryService(LvsCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<LvsCategory> getAllCategories() {
        List<LvsCategory> categories = categoryRepository.findAll();
        System.out.println("=== DATABASE CATEGORIES: " + categories.size() + " ===");

        // Debug: in ra tất cả categories
        for (LvsCategory category : categories) {
            System.out.println("Category: " + category.getId() + " - " +
                    category.getCategoryName() + " - " +
                    category.getCategoryStatus());
        }

        return categories;
    }

    public Optional<LvsCategory> getLvsCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public LvsCategory saveLvsCategory(LvsCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteLvsCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}