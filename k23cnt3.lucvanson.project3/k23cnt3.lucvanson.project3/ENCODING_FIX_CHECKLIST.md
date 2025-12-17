# Encoding Fix Checklist - 8 Modules

## Instructions
For each module below, fix Vietnamese encoding in all HTML files.

### How to Fix
1. Open file in VS Code
2. Press **Ctrl + H** (Find & Replace)
3. Replace corrupted text with correct Vietnamese
4. Click encoding in bottom-right → "Save with Encoding" → "UTF-8"
5. Save file

### Common Replacements
```
Quáº£n lÃ½ → Quản lý
HÃ nh Ä'á»™ng → Hành động
Tráº¡ng thÃ¡i → Trạng thái
NgÃ y táº¡o → Ngày tạo
TÃ¬m kiáº¿m → Tìm kiếm
ThÃªm má»›i → Thêm mới
Chi tiáº¿t → Chi tiết
Chá»‰nh sá»­a → Chỉnh sửa
XÃ³a → Xóa
Cáº­p nháº­t → Cập nhật
```

---

## 1. Comment Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsCreate.html`
- [ ] `LvsEdit.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsComment/`

---

## 2. Order Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsInvoice.html`
- [ ] `LvsUser.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsOrder/`

---

## 3. Post Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsCreate.html`
- [ ] `LvsEdit.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsPost/`

---

## 4. Transaction Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsCreate.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsTransaction/`

---

## 5. Message Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsConversation.html`
- [ ] `LvsSearch.html`
- [ ] `LvsStatistics.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsMessage/`

---

## 6. Report Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsStatistics.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsReport/`

---

## 7. Review Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsEdit.html`
- [ ] `LvsProject.html`
- [ ] `LvsUser.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsReview/`

---

## 8. Promotion Module
- [ ] `LvsList.html`
- [ ] `LvsDetail.html`
- [ ] `LvsCreate.html`
- [ ] `LvsEdit.html`
- [ ] `LvsStatistics.html`

**Path:** `src/main/resources/templates/LvsAreas/LvsAdmin/LvsPromotion/`

---

## After Fixing All Files
1. Restart Spring Boot app
2. Clear browser cache (Ctrl + Shift + Delete)
3. Test each module in browser
4. Verify Vietnamese text displays correctly
