package k23cnt3.lucvanson.project3.LvsDTO;

import k23cnt3.lucvanson.project3.LvsEntity.LvsMessage;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho conversation - chứa thông tin user và tin nhắn mới nhất
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LvsConversationDTO {
    private LvsUser lvsOtherUser;
    private LvsMessage lvsLatestMessage;
    private Integer lvsUnreadCount;
    
    public String getLvsLatestMessagePreview() {
        if (lvsLatestMessage == null) {
            return "";
        }
        
        String content = lvsLatestMessage.getLvsContent();
        if (content == null) {
            return "";
        }
        
        // Limit preview to 50 characters
        if (content.length() > 50) {
            return content.substring(0, 50) + "...";
        }
        return content;
    }
    
    public LocalDateTime getLvsLatestMessageTime() {
        if (lvsLatestMessage == null) {
            return null;
        }
        return lvsLatestMessage.getLvsCreatedAt();
    }
}
