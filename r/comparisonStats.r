
fileSinkName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/comparisonStatsOut.txt"
sink(fileSinkName)

warmUp <- 300
steps <- 1000

options(digits=3)

writeLines("")
writeLines("warm up")
writeLines(toString(warmUp))

writeLines("")
writeLines("steps")
writeLines(toString(steps))

lineDivider <- "\n+++++++++++++++++++++++++++++++++++++++++++++++++\n"
writeLines(lineDivider)

defaultDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/Default/Run Data/default-test_cleaned.csv"
defaultData <- read.csv(file= defaultDataName,head=TRUE,sep=",")
defaultData.sub <- subset(defaultData, X.step. > warmUp & X.step. <= warmUp + steps)

data_02_name <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_02/Run Data/default-test_cleaned.csv"
data_02 <- read.csv(file= data_02_name,head=TRUE,sep=",")
data_02.sub <- subset(data_02, X.step. > warmUp & X.step. <= warmUp + steps)

data_10_name <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_1/Run Data/default-test_cleaned.csv"
data_10 <- read.csv(file= data_10_name,head=TRUE,sep=",")
data_10.sub <- subset(data_10, X.step. > warmUp & X.step. <= warmUp + steps)

###############################

getOligMeans <- function(dataset) {
myOligMeans <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  myOligMeans <- append(myOligMeans, mean(mySubset[,c('precision.mean..olig..of.parties.3')]))
}
myOligMeans <- as.numeric(myOligMeans)
return(myOligMeans)
}

cohens_d <- function(x, y) {
    lx <- length(x)- 1
    ly <- length(y)- 1
    md  <- abs(mean(x) - mean(y))        ## mean difference (numerator)
    csd <- lx * var(x) + ly * var(y)
    csd <- csd/(lx + ly)
    csd <- sqrt(csd)                     ## common sd computation

    cd  <- md/csd                        ## cohen's d
}

library(data.table)

compareIncomeResults <- function(myFunction) {
defaultResult <- myFunction(defaultData.sub)
data_02_result <- myFunction(data_02.sub)
data_10_result <- myFunction(data_10.sub)

defaultTo02 <- t.test(defaultResult, data_02_result, paired=TRUE)
defaultFrom10 <- t.test(data_10_result, defaultResult, paired=TRUE)
defaultTo02d <- cohens_d(defaultResult, data_02_result)
defaultTo10d <- cohens_d(defaultResult, data_10_result)

result <- data.table(defaultTo02, defaultFrom10, defaultTo02d, defaultTo10d)
return(result)
}

result <- compareIncomeResults(getOligMeans)

writePValue <- function(myDataTable, myColName) {
writeLines("p-value")
cat(unlist(myDataTable[3, eval(myColName)]))
writeLines("\nconf int")
cat(unlist(myDataTable[4, eval(myColName)]))
writeLines("\nestimate")
cat(unlist(myDataTable[5, eval(myColName)]))
writeLines("")
}

writeCohensD <- function(myDataTable, myColName) {
writeLines("cohen's d")
cat(unlist(myDataTable[1, eval(myColName)]))
writeLines("")
}

writeLines("\nolig means: default vs. 02")
writePValue(result, quote(defaultTo02))
writeCohensD(result, quote(defaultTo02d))
writeLines("\nolig means: 10 vs. default")
writePValue(result, quote(defaultFrom10))
writeCohensD(result, quote(defaultTo10d))

###############################

getSalienceMeans <- function(dataset) {
mySalienceMeans <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  mySalienceMeans <- append(mySalienceMeans, mean(mySubset[,c('precision.mean..olig.salience..of.voters.3')]))
}
mySalienceMeans <- as.numeric(mySalienceMeans)
return(mySalienceMeans)
}

result <- compareIncomeResults(getSalienceMeans)
writeLines("\nsalience means: default vs. 02")
writePValue(result, quote(defaultTo02))
writeCohensD(result, quote(defaultTo02d))

writeLines("\nsalience means: 10 vs. default")
writePValue(result, quote(defaultFrom10))
writeCohensD(result, quote(defaultTo10d))


###############################

getTaxRateMeans <- function(dataset) {
myTaxRateMeans <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  myTaxRateMeans <- append(myTaxRateMeans, mean(mySubset[,c('precision.voter.tax.rate.3')]))
}
myTaxRateMeans <- as.numeric(myTaxRateMeans)
return(myTaxRateMeans)
}

