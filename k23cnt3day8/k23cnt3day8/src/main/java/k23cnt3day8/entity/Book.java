package k23cnt3day8.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book") // giữ tên bảng như trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // INT AUTO_INCREMENT
    private Integer id;

    @Column(name = "code", length = 255)
    private String code;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "imgUrl", length = 255)
    private String imgUrl;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "isActive")
    private Boolean isActive;

    @ManyToMany
    @JoinTable(
            name = "book_author", // tên bảng trung gian
            joinColumns = @JoinColumn(name = "bookId"), // cột liên kết book
            inverseJoinColumns = @JoinColumn(name = "authorId") // cột liên kết author
    )
    private List<Author> authors = new ArrayList<>();
}
