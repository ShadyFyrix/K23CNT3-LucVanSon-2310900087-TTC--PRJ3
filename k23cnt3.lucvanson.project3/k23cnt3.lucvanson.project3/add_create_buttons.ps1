# Add Create buttons to List templates
$templates = @(
    @{folder='LvsComment'; title='Quản lý Bình luận'; btn='Tạo bình luận mới'},
    @{folder='LvsOrder'; title='Quản lý Đơn hàng'; btn='Tạo đơn hàng mới'},
    @{folder='LvsTransaction'; title='Quản lý Giao dịch'; btn='Tạo giao dịch mới'},
    @{folder='LvsReview'; title='Quản lý Đánh giá'; btn='Tạo đánh giá mới'},
    @{folder='LvsReport'; title='Quản lý Báo cáo'; btn='Tạo báo cáo mới'},
    @{folder='LvsPromotion'; title='Quản lý Khuyến mãi'; btn='Tạo khuyến mãi mới'},
    @{folder='LvsMessage'; title='Quản lý Tin nhắn'; btn='Tạo tin nhắn mới'},
    @{folder='LvsSetting'; title='Cài đặt hệ thống'; btn='Tạo cài đặt mới'},
    @{folder='LvsProject'; title='Quản lý Dự án'; btn='Tạo dự án mới'}
)

foreach ($t in $templates) {
    $path = "src\main\resources\templates\LvsAreas\LvsAdmin\$($t.folder)\LvsList.html"
    
    if (Test-Path $path) {
        $content = Get-Content $path -Raw
        
        # Check if already has Create button
        if ($content -match 'Tạo') {
            Write-Output "$($t.folder) already has Create button - skipping"
            continue
        }
        
        # Find the closing </div> after breadcrumb and add Create button before it
        $pattern = '(</nav>\s*</div>)\s*(</div>)'
        $replacement = "`$1`r`n                <div>`r`n                    <a th:href=`"@{/LvsAdmin/$($t.folder)/LvsCreate}`" class=`"btn btn-primary`">`r`n                        <i class=`"fas fa-plus`"></i> $($t.btn)`r`n                    </a>`r`n                </div>`r`n            `$2"
        
        $newContent = $content -replace $pattern, $replacement
        
        if ($newContent -ne $content) {
            Set-Content $path -Value $newContent -NoNewline
            Write-Output "Added Create button to $($t.folder)"
        } else {
            Write-Output "Failed to add button to $($t.folder) - pattern not found"
        }
    } else {
        Write-Output "$path not found"
    }
}

Write-Output "`nDone!"
