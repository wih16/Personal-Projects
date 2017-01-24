#Yahtzee Program

import time
import random

rolled_dice = [0, 0, 0, 0, 0]
to_roll = [1, 1, 1, 1, 1]

one = 0
two = 0
three = 0
four = 0
five = 0
six = 0
upper = [one, two, three, four, five, six]
bonus = 0
tok = 0
fok = 0
small = 0
large = 0
full = 0
chance = 0
yahtzee = 0
lower = [tok, fok, small, large, full, chance, yahtzee]


#functions
def roll():
    for x in range(5):
        if to_roll[x] == 1:
            rolled_dice[x] = (random.randint(1,6))

print("Welcome to Yahtzee!\n")
name = input("Please enter your name: ")
print("Hello "+  name + "!")
print("Let's get started")
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
    elif answer == '2':
        for x in range(5):
            if rolled_dice[x] == 2:
                total += 2
    elif answer == '3':
        for x in range(5):
            if rolled_dice[x] == 3:
                total += 3
    elif answer == '4':
        for x in range(5):
            if rolled_dice[x] == 4:
                total += 4
    elif answer == '5':
        for x in range(5):
            if rolled_dice[x] == 5:
                total += 5
    elif answer == '6':
        for x in range(5):
            if rolled_dice[x] == 6:
                total += 6
elif answer == '2':
    print("Would you like to place it in:")
    print("1) 3 of a Kind\n2) 4 of a Kind\n3) Full House\n4) Small Straight\n5) Large Straight\n6) Yahtzee\n7) Chance")
    answer = input("> ")

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
        else:
            print("Not a Full House, setting score to 0")
            full = 0
    elif answer == '4':
        rolled_dice = [1,2,3,4,6]
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
                        print(rolled_dice[y])
                        print(rolled_dice[y+1])
                        valid = False
                        break
                if valid == True:
                    break
        else:
            print()
            
            
        print(valid)
                    
                    
                    
            

    
          


        
del rolled_dice[:]
            
                

            
            




