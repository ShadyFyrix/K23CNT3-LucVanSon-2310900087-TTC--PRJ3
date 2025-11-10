package k23cn3.lvsday4.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lvs_users")
public class LvsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "birth_day")
    LocalDate birthDay;

    @Column(name = "email")
    String email;

    @Column(name = "phone")
    String phone;

    @Column(name = "age")
    int age;

    @Column(name = "status")
    Boolean status;

    public LvsUser(Long id, String username, String password, String fullName,
                   LocalDate birthDay, String email, String phone, int age, Boolean status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.status = status;
    }
}