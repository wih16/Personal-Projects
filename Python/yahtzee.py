#Yahtzee Program

import time
import random

rolled_dice = [0, 0, 0, 0, 0]
to_roll = [1, 1, 1, 1, 1]

one = -1
two = -1
three = -1
four = -1
five = -1
six = -1
bonus = -1
awarded = False
tok = -1
fok = -1
small = -1
large = -1
full = -1
chance = -1
yahtzee = -1

super_total = 0


#functions
def roll():
    for x in range(5):
        if to_roll[x] == 1:
            rolled_dice[x] = (random.randint(1,6))

print("Welcome to Yahtzee!\n")
name = input("Please enter your name: ")
print("Hello "+  name + "!")
print("Let's get started")
for _ in range(13):
    for x in range(3):
        time.sleep(.5)
        print("Rolling...");
        time.sleep(.5)
        print("..")
        time.sleep(.5)
        print(".")
        roll()
        print("\nYou rolled: ", end="")
        for y in range(5):
            print(rolled_dice[y], end=" ")
        print()
            
        if x != 2:
            answer = input("Would you like to reroll (y or n): ")
            if answer == 'n':
                break
            for y in range(5):
                to_roll[y] = 0
                answer = input("Would you like to reroll " + str(rolled_dice[y]) + " (y or n): ")
                if answer == 'y':
                    to_roll[y] = 1


    rolled_dice.sort()


    print("Would you like to place it in: \n1) Upper Section \n2) Lower Section")
    answer = input("> ");

    if answer == '1':
        print("Would you like to place it in:")
        print("1) ones\n2) twos\n3) threes\n4) fours\n5) fives\n6) sixes")
        answer = input("> ");
        total = 0
        if answer == '1':
            for x in range(5):
                if rolled_dice[x] == 1:
                    total += 1
            one = total
        elif answer == '2':
            for x in range(5):
                if rolled_dice[x] == 2:
                    total += 2
            two = total
        elif answer == '3':
            for x in range(5):
                if rolled_dice[x] == 3:
                    total += 3
            three = total
        elif answer == '4':
            for x in range(5):
                if rolled_dice[x] == 4:
                    total += 4
            four = total            
        elif answer == '5':
            for x in range(5):
                if rolled_dice[x] == 5:
                    total += 5
            five = total
        elif answer == '6':
            for x in range(5):
                if rolled_dice[x] == 6:
                    total += 6
            six = total
        bonus += total
        if bonus >= 63 and awarded == False:
            awarded = True
            super_total += 35
        super_total += total
    elif answer == '2':
        print("Would you like to place it in:")
        print("1) 3 of a Kind\n2) 4 of a Kind\n3) Full House\n4) Small Straight\n5) Large Straight\n6) Yahtzee\n7) Chance")
        answer = input("> ")
        total = 0
        if answer == '1':
            valid = False
            #Checking to see if the dice are 3 of a kind
            for x in range(3):
                for y in range(x, x+2):
                    if rolled_dice[y] == rolled_dice[y+1]:
                        valid = True
                    else:
                        valid = False
                        break
                if valid == True:
                    break;
            if valid == True:
                print("Totaling score")
                time.sleep(.25)
                total = 0
                for x in range(5):
                    total += rolled_dice[x]
                    tok = total
            else:
                print("Not a 3 of a Kind, setting score to 0")
                tok = 0
        elif answer == '2':
            valid = False
            #checking to see if the dice are 4 of a kind
            for x in range(2):
                for y in range(x, x+3):
                    if rolled_dice[y] == rolled_dice[y+1]:
                        valid = True
                    else:
                        valid = False
                if valid == True:
                    break;
            if valid == True:
                print("Totaling score")
                time.sleep(.25)
                total = 0
                for x in range(5):
                    total += rolled_dice[x]
                fok = total
            else:
                print("Not a 4 of a Kind, setting score to 0")
                fok = 0
        elif answer == '3':
            valid = False
            #checking to see if the dice arae a full house
            if rolled_dice[1] == rolled_dice[2]:
                if rolled_dice[0] == rolled_dice[1] and rolled_dice[3] == rolled_dice[4]:
                    valid = True
            elif rolled_dice[2] == rolled_dice[3]:
                if rolled_dice[0] == rolled_dice[1] and rolled_dice[3] == rolled_dice[4]:
                    valid = True
                
            if valid == True:
                print("Totaling score")
                time.sleep(.25)
                full = 25
                total = 25
            else:
                print("Not a Full House, setting score to 0")
                full = 0
        elif answer == '4':
            valid = True
            #checking to see if the dice are a small straight
            double = 0 #stores the index of the double number
            for x in range(4):
                if rolled_dice[x] == rolled_dice[x+1]:
                    double = x+1
                    break
            if double == 0:
                for x in range(2):
                    for y in range(x, x+3):                   
                        if (rolled_dice[y]+1) != (rolled_dice[y+1]):
                            valid = False
                            break
                    if valid == True:
                        break
            else:
                for x in range(4):
                    if x+1 == double:
                        continue
                    else:
                        if (rolled_dice[x]+1) != (rolled_dice[x+1]):
                            valid = False                    
                            break
                if valid == True:
                    print("Totaling score")
                    time.sleep(.25)
                    small = 30
                    total = 30
                else:
                    print("Not a Small Straight, setting score to 0")
                    small = 0
        elif answer == '5':
            valid = True
            #checking to see if the dice are a large straight
            for x in range(4):
                if rolled_dice[x]+1 != rolled_dice[x+1]:
                    valid = False
                    break
            
            if valid == True:
                print("Totaling score")
                time.sleep(.25)
                large = 40
                total = 40
            else:
                print("Not a Large Straight, setting score to 0")
                large = 0

        elif answer == '6':    
            #checking to see if the dice are a large straight
            if rolled_dice[0] == rolled_dice[4]:
                print("Totaling score")
                time.sleep(.25)
                yahtzee = 50
                total = 50
            else:
                print("Not Yahtzee, setting score to 0")
                yahtzee = 0

        elif answer == '7':
            print("Totaling score")
            time.sleep(.25)
            for x in range(5):
                total += rolled_dice[x]
            chance = total            
        super_total += total
            
    print("Total Score: ")
    if one != -1:
        print("Ones:", one)
    else:
        print("Ones:")
    if two != -1:
        print("Twos:", two)
    else:
        print("Twos:")
    if three != -1:
        print("Threes:", three)
    else:
        print("Threes:")
    if four != -1:
        print("Fours:", four)
    else:
        print("Fours:")
    if five != -1:
        print("Fives:", five)
    else:
        print("Fives:")
    if six != -1:
        print("Sixes:", six)
    else:
        print("Sixes:")
    if awarded == True:
        print("Bonus: 35")
    else:
        print("Bonus:")
    if tok != -1:
        print("3 of a Kind:", tok)
    else:
        print("3 of a Kind:")
    if fok != -1:
        print("4 of a Kind:", fok)
    else:
        print("4 of a Kind:")
    if full != -1:
        print("Full House:", full)
    else:
        print("Full House:")
    if small != -1:
        print("Small Straight:", small)
    else:
        print("Small Straight:")
    if large != -1:
        print("Large Straight:", large)
    else:
        print("Large Straight:")
    if yahtzee != -1:
        print("Yahtzee:", yahtzee)
    else:
        print("Yahtzee:")
    if chance != -1:
        print("Chance:", chance)
    else:
        print("Chance:")        

    print("Overall Score:", super_total)
    time.sleep(1)
    rolled_dice = [0,0,0,0,0]
    to_roll = [1, 1, 1, 1, 1]

            
            




