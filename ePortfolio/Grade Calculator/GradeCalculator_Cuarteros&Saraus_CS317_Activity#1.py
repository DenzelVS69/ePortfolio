print("GRADE CALCULATOR")

# Function for calculating student grade
def calculate_grade(scores):
    try:
        average = sum(scores) / len(scores)
        if average >= 75:
            return "Passed"
        elif average >= 60:
            return "Probation"
        else:
            return "Failed"
    except ZeroDivisionError:
        return "No scores entered"
    except TypeError:
        return "Invalid input. Please enter numbers only."

# Empty list for scores
scores = []

# Loop for getting scores from user
while True:
    score = input("Enter a score (or 'quit' to finish): ")
    if score == 'quit':
        break
    try:
        scores.append(float(score))
    except ValueError:
        print("Invalid input. Please enter a number.")

# Conditional statement for printing grade
if scores:
    print("Average score:", sum(scores) / len(scores))
    print("Status:", calculate_grade(scores))
else:
    print("No scores entered.")
