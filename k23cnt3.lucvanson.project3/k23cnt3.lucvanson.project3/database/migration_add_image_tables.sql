-- ==================== MIGRATION: THÊM CHỨC NĂNG UPLOAD ẢNH ====================
-- Ngày tạo: 2025-12-18
-- Mục đích: Thêm bảng lưu ảnh cho Post và Comment

USE LvsForumDB;

-- 1. Tạo bảng LvsPostImage (Lưu ảnh của bài viết)
CREATE TABLE IF NOT EXISTS LvsPostImage (
    LvsImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    LvsPostId BIGINT NOT NULL,
    LvsImageUrl VARCHAR(500) NOT NULL,
    LvsImageOrder INT DEFAULT 0,
    LvsCaption VARCHAR(200),
    LvsCreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_postimage_post (LvsPostId),
    INDEX idx_postimage_order (LvsImageOrder),
    FOREIGN KEY (LvsPostId) REFERENCES LvsPost(LvsPostId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Tạo bảng LvsCommentImage (Lưu ảnh của bình luận)
CREATE TABLE IF NOT EXISTS LvsCommentImage (
    LvsImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    LvsCommentId BIGINT NOT NULL,
    LvsImageUrl VARCHAR(500) NOT NULL,
    LvsImageOrder INT DEFAULT 0,
    LvsCreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_commentimage_comment (LvsCommentId),
    INDEX idx_commentimage_order (LvsImageOrder),
    FOREIGN KEY (LvsCommentId) REFERENCES LvsComment(LvsCommentId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Thêm cột thumbnail cho Post (ảnh đại diện)
ALTER TABLE LvsPost 
ADD COLUMN IF NOT EXISTS LvsThumbnailUrl VARCHAR(500) AFTER LvsContent;

-- 4. Kiểm tra kết quả
SELECT 'Migration completed successfully!' AS Status;
DESC LvsPostImage;
DESC LvsCommentImage;
DESC LvsPost;
