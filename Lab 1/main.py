import random



def montyHall():
    doors = [0, 0, 1] # 1 is car, 0's are goats
    random.shuffle(doors)
    goatIndexes = []
    for x in range(len(doors)):
        if doors[x] == 0:
            goatIndexes.append(x)

    doorChoice = random.randint(0,2)
    randomGoat = goatIndexes[random.randint(0,1)]

    if(doorChoice == randomGoat):
        randomGoat = (randomGoat + 1) % 2

    if(doors[doorChoice] == 1):     # 1 if I didn't need to switch
        return 1
    return 0                        # 0 if switch is needed



# St Peter's Paradox solution
def stPetersParadox():
    numHeads = 0
    coinFlip = 0            # O = Heads, 1 = Tails
    while coinFlip == 0:
        numHeads += 1
        coinFlip = random.randint(0,1)
    return pow(2, numHeads)


if __name__ == '__main__':
    list = []
    numTimes = 10
    for x in range(numTimes):
        result = montyHall()
        list.append(result)
        # print(result)

    print("Average for", numTimes, ":", sum(list)/len(list))
    print("Or", sum(list), "/", numTimes)