result <- compareIncomeResults(getTaxRateMeans)
writeLines("\ntax rate means: default vs. 02")
writePValue(result, quote(defaultTo02))
writeCohensD(result, quote(defaultTo02d))

writeLines("\ntax rate means: 10 vs. default")
writePValue(result, quote(defaultFrom10))
writeCohensD(result, quote(defaultTo10d))


###############################

getProfitMeans <- function(dataset) {
myProfitMeans <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  myProfitMeans <- append(myProfitMeans, mean(mySubset[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')]))
}
myProfitMeans <- as.numeric(myProfitMeans)
return(myProfitMeans)
}

result <- compareIncomeResults(getProfitMeans)
writeLines("\nprofit means: default vs. 02")
writePValue(result, quote(defaultTo02))
writeCohensD(result, quote(defaultTo02d))

writeLines("\nprofit means: 10 vs. default")
writePValue(result, quote(defaultFrom10))
writeCohensD(result, quote(defaultTo10d))

###############################

getRedPartyAbsVoteDiffMaxes <- function(dataset) {
myRedPartyAbsVoteDiffMaxes <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffMaxes <- append(myRedPartyAbsVoteDiffMaxes, max(abs(tempRedPartyVoteDiffs)))
}
myRedPartyAbsVoteDiffMaxes <- as.numeric(myRedPartyAbsVoteDiffMaxes)
return(myRedPartyAbsVoteDiffMaxes)
}

result <- compareIncomeResults(getRedPartyAbsVoteDiffMaxes)
writeLines("\nred party abs vote diff maxes: default vs. 02")
writePValue(result, quote(defaultTo02))
writeCohensD(result, quote(defaultTo02d))

writeLines("\nred party abs vote diff maxes: 10 vs. default")
writePValue(result, quote(defaultFrom10))
writeCohensD(result, quote(defaultTo10d))


###############################
###############################

writeLines(lineDivider)

modifiedDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/Modified/Run Data/default-test_cleaned.csv"
modifiedData <- read.csv(file= modifiedDataName,head=TRUE,sep=",")
modifiedData.sub <- subset(modifiedData, X.step. > warmUp & X.step. <= warmUp + steps)

ideoPrefDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/IdeoPreference/Run Data/default-test_cleaned.csv"
ideoPrefData <- read.csv(file= ideoPrefDataName,head=TRUE,sep=",")
ideoPrefData.sub <- subset(ideoPrefData, X.step. > warmUp & X.step. <= warmUp + steps)

getMeanRedMinusBlueMeanIdeos <- function(dataset) {
myRedMinusBlueMeanIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  tempRedMinusBlue <- mapply('-', mySubset["precision..ideo..of.red.party.3"][,], mySubset["precision..ideo..of.blue.party.3"][,],SIMPLIFY=TRUE)
  myRedMinusBlueMeanIdeos <- append(myRedMinusBlueMeanIdeos, mean(tempRedMinusBlue))
}
myRedMinusBlueMeanIdeos <- as.numeric(myRedMinusBlueMeanIdeos)
return(myRedMinusBlueMeanIdeos)
}

compareModifiedResults <- function(myFunction) {
defaultResult <- myFunction(defaultData.sub)
modifiedResult <- myFunction(modifiedData.sub)
ideoPrefresult <- myFunction(ideoPrefData.sub)

defaultToModified <- t.test(defaultResult, modifiedResult, paired=TRUE)
defaultToIdeoPref <- t.test(defaultResult, ideoPrefresult, paired=TRUE)
defaultToModifiedCohd <- cohens_d(defaultResult, modifiedResult)
defaultToIdeoPrefCohd <- cohens_d(defaultResult, ideoPrefresult)
result <- data.table(defaultToModified, defaultToIdeoPref, defaultToModifiedCohd, defaultToIdeoPrefCohd)
return(result)
}

result <- compareModifiedResults(getMeanRedMinusBlueMeanIdeos)
writeLines("\nmean red minus blue mean ideos: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nmean red minus blue mean ideos: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))


###############################

getMaxRedVotes <- function(dataset) {
myRedVoteMaxes <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  myRedVoteMaxes <- append(myRedVoteMaxes, max(mySubset[,c('precision..previous.cycle.votes..of.red.party.3')]))
}
myRedVoteMaxes <- as.numeric(myRedVoteMaxes)
return(myRedVoteMaxes)
}

