package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsCategory")
@Data
public class LvsCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsCategoryId")
    private Integer lvsCategoryId;

    @Column(name = "LvsCategoryName", unique = true, nullable = false, length = 100)
    private String lvsCategoryName;

    @Column(name = "LvsDescription", length = 255)
    private String lvsDescription;

    @Column(name = "LvsIcon", length = 100)
    private String lvsIcon;

    @Column(name = "LvsColor", length = 20)
    private String lvsColor;

    @Column(name = "LvsSortOrder")
    private Integer lvsSortOrder = 0;

    @Column(name = "LvsIsActive")
    private Boolean lvsIsActive = true;

    @Column(name = "LvsProjectCount")
    private Integer lvsProjectCount = 0; // Số dự án trong danh mục (cập nhật bằng trigger)

    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Quan hệ
    @OneToMany(mappedBy = "lvsCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsProject> lvsProjects = new ArrayList<>();
}