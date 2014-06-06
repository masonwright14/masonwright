
import checkPrefs
import random

def cardinalToJitteredCardinalPrefs(inFileName, outFileName):
    myRows = checkPrefs.getRowsAsInts(inFileName)
    outfile = open(outFileName, 'w')
    matrixString = ""
    for row in myRows:
        outputRow = list()
        for i in range(0, len(row)):
            if row[i] == -1:
                outputRow.append(row[i])
            else:
                outputRow.append(row[i] + random.random() * (1.0 / len(myRows)))
        matrixString += ' '.join(map(str, outputRow)) + '\n'
    outfile.write(matrixString)
    outfile.close()

def ordinalToCardinalPrefs(inFileName, outFileName):
    myRows = checkPrefs.getRowsAsInts(inFileName)
    outfile = open(outFileName, 'w')
    matrixString = ""
    for row in myRows:
        outputRow = list()
        for item in row:
            if item == -1:
                outputRow.append(item)
            else:
                outputRow.append(len(myRows) - item) # convert ranks into non-negative ratings
        for i in range(0, len(outputRow)):
            if outputRow[i] != -1:
                outputRow[i] += random.random() * (1.0 / len(myRows))
        matrixString += ' '.join(map(str, outputRow)) + '\n'
    outfile.write(matrixString)
    outfile.close()

if __name__ == '__main__':
    inFileName = "radoslawEmail_cleaned.txt"
    outFileName = "outFile.txt"
    cardinalToJitteredCardinalPrefs(inFileName, outFileName)
    pass