result <- compareModifiedResults(getMaxRedVotes)
writeLines("\nmax red votes: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nmax red votes: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))


###############################

getMaxRedIdeo <- function(dataset) {
myRedMaxIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  myRedMaxIdeos <- append(myRedMaxIdeos, max(mySubset[,c('precision..ideo..of.red.party.3')]))
}
myRedMaxIdeos <- as.numeric(myRedMaxIdeos)
return(myRedMaxIdeos)
}

result <- compareModifiedResults(getMaxRedIdeo)
writeLines("\nmax red ideo: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nmax red ideo: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))


###############################

getRedMinusBlueMaxIdeo <- function(dataset) {
myRedMinusBlueMaxIdeos <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  tempRedMinusBlue <- mapply('-', mySubset["precision..ideo..of.red.party.3"][,], mySubset["precision..ideo..of.blue.party.3"][,],SIMPLIFY=TRUE)
  myRedMinusBlueMaxIdeos <- append(myRedMinusBlueMaxIdeos, max(tempRedMinusBlue))
}
myRedMinusBlueMaxIdeos <- as.numeric(myRedMinusBlueMaxIdeos)
return(myRedMinusBlueMaxIdeos)
}

result <- compareModifiedResults(getRedMinusBlueMaxIdeo)
writeLines("\nmax red minus blue ideo: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nmax red minus blue ideo: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))


###############################

getRedVoteDiffMeans <- function(dataset) {
myRedPartyAbsVoteDiffMeans <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffMeans <- append(myRedPartyAbsVoteDiffMeans, mean(abs(tempRedPartyVoteDiffs)))
}
myRedPartyAbsVoteDiffMeans <- as.numeric(myRedPartyAbsVoteDiffMeans)
return(myRedPartyAbsVoteDiffMeans)
}

result <- compareModifiedResults(getRedVoteDiffMeans)
writeLines("\nred vote difference mean: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nred vote difference mean: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))

###############################

getRedVoteDiffMaxes <- function(dataset) {
myRedPartyAbsVoteDiffMaxes <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffMaxes <- append(myRedPartyAbsVoteDiffMaxes, max(abs(tempRedPartyVoteDiffs)))
}
myRedPartyAbsVoteDiffMaxes <- as.numeric(myRedPartyAbsVoteDiffMaxes)
return(myRedPartyAbsVoteDiffMaxes)
}

