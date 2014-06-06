
# import sys
import collections

def getLines(fileName):
    myFile = open(fileName, 'r')
    result = []
    for line in myFile:
        result.append(line.rstrip('\n'))
    myFile.close()
    return result

def getRows(fileName):
    lines = getLines(fileName)
    result = []
    for line in lines:
        line = line.lstrip().rstrip()
        result.append(line.split())
    return result

def getRowsAsInts(fileName):
    rows = getRows(fileName)
    result = list()
    for row in rows:
        resultRow = list()
        for item in row:
            if not isint(item):
                return False
            resultRow.append(int(item))
        result.append(resultRow)
    return result

def getRowsAsFloats(fileName):
    rows = getRows(fileName)
    result = list()
    for row in rows:
        resultRow = list()
        for item in row:
            if not isfloat(item):
                return False
            resultRow.append(float(item))
        result.append(resultRow)
    return result

def checkMinusOneDiagonal(myRows):
    for row in range(0, len(myRows)):
        if myRows[row][row] != -1:
            return False
    return True

def checkNonDiagonalNonNegative(myRows):
    for row in range(0, len(myRows)):
        for col in range(0, len(myRows)):
            if row != col and myRows[row][col] < 0:
                return False
    return True

def checkNonDiagonalNotInteger(myRows):
    for row in range(0, len(myRows)):
        for col in range(0, len(myRows)):
            if row != col and myRows[row][col] % 1 == 0:
                return False
    return True

def checkSquareMatrix(myRows):
    for row in myRows:
        if len(row) != len(myRows):
            return False
    return True

def checkOrdinalRanksNotSkipped(myRows):
    for row in myRows:
        rowMax = max(row)
        if rowMax > 0:
            sortedRow = sorted(row)
            if 1 not in row:
                print "Missing: 1"
                print "Row: " + str(row)
                print "Sorted: " + str(sortedRow)
                return False
            indexOf1 = sortedRow.index(1) # guaranteed to be present
            previous = 1
            for index in range(indexOf1 + 1, len(row)):
                current = sortedRow[index]
                if current - 1 != previous:
                    print "Missing: " + str(current - 1)
                    print "Row: " + str(row) 
                    print "Sorted: " + str(sortedRow)
                    print "Index: " + str(row.index(-1) + 1)
                    mostFreq = collections.Counter(row).most_common(1)
                    if mostFreq[0][1] > 1:
                        print "Duplicate: " + str(mostFreq[0][0])
                    return False
                previous = current
    return True

def checkCardinalJittered(fileName):
    myRows = getRowsAsFloats(fileName)
    if not myRows:
        print "Float value found or empty"
        return False
    if not checkSquareMatrix(myRows):
        print "Not a square matrix"
        return False
    if not checkMinusOneDiagonal(myRows):
        print "Diagonal not -1"
        return False
    if not checkNonDiagonalNonNegative(myRows):
        print "Off Diagonal < 0"
        return False
    if not checkNonDiagonalNotInteger(myRows):
        print "Off Diagonal An Integer"
        return False
    return True

def checkCardinal(fileName):
    myRows = getRowsAsInts(fileName)
    if not myRows:
        print "Float value found or empty"
        return False
    if not checkSquareMatrix(myRows):
        print "Not a square matrix"
        return False
    if not checkMinusOneDiagonal(myRows):
        print "Diagonal not -1"
        return False
    if not checkNonDiagonalNonNegative(myRows):
        print "Off Diagonal < 0"
        return False
    return True
  
def checkOrdinal(fileName):
    myRows = getRowsAsInts(fileName)
    if not myRows:
        print "Float value found or empty"
        return False
    if not checkSquareMatrix(myRows):
        print "Not a square matrix"
        return False
    if not checkMinusOneDiagonal(myRows):
        print "Diagonal not -1"
        return False
    if not checkNonDiagonalNonNegative(myRows):
        print "Off Diagonal < 0"
        return False
    if not checkOrdinalRanksNotSkipped(myRows):
        print "Rank skipped"
        return False
    return True

def isfloat(x):
    try:
        float(x)
    except ValueError:
        return False
    else:
        return True

def isint(x):
    try:
        a = float(x)
        b = int(a)
    except ValueError:
        return False
    else:
        return a == b

if __name__ == '__main__':
    #  fileName = sys.argv[1] # use file name string    
    #  isOrdinal = bool(int(sys.argv[2])) # use 1 for true, 0 for false
    #  isForMIP = bool(int(sys.argv[3])) # use 1 for true, 0 for false
   
    fileName = "webster_res_cardinal.txt"
    isOrdinal = False
    isForMIP = True
    
    if isOrdinal:
        print checkOrdinal(fileName)
    else:
        if isForMIP:
            print checkCardinalJittered(fileName)
        else:
            print checkCardinal(fileName)
    pass