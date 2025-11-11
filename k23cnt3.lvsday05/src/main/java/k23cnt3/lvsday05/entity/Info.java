package k23cnt3.lvsday05.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Info {
    private String name;
    private String username;
    private String email;
    private String website;
}