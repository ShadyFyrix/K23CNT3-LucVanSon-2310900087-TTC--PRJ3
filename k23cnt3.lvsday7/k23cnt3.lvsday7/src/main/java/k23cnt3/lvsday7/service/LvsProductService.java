package k23cnt3.lvsday7.service;

import k23cnt3.lvsday7.entity.LvsProduct;
import k23cnt3.lvsday7.repository.LvsProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service

public class LvsProductService {
    @Autowired
    private LvsProductRepository productRepository;
    // Đọc toàn bộ dữ liệu bảng LvsProduct
    public List<LvsProduct> getAllLvsProducts() {
        return productRepository.findAll();
    }
    // Đọc dữ liệu bảng LvsProduct theo id
    public Optional<LvsProduct> findById(Long id) {
        return productRepository.findById(id);
    }
    // Cập nhật: create / update
    public LvsProduct saveLvsProduct(LvsProduct product) {
        System.out.println(product);
        return productRepository.save(product);
    }
    // Xóa product theo id
    public void deleteLvsProduct(Long id) {
        productRepository.deleteById(id);
    }
}
