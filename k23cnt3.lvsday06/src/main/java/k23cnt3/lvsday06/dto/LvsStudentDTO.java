package k23cnt3.lvsday06.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class LvsStudentDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
}
