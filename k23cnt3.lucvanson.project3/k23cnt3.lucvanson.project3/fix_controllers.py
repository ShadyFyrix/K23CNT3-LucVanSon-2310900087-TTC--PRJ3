import re
import os

# List of controller files to fix
controllers = [
    'LvsAdminCommentController.java',
    'LvsAdminOrderController.java',
    'LvsAdminTransactionController.java',
    'LvsAdminReviewController.java',
    'LvsAdminReportController.java',
    'LvsAdminPromotionController.java',
    'LvsAdminMessageController.java',
    'LvsAdminSettingController.java'
]

base_path = r'd:\K23CNT3-LucVanSon-2310900087-TTCD-PRJ3\k23cnt3.lucvanson.project3\k23cnt3.lucvanson.project3\src\main\java\k23cnt3\lucvanson\project3\LvsController\LvsAdmin'

for controller in controllers:
    file_path = os.path.join(base_path, controller)
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove HttpSession parameters
    content = re.sub(r',\s*HttpSession\s+session', '', content)
    content = re.sub(r'HttpSession\s+session,\s*', '', content)
    
    # Remove session check blocks
    content = re.sub(r'\s*// Kiểm tra quyền admin\s*\n\s*if \(!lvsUserService\.lvsIsAdmin\(session\)\) \{\s*\n\s*return "redirect:/LvsAuth/LvsLogin\.html";\s*\n\s*\}\s*\n', '\n', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f'Fixed {controller}')

print('All controllers fixed!')
