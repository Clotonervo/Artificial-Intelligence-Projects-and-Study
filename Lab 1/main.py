import random

def roleDice():
    return random.randint(1,6)

def risk(attackerArmies, defenderArmies, attackerDice=3, defenderDice=2):
    # print("# of Attackers:", attackerArmies)
    # print("# of Defenders:", defenderArmies)

    while attackerArmies > 1 and defenderArmies > 0:
        attackerRole = []
        defenderRole = []
        for x in range(attackerDice):
            attackerRole.append(roleDice())
        attackerRole.sort()
        attackerRole.reverse()

        for x in range(defenderDice):
            defenderRole.append(roleDice())
        defenderRole.sort()
        defenderRole.reverse()

        for x in range(min(len(defenderRole), len(attackerRole))):
            if defenderRole[x] < attackerRole[x]:
                defenderArmies -= 1
            else:
                attackerArmies -= 1

        attackerDice = min(3, attackerArmies - 1)
        defenderDice = min(2, defenderArmies)

        # print(attackerRole)
        # print(defenderRole)
        # print(attackerArmies)
        # print(defenderArmies)

    if(defenderArmies > 0):
        # print("Defenders win!")
        return 0
    else:
        # print("Attackers win!")
        return 1


def numArmiesLost(attackerDice=3, defenderDice=2):
        attackerRole = []
        defenderRole = []
        attackerArmiesLost = 0
        defenderArmiesLost = 0
        for x in range(attackerDice):
            attackerRole.append(roleDice())
        attackerRole.sort()
        attackerRole.reverse()

        for x in range(defenderDice):
            defenderRole.append(roleDice())
        defenderRole.sort()
        defenderRole.reverse()

        for x in range(min(len(defenderRole), len(attackerRole))):
            if defenderRole[x] < attackerRole[x]:
                defenderArmiesLost += 1
            else:
                attackerArmiesLost += 1
        return (attackerArmiesLost, defenderArmiesLost)





# Monty Hall Solution
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
    # for x in range(5, 21):
    # print("A =", x)
    list = []
    d = {}
    numTimes = 1000000
    for y in range(numTimes):
        result = risk(6, 5)
        list.append(result)

    percent = "{:.2f}".format((sum(list)/numTimes) * 100)
    # print("Results for", numTimes, "runs: Attacker wins ", sum(list))
    print(sum(list), "wins,", percent, "%")
    print()



    # for x in range(numTimes):
    #     key = numArmiesLost(1,1)
    #     if key not in d:
    #         d[key] = 1
    #     else:
    #         d[key] += 1
    #
    # for key in d:
    #     print(key, "=", d[key], f"/{numTimes}  : ", (d[key]/numTimes) * 100, "%")


