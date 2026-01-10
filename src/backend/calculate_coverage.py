import csv

file_path = r'c:\Users\saife\.gemini\antigravity\scratch\lab_work\fullstack-ecommerce\src\backend\target\site\jacoco\jacoco.csv'

total_missed = 0
total_covered = 0

try:
    with open(file_path, 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            total_missed += int(row['INSTRUCTION_MISSED'])
            total_covered += int(row['INSTRUCTION_COVERED'])

    total = total_missed + total_covered
    if total > 0:
        percent = (total_covered / total) * 100
        print(f"Total Instructions: {total}")
        print(f"Covered: {total_covered}")
        print(f"Missed: {total_missed}")
        print(f"Total Coverage: {percent:.2f}%")
    else:
        print("No instructions found.")
except Exception as e:
    print(f"Error: {e}")
