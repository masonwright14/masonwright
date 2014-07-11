myArgs <- commandArgs()
myArg <- myArgs[6]
steps <- as.integer(myArgs[7])

fileSinkName <- paste("/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/", myArg, "/gallupStatisticsOut", steps, ".txt", myFileString <- "draft_newfrat_agents"

# myFileString <- "tabuAllOpt_newfrat_agents"

descFileName <- paste("/Users/masonwright/Dropbox/JavaControlling/coalitiongamesjava/outputFiles/", myFileString, "_descr.csv", sep = "")

summaryFileName <- paste("/Users/masonwright/Dropbox/JavaControlling/coalitiongamesjava/outputFiles/", myFileString, "_summary.csv", sep = "")

resultFileName <- paste("/Users/masonwright/Dropbox/JavaControlling/coalitiongamesjava/outputFiles/", myFileString, "_results.csv", sep = "")

descData <- read.csv(file= descFileName,head=TRUE,sep=",")

colnames(descData)

myN <- descData[,c('n')]
myKMin <- descData[,c('kMin')]
myKMax <- descData[,c('kMax')]

summaryData <- read.csv(file= summaryFileName,head=TRUE,sep=",")

colnames(summaryData)

runNumber <- summaryData[,c('runNumber')]
countRuns <- length(runNumber)
runTimeInMillis <- summaryData[,c('runTimeInMillis')]
numberOfTeams <- summaryData[,c('numberOfTeams')]

resultData <- read.csv(file= resultFileName,head=TRUE,sep=","))
sink(fileSinkName)

warmUp <- 300

options(digits=3)

writeLines("")
writeLines("warm up")
writeLines(toString(warmUp))

writeLines("")
writeLines("steps")
writeLines(toString(steps))

lineDivider <- "\n+++++++++++++++++++++++++++++++++++++++++++++++++\n"
writeLines(lineDivider)
writeLines("default-test")
runDataFolderName <- paste("/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/", myArg, "/Run Data", sep = "")
defaultDataName <- paste(runDataFolderName, "/default-test_cleaned.csv", sep = "")
defaultData <- read.csv(file= defaultDataName,head=TRUE,sep=",")
defaultData.sub <- subset(defaultData, X.step. > warmUp & X.step. <= warmUp + steps)


myRedVoteMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myRedVoteMeans <- append(myRedVoteMeans, mean(mySubset[,c('precision..previous.cycle.votes..of.red.party.3')]))
}
myRedVoteMeans <- as.numeric(myRedVoteMeans)
a <- mean(myRedVoteMeans)
s <- sd(myRedVoteMeans)
n <- length(myRedVoteMeans)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred votes")
writeLines("\nmean of means")
cat(a)
writeLines("\nsd of means")
cat(s)
writeLines("\nlower 95% confidence bound for mean")
cat(a - error)
writeLines("\nupper 95% confidence bound for mean")
cat(a + error)
writeLines(lineDivider)


myRedVoteMaxes <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myRedVoteMaxes <- append(myRedVoteMaxes, max(mySubset[,c('precision..previous.cycle.votes..of.red.party.3')]))
}
myRedVoteMaxes <- as.numeric(myRedVoteMaxes)
a <- mean(myRedVoteMaxes)
s <- sd(myRedVoteMaxes)
n <- length(myRedVoteMaxes)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred votes")
writeLines("\nmean of maxes")
cat(a)
writeLines("\nsd of maxes")
cat(s)
writeLines("\nlower 95% confidence bound for max")
cat(a - error)
writeLines("\nupper 95% confidence bound for max")
cat(a + error)
writeLines(lineDivider)


myRedVoteMins <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myRedVoteMins <- append(myRedVoteMins, min(mySubset[,c('precision..previous.cycle.votes..of.red.party.3')]))
}
myRedVoteMins <- as.numeric(myRedVoteMins)
a <- mean(myRedVoteMins)
s <- sd(myRedVoteMins)
n <- length(myRedVoteMins)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred votes")
writeLines("\nmean of mins")
cat(a)
writeLines("\nsd of mins")
cat(s)
writeLines("\nlower 95% confidence bound for min")
cat(a - error)
writeLines("\nupper 95% confidence bound for min")
cat(a + error)
writeLines(lineDivider)


myRedMeanIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myRedMeanIdeos <- append(myRedMeanIdeos, mean(mySubset[,c('precision..ideo..of.red.party.3')]))
}
myRedMeanIdeos <- as.numeric(myRedMeanIdeos)
a <- mean(myRedMeanIdeos)
s <- sd(myRedMeanIdeos)
n <- length(myRedMeanIdeos)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred ideo positions")
writeLines("\nmean of means")
cat(a)
writeLines("\nsd of means")
cat(s)
writeLines("\nlower 95% confidence bound for mean")
cat(a - error)
writeLines("\nupper 95% confidence bound for mean")
cat(a + error)
writeLines(lineDivider)


myRedMaxIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myRedMaxIdeos <- append(myRedMaxIdeos, max(mySubset[,c('precision..ideo..of.red.party.3')]))
}
myRedMaxIdeos <- as.numeric(myRedMaxIdeos)
a <- mean(myRedMaxIdeos)
s <- sd(myRedMaxIdeos)
n <- length(myRedMaxIdeos)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred ideo positions")
writeLines("\nmean of maxes")
cat(a)
writeLines("\nsd of maxes")
cat(s)
writeLines("\nlower 95% confidence bound for max")
cat(a - error)
writeLines("\nupper 95% confidence bound for max")
cat(a + error)
writeLines(lineDivider)


myRedMinIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myRedMinIdeos <- append(myRedMinIdeos, min(mySubset[,c('precision..ideo..of.red.party.3')]))
}
myRedMinIdeos <- as.numeric(myRedMinIdeos)
a <- mean(myRedMinIdeos)
s <- sd(myRedMinIdeos)
n <- length(myRedMinIdeos)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred ideo positions")
writeLines("\nmean of mins")
cat(a)
writeLines("\nsd of mins")
cat(s)
writeLines("\nlower 95% confidence bound for min")
cat(a - error)
writeLines("\nupper 95% confidence bound for min")
cat(a + error)
writeLines(lineDivider)


myRedMinusBlueMeanIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  tempRedMinusBlue <- mapply('-', mySubset["precision..ideo..of.red.party.3"][,], mySubset["precision..ideo..of.blue.party.3"][,],SIMPLIFY=TRUE)
  myRedMinusBlueMeanIdeos <- append(myRedMinusBlueMeanIdeos, mean(tempRedMinusBlue))
}
myRedMinusBlueMeanIdeos <- as.numeric(myRedMinusBlueMeanIdeos)
a <- mean(myRedMinusBlueMeanIdeos)
s <- sd(myRedMinusBlueMeanIdeos)
n <- length(myRedMinusBlueMeanIdeos)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred minus blue ideo position")
writeLines("\nmean of means")
cat(a)
writeLines("\nsd of means")
cat(s)
writeLines("\nlower 95% confidence bound for mean")
cat(a - error)
writeLines("\nupper 95% confidence bound for mean")
cat(a + error)
writeLines(lineDivider)


myRedMinusBlueMaxIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  tempRedMinusBlue <- mapply('-', mySubset["precision..ideo..of.red.party.3"][,], mySubset["precision..ideo..of.blue.party.3"][,],SIMPLIFY=TRUE)
  myRedMinusBlueMaxIdeos <- append(myRedMinusBlueMaxIdeos, max(tempRedMinusBlue))
}
myRedMinusBlueMaxIdeos <- as.numeric(myRedMinusBlueMaxIdeos)
a <- mean(myRedMinusBlueMaxIdeos)
s <- sd(myRedMinusBlueMaxIdeos)
n <- length(myRedMinusBlueMaxIdeos)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred minus blue ideo position")
writeLines("\nmean of maxes")
cat(a)
writeLines("\nsd of maxes")
cat(s)
writeLines("\nlower 95% confidence bound for max")
cat(a - error)
writeLines("\nupper 95% confidence bound for max")
cat(a + error)
writeLines(lineDivider)


myRedMinusBlueMinIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  tempRedMinusBlue <- mapply('-', mySubset["precision..ideo..of.red.party.3"][,], mySubset["precision..ideo..of.blue.party.3"][,],SIMPLIFY=TRUE)
  myRedMinusBlueMinIdeos <- append(myRedMinusBlueMinIdeos, min(tempRedMinusBlue))
}
myRedMinusBlueMinIdeos <- as.numeric(myRedMinusBlueMinIdeos)
a <- mean(myRedMinusBlueMinIdeos)
s <- sd(myRedMinusBlueMinIdeos)
n <- length(myRedMinusBlueMinIdeos)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred minus blue ideo position")
writeLines("\nmean of mins")
cat(a)
writeLines("\nsd of mins")
cat(s)
writeLines("\nlower 95% confidence bound for min")
cat(a - error)
writeLines("\nupper 95% confidence bound for min")
cat(a + error)
writeLines(lineDivider)


myRedPartyAbsVoteDiffMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffMeans <- append(myRedPartyAbsVoteDiffMeans, mean(abs(tempRedPartyVoteDiffs)))
}
myRedPartyAbsVoteDiffMeans <- as.numeric(myRedPartyAbsVoteDiffMeans)
a <- mean(myRedPartyAbsVoteDiffMeans)
s <- sd(myRedPartyAbsVoteDiffMeans)
n <- length(myRedPartyAbsVoteDiffMeans)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred party absval change in votes in next cycle")
writeLines("\nmean of means")
cat(a)
writeLines("\nsd of means")
cat(s)
writeLines("\nlower 95% confidence bound for mean")
cat(a - error)
writeLines("\nupper 95% confidence bound for mean")
cat(a + error)
writeLines(lineDivider)


myRedPartyAbsVoteDiffMaxes <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffMaxes <- append(myRedPartyAbsVoteDiffMaxes, max(abs(tempRedPartyVoteDiffs)))
}
myRedPartyAbsVoteDiffMaxes <- as.numeric(myRedPartyAbsVoteDiffMaxes)
a <- mean(myRedPartyAbsVoteDiffMaxes)
s <- sd(myRedPartyAbsVoteDiffMaxes)
n <- length(myRedPartyAbsVoteDiffMaxes)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred party absval change in votes in next cycle")
writeLines("\nmean of maxes")
cat(a)
writeLines("\nsd of maxes")
cat(s)
writeLines("\nlower 95% confidence bound for max")
cat(a - error)
writeLines("\nupper 95% confidence bound for max")
cat(a + error)
writeLines(lineDivider)


myRedPartyAbsVoteDiffSds <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffSds <- append(myRedPartyAbsVoteDiffSds, sd(tempRedPartyVoteDiffs))
}
myRedPartyAbsVoteDiffSds <- as.numeric(myRedPartyAbsVoteDiffSds)
a <- mean(myRedPartyAbsVoteDiffSds)
s <- sd(myRedPartyAbsVoteDiffSds)
n <- length(myRedPartyAbsVoteDiffSds)
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nred party within-run sd of change in votes in next cycle")
writeLines("\nmean of sds")
cat(a)
writeLines("\nsd of sds")
cat(s)
writeLines("\nlower 95% confidence bound for sd")
cat(a - error)
writeLines("\nupper 95% confidence bound for sd")
cat(a + error)

headerString <- "\n\nrun name,mean red votes,max red votes,min red votes,mean red ideo,max red ideo,min red ideo,mean red minus blue ideo,max red minus blue ideo,min red minus blue ideo,mean abs red vote change,max abs red vote change,sd within-run red vote change"
cat(headerString)

pr <- function(x) format(round(x, 3), nsmall=3)
writeLines("")
myTitle <- paste(myArg, steps, sep = "_")
resultString <- paste(myTitle, pr(mean(myRedVoteMeans)), pr(mean(myRedVoteMaxes)), pr(mean(myRedVoteMins)), pr(mean(myRedMeanIdeos)), pr(mean(myRedMaxIdeos)), pr(mean(myRedMinIdeos)), pr(mean(myRedMinusBlueMeanIdeos)), pr(mean(myRedMinusBlueMaxIdeos)), pr(mean(myRedMinusBlueMinIdeos)), pr(mean(myRedPartyAbsVoteDiffMeans)), pr(mean(myRedPartyAbsVoteDiffMaxes)), pr(mean(myRedPartyAbsVoteDiffSds)), sep = ",")
cat(resultString)

writeLines("")
writeLines("")
