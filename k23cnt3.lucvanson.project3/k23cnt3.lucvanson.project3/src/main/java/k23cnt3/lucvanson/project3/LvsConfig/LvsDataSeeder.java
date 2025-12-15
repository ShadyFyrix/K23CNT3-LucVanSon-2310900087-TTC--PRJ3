package k23cnt3.lucvanson.project3.LvsConfig;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LvsDataSeeder implements CommandLineRunner {

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "LvsAdmin";

        // Ensure Admin user exists and has correct password
        LvsUser admin = lvsUserRepository.findByLvsUsername(adminUsername)
                .orElse(new LvsUser());

        admin.setLvsUsername(adminUsername);
        admin.setLvsPassword(passwordEncoder.encode("123456")); // BCrypt Encoded
        admin.setLvsFullName("Administrator");
        if (admin.getLvsEmail() == null) {
            admin.setLvsEmail("LvsAdmin@example.com");
        }
        admin.setLvsRole(LvsUser.LvsRole.ADMIN);
        admin.setLvsStatus(LvsUser.LvsUserStatus.ACTIVE);

        lvsUserRepository.save(admin);
        System.out.println(">>> SEEDER: Verified Admin user (LvsAdmin/123456)");
    }
}