result <- compareModifiedResults(getRedVoteDiffMaxes)
writeLines("\nred vote difference max: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nred vote difference max: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))


###############################

getRedVoteDiffSds <- function(dataset) {
myRedPartyAbsVoteDiffSds <- list()
for (i in 1:100) {
  mySubset <- subset(dataset, X.run.number. == i)
  tempRedPartyVoteDiffs <- diff(mySubset["precision..previous.cycle.votes..of.red.party.3"][,])
  myRedPartyAbsVoteDiffSds <- append(myRedPartyAbsVoteDiffSds, sd(tempRedPartyVoteDiffs))
}
myRedPartyAbsVoteDiffSds <- as.numeric(myRedPartyAbsVoteDiffSds)
return(myRedPartyAbsVoteDiffSds)
}

result <- compareModifiedResults(getRedVoteDiffSds)
writeLines("\nred vote difference sd's: default vs. modified")
writePValue(result, quote(defaultToModified))
writeCohensD(result, quote(defaultToModifiedCohd))

writeLines("\nred vote difference sd's: default vs. ideoPreferences")
writePValue(result, quote(defaultToIdeoPref))
writeCohensD(result, quote(defaultToIdeoPrefCohd))

###############################
###############################

writeLines(lineDivider)

# Zou, G. Y. (2007). Toward using confidence intervals to compare correlations.
#	Psychological Methods, 12, 399-413.

rz.ci <- function(r, N, conf.level = 0.95) {
    zr.se <- 1/(N - 3)^0.5
    moe <- qnorm(1 - (1 - conf.level)/2) * zr.se
    zu <- atanh(r) + moe
    zl <- atanh(r) - moe
    tanh(c(zl, zu))
}

r.ind.ci <- function(r1, r2, n1, n2=n1, conf.level = 0.95) {
    L1 <- rz.ci(r1, n1, conf.level = conf.level)[1]
    U1 <- rz.ci(r1, n1, conf.level = conf.level)[2]
    L2 <- rz.ci(r2, n2, conf.level = conf.level)[1]
    U2 <- rz.ci(r2, n2, conf.level = conf.level)[2]
    lower <- r1 - r2 - ((r1 - L1)^2 + (U2 - r2)^2)^0.5
    upper <- r1 - r2 + ((U1 - r1)^2 + (r2 - L2)^2)^0.5
    c(lower, upper)
}

library(Hmisc)

defaultDonationSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/Default/Run Data/olig-donation-sweep_cleaned.csv"
defaultDonationSweep <- read.csv(file= defaultDonationSweepDataName,head=TRUE,sep=",")
defaultDonationSweep.sub <- subset(defaultDonationSweep, X.step. > warmUp & X.step. <= warmUp + steps)

data_02_donationSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_02/Run Data/olig-donation-sweep_cleaned.csv"
data_02_donationSweep <- read.csv(file= data_02_donationSweepDataName,head=TRUE,sep=",")
data_02_donationSweep.sub <- subset(data_02_donationSweep, X.step. > warmUp & X.step. <= warmUp + steps)

data_10_donationSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_1/Run Data/olig-donation-sweep_cleaned.csv"
data_10_donationSweep <- read.csv(file= data_10_donationSweepDataName,head=TRUE,sep=",")
data_10_donationSweep.sub <- subset(data_10_donationSweep, X.step. > warmUp & X.step. <= warmUp + steps)

compareDonationIncomeCorrs <- function(myFunction, numberOfCycles) {
defaultCorr <- myFunction(defaultDonationSweep.sub)
data_02_Corr <- myFunction(data_02_donationSweep.sub)
data_10_Corr <- myFunction(data_10_donationSweep.sub)

defaultTo02 <- r.ind.ci(defaultCorr, data_02_Corr, numberOfCycles * 1000)
defaultFrom10 <- r.ind.ci(data_10_Corr, defaultCorr, numberOfCycles * 1000)
result <- data.table(defaultTo02, defaultFrom10)
return(result)
}

writeCorr <- function(myData, colName) {
cat(unlist(result[1, eval(colName)]))
writeLines("")
cat(unlist(result[2, eval(colName)]))
writeLines("")
}

getInitialDonationToOligDefeatsCenterCorr <- function(dataset) {
mySums1 <- lapply(aggregate(subset(dataset, OLIGARCH.INITIAL.DONATION == 0)$winner == "O", list(RunNum = subset(dataset, OLIGARCH.INITIAL.DONATION == 0)$X.run.number.), sum), function(x) {x / steps})
mySums2 <- lapply(aggregate(subset(dataset, OLIGARCH.INITIAL.DONATION == 0.2)$winner == "O", list(RunNum = subset(dataset, OLIGARCH.INITIAL.DONATION == 0.2)$X.run.number.), sum), function(x) {x / steps})
mySums3 <- lapply(aggregate(subset(dataset, OLIGARCH.INITIAL.DONATION == 0.4)$winner == "O", list(RunNum = subset(dataset, OLIGARCH.INITIAL.DONATION == 0.4)$X.run.number.), sum), function(x) {x / steps})
mySums4 <- lapply(aggregate(subset(dataset, OLIGARCH.INITIAL.DONATION == 0.6)$winner == "O", list(RunNum = subset(dataset, OLIGARCH.INITIAL.DONATION == 0.6)$X.run.number.), sum), function(x) {x / steps})
mySums5 <- lapply(aggregate(subset(dataset, OLIGARCH.INITIAL.DONATION == 0.8)$winner == "O", list(RunNum = subset(dataset, OLIGARCH.INITIAL.DONATION == 0.8)$X.run.number.), sum), function(x) {x / steps})
mySums6 <- lapply(aggregate(subset(dataset, OLIGARCH.INITIAL.DONATION == 1)$winner == "O", list(RunNum = subset(dataset, OLIGARCH.INITIAL.DONATION == 1)$X.run.number.), sum), function(x) {x / steps})
allSums <- c(mySums1$x, mySums2$x, mySums3$x, mySums4$x, mySums5$x, mySums6$x)
allFactors <- c( rep(0.0, 20), rep(0.2, 20), rep(0.4, 20), rep(0.6, 20), rep(0.8, 20), rep(1, 20) )
oligDonationSweepHighOligCorr <- rcorr(allFactors, allSums, type="spearman")$r[1,2]
return(oligDonationSweepHighOligCorr)
}

result <- compareDonationIncomeCorrs(getInitialDonationToOligDefeatsCenterCorr, 120)
writeLines("\ninitial donation to olig defeats center corr: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\ninitial donation to olig defeats center corr: 10 vs. default")
writeCorr(result, quote(defaultFrom10))


###############################

getCorr <- function(dataset, corrFrom, corrTo) {
return(rcorr(dataset[[corrFrom]], dataset[[corrTo]], type="spearman")$r[1,2])
}

getDonationToOligCorr <- function(dataset) {
return(getCorr(dataset, "OLIGARCH.INITIAL.DONATION", "precision.mean..olig..of.parties.3"))
}

result <- compareDonationIncomeCorrs(getDonationToOligCorr, 120)
writeLines("\ndonation to mean olig corr: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\ndonation to mean olig corr: 10 vs. default")
writeCorr(result, quote(defaultFrom10))


###############################

getDonationToTaxCorr <- function(dataset) {
return(getCorr(dataset, "OLIGARCH.INITIAL.DONATION", "precision.voter.tax.rate.3"))
}

result <- compareDonationIncomeCorrs(getDonationToTaxCorr, 120)
writeLines("\ndonation to tax rate corr: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\ndonation to tax rate corr: 10 vs. default")
writeCorr(result, quote(defaultFrom10))


###############################

getProfitCorr <- function(dataset) {
return(getCorr(dataset, "OLIGARCH.INITIAL.DONATION", "precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3"))
}

result <- compareDonationIncomeCorrs(getProfitCorr, 120)
writeLines("\ndonation to profit corr: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\ndonation to profit corr: 10 vs. default")
writeCorr(result, quote(defaultFrom10))


###############################


defaultAdDecaySweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/Default/Run Data/ad-decay-sweep_cleaned.csv"
defaultAdDecaySweep <- read.csv(file= defaultAdDecaySweepDataName,head=TRUE,sep=",")
defaultAdDecaySweep.sub <- subset(defaultAdDecaySweep, X.step. > warmUp & X.step. <= warmUp + steps)

data_02_adDecaySweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_02/Run Data/ad-decay-sweep_cleaned.csv"
data_02_adDecaySweep <- read.csv(file= data_02_adDecaySweepDataName,head=TRUE,sep=",")
data_02_adDecaySweep.sub <- subset(data_02_adDecaySweep, X.step. > warmUp & X.step. <= warmUp + steps)

data_10_adDecaySweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_1/Run Data/ad-decay-sweep_cleaned.csv"
data_10_adDecaySweep <- read.csv(file= data_10_adDecaySweepDataName,head=TRUE,sep=",")
data_10_adDecaySweep.sub <- subset(data_10_adDecaySweep, X.step. > warmUp & X.step. <= warmUp + steps)

compareAdIncomeCorrs <- function(myFunction, numberOfCycles) {
defaultCorr <- myFunction(defaultAdDecaySweep.sub)
data_02_Corr <- myFunction(data_02_adDecaySweep.sub)
data_10_Corr <- myFunction(data_10_adDecaySweep.sub)

defaultTo02 <- r.ind.ci(defaultCorr, data_02_Corr, numberOfCycles * 1000)
defaultFrom10 <- r.ind.ci(data_10_Corr, defaultCorr, numberOfCycles * 1000)
result <- data.table(defaultTo02, defaultFrom10)
return(result)
}

getAdDecayToOligCorr <- function(dataset) {
return(getCorr(dataset, "VOTER.AD.DECAY.FACTOR", "precision.mean..olig..of.parties.3"))
}

result <- compareAdIncomeCorrs(getAdDecayToOligCorr, 120)
writeLines("\nad decay to mean olig: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\nad decay to mean olig: 10 vs. default")
writeCorr(result, quote(defaultFrom10))


###############################

getAdDecayToProfitCorr <- function(dataset) {
return(getCorr(dataset, "VOTER.AD.DECAY.FACTOR", "precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3"))
}

result <- compareAdIncomeCorrs(getAdDecayToProfitCorr, 120)
writeLines("\nad decay to profit: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\nad decay to profit: 10 vs. default")
writeCorr(result, quote(defaultFrom10))


###############################

defaultSalienceSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/Default/Run Data/voter-salience-sweep_cleaned.csv"
defaultSalienceSweep <- read.csv(file= defaultSalienceSweepDataName,head=TRUE,sep=",")
defaultSalienceSweep.sub <- subset(defaultSalienceSweep, X.step. > warmUp & X.step. <= warmUp + steps)

data_02_salienceSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_02/Run Data/voter-salience-sweep_cleaned.csv"
data_02_salienceSweep <- read.csv(file= data_02_salienceSweepDataName,head=TRUE,sep=",")
data_02_salienceSweep.sub <- subset(data_02_salienceSweep, X.step. > warmUp & X.step. <= warmUp + steps)

data_10_salienceSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/e_100_5_1/Run Data/voter-salience-sweep_cleaned.csv"
data_10_salienceSweep <- read.csv(file= data_10_salienceSweepDataName,head=TRUE,sep=",")
data_10_salienceSweep.sub <- subset(data_10_salienceSweep, X.step. > warmUp & X.step. <= warmUp + steps)

compareSalienceIncomeCorrs <- function(myFunction, numberOfCycles) {
defaultCorr <- myFunction(defaultSalienceSweep.sub)
data_02_Corr <- myFunction(data_02_salienceSweep.sub)
data_10_Corr <- myFunction(data_10_salienceSweep.sub)

defaultTo02 <- r.ind.ci(defaultCorr, data_02_Corr, numberOfCycles * 1000)
defaultFrom10 <- r.ind.ci(data_10_Corr, defaultCorr, numberOfCycles * 1000)
result <- data.table(defaultTo02, defaultFrom10)
return(result)
}

getSalienceToOligDefeatsCenterCorr <- function(dataset) {
mySums1 <- lapply(aggregate(subset(dataset, VOTER.INITIAL.SALIENCE == 0)$winner == "O", list(RunNum = subset(dataset, VOTER.INITIAL.SALIENCE == 0)$X.run.number.), sum), function(x) {x / steps})
mySums2 <- lapply(aggregate(subset(dataset, VOTER.INITIAL.SALIENCE == 0.2)$winner == "O", list(RunNum = subset(dataset, VOTER.INITIAL.SALIENCE == 0.2)$X.run.number.), sum), function(x) {x / steps})
mySums3 <- lapply(aggregate(subset(dataset, VOTER.INITIAL.SALIENCE == 0.4)$winner == "O", list(RunNum = subset(dataset, VOTER.INITIAL.SALIENCE == 0.4)$X.run.number.), sum), function(x) {x / steps})
mySums4 <- lapply(aggregate(subset(dataset, VOTER.INITIAL.SALIENCE == 0.6)$winner == "O", list(RunNum = subset(dataset, VOTER.INITIAL.SALIENCE == 0.6)$X.run.number.), sum), function(x) {x / steps})
mySums5 <- lapply(aggregate(subset(dataset, VOTER.INITIAL.SALIENCE == 0.8)$winner == "O", list(RunNum = subset(dataset, VOTER.INITIAL.SALIENCE == 0.8)$X.run.number.), sum), function(x) {x / steps})
mySums6 <- lapply(aggregate(subset(dataset, VOTER.INITIAL.SALIENCE == 1)$winner == "O", list(RunNum = subset(dataset, VOTER.INITIAL.SALIENCE == 1)$X.run.number.), sum), function(x) {x / steps})

allSums <- c(mySums1$x, mySums2$x, mySums3$x, mySums4$x, mySums5$x, mySums6$x)
allFactors <- c( rep(0.0, 20), rep(0.2, 20), rep(0.4, 20), rep(0.6,
20), rep(0.8, 20), rep(1, 20) )
salienceSweepHighOligCorr <- rcorr(allFactors, allSums, type="spearman")$r[1,2]
return(salienceSweepHighOligCorr)
}

result <- compareSalienceIncomeCorrs(getSalienceToOligDefeatsCenterCorr, 120)
writeLines("\nsalience to olig defeats center corr: default vs. 02")
writeCorr(result, quote(defaultTo02))

writeLines("\nsalience to olig defeats center corr: 10 vs. default")
writeCorr(result, quote(defaultFrom10))

###############################
###############################

writeLines(lineDivider)

compareModifiedCorrs <- function(myFunction) {
defaultCorr <- myFunction(defaultData.sub)
modifiedCorr <- myFunction(modifiedData.sub)
ideoPrefCorr <- myFunction(ideoPrefData.sub)

defaultToModified <- r.ind.ci(defaultCorr, modifiedCorr, 100000)
defaultToIdeoPref <- r.ind.ci(defaultCorr, ideoPrefCorr, 100000)
result <- data.table(defaultToModified, defaultToIdeoPref)
return(result)
}

getTaxToSalienceCorr <- function(dataset) {
return(getCorr(dataset, "precision.voter.tax.rate.3", "precision.mean..olig.salience..of.voters.3"))
}

result <- compareModifiedCorrs(getTaxToSalienceCorr)
writeLines("\ntax to salience corr: default vs. modified")
writeCorr(result, quote(defaultToModified))

writeLines("\ntax to salience corr: default vs. ideoPreference")
writeCorr(result, quote(defaultToIdeoPref))


###############################

getProfitToSalienceCorr <- function(dataset) {
return(getCorr(dataset, "precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3", "precision.mean..olig.salience..of.voters.3"))
}

getProfitToSalienceCorrIdeoPref <- function() {
return(getCorr(ideoPrefData.sub, "precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.overall.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3", "precision.mean..olig.salience..of.voters.3"))
}

compareModifiedProfitCorrs <- function() {
defaultCorr <- getProfitToSalienceCorr(defaultData.sub)
modifiedCorr <- getProfitToSalienceCorr(modifiedData.sub)
ideoPrefCorr <- getProfitToSalienceCorrIdeoPref()

defaultToModified <- r.ind.ci(defaultCorr, modifiedCorr, 100000)
defaultToIdeoPref <- r.ind.ci(defaultCorr, ideoPrefCorr, 100000)
result <- data.table(defaultToModified, defaultToIdeoPref)
return(result)
}

result <- compareModifiedProfitCorrs()
writeLines("\nprofit to salience corr: default vs. modified")
writeCorr(result, quote(defaultToModified))

writeLines("\nprofit to salience corr: default vs. ideoPreference")
writeCorr(result, quote(defaultToIdeoPref))


###############################

modifiedDonationSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/Modified/Run Data/olig-donation-sweep_cleaned.csv"
modifiedDonationSweep <- read.csv(file= modifiedDonationSweepDataName,head=TRUE,sep=",")
modifiedDonationSweep.sub <- subset(modifiedDonationSweep, X.step. > warmUp & X.step. <= warmUp + steps)

ideoPrefDonationSweepDataName <- "/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/IdeoPreference/Run Data/olig-donation-sweep_cleaned.csv"
ideoPrefDonationSweep <- read.csv(file= ideoPrefDonationSweepDataName,head=TRUE,sep=",")
ideoPrefDonationSweep.sub <- subset(ideoPrefDonationSweep, X.step. > warmUp & X.step. <= warmUp + steps)

compareModifiedDonationSweepCorrs <- function(myFunction) {
defaultCorr <- myFunction(defaultDonationSweep.sub)
modifiedCorr <- myFunction(modifiedDonationSweep.sub)
ideoPrefCorr <- myFunction(modifiedDonationSweep.sub)

defaultToModified <- r.ind.ci(defaultCorr, modifiedCorr, 100000)
defaultToIdeoPref <- r.ind.ci(defaultCorr, ideoPrefCorr, 100000)
result <- data.table(defaultToModified, defaultToIdeoPref)
return(result)
}

result <- compareModifiedDonationSweepCorrs(getInitialDonationToOligDefeatsCenterCorr)
writeLines("\ninitial donation to olig defeats center corr: default vs. modified")
writeCorr(result, quote(defaultToModified))

writeLines("\ninitial donation to olig defeats center corr: default vs. ideoPreference")
writeCorr(result, quote(defaultToIdeoPref))
