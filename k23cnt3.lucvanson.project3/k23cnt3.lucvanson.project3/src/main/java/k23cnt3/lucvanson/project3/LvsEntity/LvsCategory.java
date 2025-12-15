package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsCategory")
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

    // Getters and Setters

    public Integer getLvsCategoryId() {
        return lvsCategoryId;
    }

    public void setLvsCategoryId(Integer lvsCategoryId) {
        this.lvsCategoryId = lvsCategoryId;
    }

    public String getLvsCategoryName() {
        return lvsCategoryName;
    }

    public void setLvsCategoryName(String lvsCategoryName) {
        this.lvsCategoryName = lvsCategoryName;
    }

    public String getLvsDescription() {
        return lvsDescription;
    }

    public void setLvsDescription(String lvsDescription) {
        this.lvsDescription = lvsDescription;
    }

    public String getLvsIcon() {
        return lvsIcon;
    }

    public void setLvsIcon(String lvsIcon) {
        this.lvsIcon = lvsIcon;
    }

    public String getLvsColor() {
        return lvsColor;
    }

    public void setLvsColor(String lvsColor) {
        this.lvsColor = lvsColor;
    }

    public Integer getLvsSortOrder() {
        return lvsSortOrder;
    }

    public void setLvsSortOrder(Integer lvsSortOrder) {
        this.lvsSortOrder = lvsSortOrder;
    }

    public Boolean getLvsIsActive() {
        return lvsIsActive;
    }

    public void setLvsIsActive(Boolean lvsIsActive) {
        this.lvsIsActive = lvsIsActive;
    }

    public Integer getLvsProjectCount() {
        return lvsProjectCount;
    }

    public void setLvsProjectCount(Integer lvsProjectCount) {
        this.lvsProjectCount = lvsProjectCount;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

    public List<LvsProject> getLvsProjects() {
        return lvsProjects;
    }

    public void setLvsProjects(List<LvsProject> lvsProjects) {
        this.lvsProjects = lvsProjects;
    }

}