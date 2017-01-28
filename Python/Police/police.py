import csv

c = open("e03a89dd-134a-4ee8-a2bd-62c40aeebc6f.csv", "rt")
cr = csv.reader(c)

#functions
def print_data(index):
    print("Incident:", offense[index])
    print("Age:", age[index])
    print("Gender:", gender[index])
    print("Race:", race[index])
    temp = time[index].split('T')
    date = temp[0]
    t = temp[1]
    print("Date:", date)
    print("Time:", t)
    print("Arrest Location:", arrest_location[index])
    print("Incident Location:", incident_location[index])
    print("Neighborhood:", neighborhood[index]) 


age = []
gender = []
race = []
time = []
arrest_location = []
offense = []
incident_location = []
neighborhood = []
X  = []
Y = []
i = 0
for row in cr:
    if i == 0:
        i += 1
        continue
    age.append(row[3])
    gender.append(row[4])
    race.append(row[5])
    time.append(row[6])
    arrest_location.append(row[7])
    offense.append(row[8])
    incident_location.append(row[9])
    neighborhood.append(row[10])
    X.append(row[15])
    Y.append(row[16])

print("Incident:", offense[0])
print("Age:", age[0])
print("Gender:", gender[0])
print("Race:", race[0])
temp = time[0].split('T')
date = temp[0]
t = temp[1]
print("Date:", date)
print("Time:", t)
print("Arrest Location:", arrest_location[0])
print("Incident Location:", incident_location[0])
print("Neighborhood:", neighborhood[0])

while True:
    print("How would like to sort these statistics?")
    print("1) Age")
    print("2) Gender")
    print("3) Date")
    print("4) Time")
    print("5) Neighborhood")
    answer_str = input(">> ")
    try: 
        answer = int(answer_str)
    except ValueError:
        print("Please enter a valid number")
        continue
    if not answer > 0 or not answer < 6:
        print("Please enter a valid number")
        continue
    break

#Sorting by Age
if answer == 1:
    while True:
        print("Find everyone...")
        print("1) Under")
        print("2) Over")
        answer_str = input(">> ")
        try: 
            answer = int(answer_str)
        except ValueError:
            print("Please enter a valid number")
            continue
        if not answer > 0 or not answer < 3:
            print("Please enter a valid number")
            continue
        break
    while True:
        print("Age...")
        answer_str = input(">> ")
        try: 
            answer2 = int(answer_str)
        except ValueError:
            print("Please enter a valid number")
            continue
        if not answer2 > 0:
            print("Please enter a valid number")
            continue
        break

    print(answer2)

    #Under a certain age
    if answer == 1:
        i = 0
        for a in age:
            try:
                a = int(a)
            except ValueError:
                i += 1
                continue
            if a < answer2:
                print_data(i)
                print("\n")
            i += 1
    #Over a certain age
    elif answer == 2:
        i = 0
        for a in age:
            try:
                a = int(a)
            except ValueError:
                i += 1
                continue

            if a > answer2:
                print_data(i)
                print("\n")
            i += 1
                

    

      
    


