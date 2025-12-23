package k23cnt3.lucvanson.project3.LvsConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Migration to fix old image URLs
 * Removes /lvsforum prefix from image URLs to work in both admin and user
 * contexts
 * 
 * TEMPORARILY DISABLED - Need to debug
 */
// @Component
public class LvsImageUrlMigration implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 Starting image URL migration...");

        try {
            // Fix post images
            int postImages = jdbcTemplate.update(
                    "UPDATE LvsPostImage SET lvsImageUrl = REPLACE(lvsImageUrl, '/lvsforum/uploads/', '/uploads/') " +
                            "WHERE lvsImageUrl LIKE '/lvsforum/uploads/%'");
            System.out.println("✅ Fixed " + postImages + " post images");

            // Fix comment images
            int commentImages = jdbcTemplate.update(
                    "UPDATE LvsCommentImage SET lvsImageUrl = REPLACE(lvsImageUrl, '/lvsforum/uploads/', '/uploads/') "
                            +
                            "WHERE lvsImageUrl LIKE '/lvsforum/uploads/%'");
            System.out.println("✅ Fixed " + commentImages + " comment images");

            // Fix post thumbnails
            int thumbnails = jdbcTemplate.update(
                    "UPDATE LvsPost SET lvsThumbnailUrl = REPLACE(lvsThumbnailUrl, '/lvsforum/uploads/', '/uploads/') "
                            +
                            "WHERE lvsThumbnailUrl LIKE '/lvsforum/uploads/%'");
            System.out.println("✅ Fixed " + thumbnails + " post thumbnails");

            // Fix user avatars
            int avatars = jdbcTemplate.update(
                    "UPDATE LvsUser SET lvsAvatarUrl = REPLACE(lvsAvatarUrl, '/lvsforum/uploads/', '/uploads/') " +
                            "WHERE lvsAvatarUrl LIKE '/lvsforum/uploads/%'");
            System.out.println("✅ Fixed " + avatars + " user avatars");

            // Fix project images (if column exists)
            try {
                int projectImages = jdbcTemplate.update(
                        "UPDATE LvsProject SET lvsImageUrl = REPLACE(lvsImageUrl, '/lvsforum/uploads/', '/uploads/') "
                                +
                                "WHERE lvsImageUrl LIKE '/lvsforum/uploads/%'");
                System.out.println("✅ Fixed " + projectImages + " project images");
            } catch (Exception e) {
                // Column might not exist, skip
                System.out.println("⚠️ Project images column not found, skipping");
            }

            System.out.println("🎉 Image URL migration completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error during image URL migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
