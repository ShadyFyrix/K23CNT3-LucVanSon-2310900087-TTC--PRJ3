# PowerShell script to add getters/setters
$entityDir = "src\main\java\k23cnt3\lucvanson\project3\LvsEntity"
$files = Get-ChildItem -Path $entityDir -Filter "Lvs*.java"

foreach ($file in $files) {
    Write-Host "Processing $($file.Name)..."
    $java = Get-Content $file.FullName -Raw
    
    # Extract fields using regex
    $pattern = 'private\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*(?:=\s*[^;]+)?;'
    $matches = [regex]::Matches($java, $pattern)
    
    if ($matches.Count -eq 0) { continue }
    
    $methods = "`n    // Getters and Setters`n"
    
    foreach ($match in $matches) {
        $type = $match.Groups[1].Value
        $name = $match.Groups[2].Value
        $capName = $name.Substring(0,1).ToUpper() + $name.Substring(1)
        
        # Getter
        $methods += "`n    public $type get$capName() {`n        return $name;`n    }`n"
        # Setter  
        $methods += "`n    public void set$capName($type $name) {`n        this.$name = $name;`n    }`n"
    }
    
    # Insert before last }
    $lastBrace = $java.LastIndexOf('}')
    $newJava = $java.Substring(0, $lastBrace) + $methods + "`n" + $java.Substring($lastBrace)
    
    Set-Content $file.FullName $newJava -NoNewline
    Write-Host "  Added $($matches.Count) getters/setters"
}
Write-Host "`nDone!"
