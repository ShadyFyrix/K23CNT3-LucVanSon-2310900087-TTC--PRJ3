import re
import os
from pathlib import Path

def extract_fields(java_content):
    """Extract private fields from Java class"""
    # Pattern to match private fields
    pattern = r'private\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*(?:=\s*[^;]+)?;'
    matches = re.findall(pattern, java_content)
    return [(field_type, field_name) for field_type, field_name in matches]

def generate_getter(field_type, field_name):
    """Generate getter method"""
    method_name = f"get{field_name[0].upper()}{field_name[1:]}"
    return f"""
    public {field_type} {method_name}() {{
        return {field_name};
    }}"""

def generate_setter(field_type, field_name):
    """Generate setter method"""
    method_name = f"set{field_name[0].upper()}{field_name[1:]}"
    return f"""
    public void {method_name}({field_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}"""

def add_getters_setters(java_file_path):
    """Add getters and setters to a Java entity file"""
    with open(java_file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Extract fields
    fields = extract_fields(content)
    
    if not fields:
        print(f"No fields found in {java_file_path}")
        return
    
    # Generate getters and setters
    methods = []
    for field_type, field_name in fields:
        methods.append(generate_getter(field_type, field_name))
        methods.append(generate_setter(field_type, field_name))
    
    # Find the last closing brace
    last_brace = content.rfind('}')
    
    # Insert methods before the last brace
    new_content = content[:last_brace] + '\n    // Getters and Setters' + ''.join(methods) + '\n' + content[last_brace:]
    
    # Write back
    with open(java_file_path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print(f"Added {len(fields)} getters and setters to {os.path.basename(java_file_path)}")

# Process all entity files
entity_dir = r"d:\k23cnt3.lucvanson.project3\k23cnt3.lucvanson.project3\src\main\java\k23cnt3\lucvanson\project3\LvsEntity"

for java_file in Path(entity_dir).glob("Lvs*.java"):
    print(f"\nProcessing {java_file.name}...")
    add_getters_setters(str(java_file))

print("\n✅ Done! All entities now have getters and setters.")
