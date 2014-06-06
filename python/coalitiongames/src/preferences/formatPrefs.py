
import checkPrefs
    
def setMainDiagMinusOne(inFileName, outFileName):
    myRows = checkPrefs.getRowsAsInts(inFileName)
    outfile = open(outFileName, 'w')
    matrixString = ""
    for row in range(0, len(myRows)):
        myRows[row][row] = -1
        for col in range(0, len(myRows)):
            matrixString += str(myRows[row][col]) + " "
        if (row < len(myRows) - 1):
            matrixString += '\n'
    outfile.write(matrixString)
    outfile.close()
    
def reduceInRange(myMin, myMax):
    #myArray = [1, 3, 4, 5, 5]
    #myStr = "10 -1 35 17 29 6 13 30 2 7 35 21 15 20 19 24 18 4 12 8 1 22 11 31 28 32 39 26 23 5 14 16 33 38 27 36 25 3 34 37"
    #myStr = "29 21 34 -1 1 23 11 28 30 22 34 2 12 20 26 39 25 14 10 32 33 8 24 4 15 16 37 17 27 7 6 5 36 19 9 35 38 3 31 18"
    #myStr = "29 8 31 1 -1 4 21 20 27 24 31 3 28 18 16 12 2 17 11 34 14 35 30 13 26 7 9 37 36 32 33 15 23 39 19 22 6 5 25 38"
    #myStr = "27 24 12 19 25 -1 16 28 23 4 12 3 6 11 9 2 20 5 18 39 22 35 1 13 26 29 14 30 38 21 17 7 33 36 15 32 10 31 34 37"
    myStr = "29 38 17 4 31 37 6 35 36 22 17 24 39 20 19 26 12 30 32 28 25 1 18 14 33 34 27 8 9 21 11 10 5 3 2 15 23 16 13 -1 "
    strArray = myStr.split()
    myArray = list()
    for i in strArray:
        myArray.append(int(i))
    for i in range(0, len(myArray)):
        if myArray[i] >= myMin and myArray[i] < myMax:
            myArray[i] -= 1
    i = len(myArray) - 1
    while i >= 0:
        if myArray[i] == myMax:
            myArray[i] -= 1
            break
        i -= 1
    print(' '.join(map(str, myArray)))
    
def addOneInRange(myMin, myMax):
    myStr = "31 36 5 34 28 23 38 30 18 35 5 27 33 25 13 14 6 24 32 22 39 12 29 26 1 16 4 8 7 20 21 19 3 17 9 10 11 2 -1 37 "
    strArray = myStr.split()
    myArray = list()
    for i in strArray:
        myArray.append(int(i))
    for i in range(0, len(myArray)):
        if (myArray[i] <= myMax and myArray[i] > myMin):
            myArray[i] += 1
    while i >= 0:
        if myArray[i] == myMin:
            myArray[i] += 1
            break
        i -= 1
    print(' '.join(map(str, myArray)))
    
if __name__ == '__main__':
    #inFileName = "bkoffice_cleaned.txt"
    inFileName = "freeman.txt"
    outFileName = "outFile.txt"
    setMainDiagMinusOne(inFileName, outFileName)
    #reduceInRange(3, 5)
    #reduceInRange(10, 35)
    #reduceInRange(14, 34)
    #reduceInRange(8, 17)
    
    
    #addOneInRange(5, 14)
    #addOneInRange(2, 10)
    pass
