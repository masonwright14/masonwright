
import checkPrefs

def printAsMatrix(inFileName, outFileName):
    myRows = checkPrefs.getRowsAsInts(inFileName)
    outfile = open(outFileName, 'w')
    matrixString = ""
    playerCount = 0
    for row in myRows:
        if row[0] > playerCount:
            playerCount = row[0]
    
    # initialize matrix to 0's, with -1's along diagonal
    myMatrix = list()
    for i in range(1, playerCount + 1):
        matrixRow = list()
        for j in range(1, playerCount + 1):
            if i == j:
                matrixRow.append(-1)
            else:
                matrixRow.append(0)
        myMatrix.append(matrixRow)
        
    # set matrix entries based on input
    for row in myRows:
        player = row[0]
        otherPlayer = row[1]
        count = row[2]
        if player != otherPlayer:
            myMatrix[player - 1][otherPlayer - 1] = count
    for matrixRow in myMatrix:
        matrixString += ' '.join(map(str, matrixRow)) + '\n'
    outfile.write(matrixString)
    outfile.close()

if __name__ == '__main__':
    inFileName = "freeman.txt"
    outFileName = "outFile.txt"
    printAsMatrix(inFileName, outFileName)
    pass