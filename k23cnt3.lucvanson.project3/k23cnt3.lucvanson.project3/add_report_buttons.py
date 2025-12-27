import re

# Add report button to blog list (each post card)
blog_list_file = r"d:\K23CNT3-LucVanSon-2310900087-TTCD-PRJ3\k23cnt3.lucvanson.project3\k23cnt3.lucvanson.project3\src\main\resources\templates\LvsAreas\LvsUsers\LvsBlog\LvsBlogList.html"

with open(blog_list_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Find "View Details" button and add Report button before it
report_button_html = '''                                    <a th:href="@{/LvsUser/LvsReport/LvsCreate(lvsReportType='POST', lvsTargetId=${post.lvsPostId})}"
                                       class="text-red-400 hover:text-red-300 text-sm"
                                       title="Report this post">
                                        <i class="fas fa-flag"></i> Report
                                    </a>
'''

# Add before "View Details" or similar action buttons
if 'View Details' in content or 'Read more' in content:
    # Find the pattern and insert report button
    pattern = r'(<a[^>]*>.*?View Details.*?</a>)'
    replacement = report_button_html + r'\1'
    content = re.sub(pattern, replacement, content, flags=re.DOTALL)
    
with open(blog_list_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Added report button to blog list")
