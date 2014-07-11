myArgs <- commandArgs()
myArg <- myArgs[6]
steps <- as.integer(myArgs[7])

fileSinkName <- paste("/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/", myArg, "/statisticsOut", steps, ".txt", sep = "")
writeLines(fileSinkName)
sink(fileSinkName)

warmUp <- 300

writeLines("")
writeLines("warm up")
writeLines(toString(warmUp))

writeLines("")
writeLines("steps")
writeLines(toString(steps))

options(digits=3)

lineDivider <- "\n+++++++++++++++++++++++++++++++++++++++++++++++++\n"
writeLines(lineDivider)
writeLines("default-test")
runDataFolderName <- paste("/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/", myArg, "/Run Data", sep = "")
defaultDataName <- paste(runDataFolderName, "/default-test_cleaned.csv", sep = "")
defaultData <- read.csv(file= defaultDataName,head=TRUE,sep=",")
defaultData.sub <- subset(defaultData, X.step. > warmUp & X.step. <= warmUp + steps)

myIdeoMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myIdeoMeans <- append(myIdeoMeans, mean(mySubset[,c('precision.mean..ideo..of.parties.3')]))
}
myIdeoMeans <- as.numeric(myIdeoMeans)
t.test(myIdeoMeans, mu=0)

myOligMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myOligMeans <- append(myOligMeans, mean(mySubset[,c('precision.mean..olig..of.parties.3')]))
}
myOligMeans <- as.numeric(myOligMeans)
t.test(myOligMeans,mu=0, alternative="less")
t.test(myOligMeans,mu=-100, alternative="greater")

mySalienceMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  mySalienceMeans <- append(mySalienceMeans, mean(mySubset[,c('precision.mean..olig.salience..of.voters.3')]))
}
mySalienceMeans <- as.numeric(mySalienceMeans)
t.test(mySalienceMeans,mu=0, alternative="greater")

myTaxRateMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myTaxRateMeans <- append(myTaxRateMeans, mean(mySubset[,c('precision.voter.tax.rate.3')]))
}
myTaxRateMeans <- as.numeric(myTaxRateMeans)
t.test(myTaxRateMeans,mu=0.5, alternative="less")

myDonationMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myDonationMeans <- append(myDonationMeans, mean(mySubset[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')]))
}
myDonationMeans <- as.numeric(myDonationMeans)
t.test(myDonationMeans,mu=0, alternative="greater")

writeLines("")
writeLines("tax rate vs. salience")
myCcfs <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  ccfData <- ccf(mySubset[,c('precision.voter.tax.rate.3')], mySubset[,c('precision.mean..olig.salience..of.voters.3')], lag.max = 5, type = c("correlation"),
    plot = FALSE)
  myCcfs <- append(myCcfs, ccfData)
}
myLags <- list()
myMeans <- list()
mySds <- list()
for (currentLag in -5:5) {
  myLags <- c(myLags, currentLag)
  currentLagCcfs <- list()
  for (currentRunNumber in 1:100) {
    currentLagCcfs <- append(currentLagCcfs, myCcfs[currentRunNumber * 6 - 5]$acf[currentLag + 6])
  }
  currentLagCcfs <- unlist(currentLagCcfs)
  myMeans <- append(myMeans, mean(currentLagCcfs))
  mySds <- append(mySds, sd(currentLagCcfs))
}
myMeans <- unlist(myMeans)
mySds <- unlist(mySds)

writeLines("max index")
cat(seq(along=myMeans)[myMeans == max(myMeans)])
writeLines("\nmax correlation")
maxTaxSalienceCorr <- max(myMeans)
cat(maxTaxSalienceCorr)

a <- maxTaxSalienceCorr
s <- mySds[seq(along=myMeans)[myMeans == max(myMeans)]]
n <- 100
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nlower 95% confidence bound")
cat(a - error)
writeLines("\nupper 95% confidence bound")
cat(a + error)
writeLines("")


writeLines("")
writeLines("profit vs. salience")
myCcfs <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  ccfData <- ccf(mySubset[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], mySubset[,c('precision.mean..olig.salience..of.voters.3')], lag.max = 5, type = c("correlation"),
    plot = FALSE)
  myCcfs <- append(myCcfs, ccfData)
}
myLags <- list()
myMeans <- list()
mySds <- list()
for (currentLag in -5:5) {
  myLags <- c(myLags, currentLag)
  currentLagCcfs <- list()
  for (currentRunNumber in 1:100) {
    currentLagCcfs <- append(currentLagCcfs, myCcfs[currentRunNumber * 6 - 5]$acf[currentLag + 6])
  }
  currentLagCcfs <- unlist(currentLagCcfs)
  myMeans <- append(myMeans, mean(currentLagCcfs))
  mySds <- append(mySds, sd(currentLagCcfs))
}
myMeans <- unlist(myMeans)
mySds <- unlist(mySds)
writeLines("max index")
cat(seq(along=myMeans)[myMeans == max(myMeans)])
writeLines("\nmax correlation")
maxProfitSalienceCorr <- max(myMeans)
cat(maxProfitSalienceCorr)

a <- maxProfitSalienceCorr
s <- mySds[seq(along=myMeans)[myMeans == max(myMeans)]]
n <- 100
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nlower 95% confidence bound")
cat(a - error)
writeLines("\nupper 95% confidence bound")
cat(a + error)
writeLines("")

writeLines("")
writeLines("donation size vs. salience")
myCcfs <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  ccfData <- ccf(mySubset[,c('precision.mean..olig.salience..of.voters.3')], mySubset[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], lag.max = 5, type = c("correlation"),
    plot = FALSE)
  myCcfs <- append(myCcfs, ccfData)
}
myLags <- list()
myMeans <- list()
mySds <- list()
for (currentLag in -5:5) {
  myLags <- c(myLags, currentLag)
  currentLagCcfs <- list()
  for (currentRunNumber in 1:100) {
    currentLagCcfs <- append(currentLagCcfs, myCcfs[currentRunNumber * 6 - 5]$acf[currentLag + 6])
  }
  currentLagCcfs <- unlist(currentLagCcfs)
  myMeans <- append(myMeans, mean(currentLagCcfs))
  mySds <- append(mySds, sd(currentLagCcfs))
}
myMeans <- unlist(myMeans)
mySds <- unlist(mySds)
writeLines("max index")
cat(seq(along=myMeans)[myMeans == max(myMeans)])
writeLines("\nmax correlation")
maxDonationSalienceCorr <- max(myMeans)
cat(maxDonationSalienceCorr)

a <- maxDonationSalienceCorr
s <- mySds[seq(along=myMeans)[myMeans == max(myMeans)]]
n <- 100
error <- qt(0.975,df=n-1)*s/sqrt(n)
writeLines("\nlower 95% confidence bound")
cat(a - error)
writeLines("\nupper 95% confidence bound")
cat(a + error)
writeLines("")

writeLines("")
writeLines("blue mean olig")
blueMeanOlig <- mean(defaultData.sub["precision..olig..of.blue.party.3"][,])
cat(blueMeanOlig)

writeLines("")
writeLines("red mean olig")
redMeanOlig <- mean(defaultData.sub["precision..olig..of.red.party.3"][,])
cat(redMeanOlig)

writeLines("")
writeLines("blue mean ideo")
blueMeanIdeo <- mean(defaultData.sub["precision..ideo..of.blue.party.3"][,])
cat(blueMeanIdeo)

writeLines("")
writeLines("red mean ideo")
redMeanIdeo <- mean(defaultData.sub["precision..ideo..of.red.party.3"][,])
cat(redMeanIdeo)

################################################

writeLines(lineDivider)
writeLines("fixed-olig")

fixedOligDataName <- paste(runDataFolderName, "/fixed-olig_cleaned.csv", sep = "")
fixedOligData <- read.csv(file= fixedOligDataName,head=TRUE,sep=",")
fixedOligData.sub <- subset(fixedOligData, X.step. > warmUp & X.step. <= warmUp + steps)

myFixedDonationMeans <- list()
for (i in 1:100) {
  mySubset <- subset(fixedOligData.sub, X.run.number. == i)
  myFixedDonationMeans <- append(myFixedDonationMeans, mean(mySubset[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')]))
}
myFixedDonationMeans <- as.numeric(myFixedDonationMeans)

t.test(myFixedDonationMeans, myDonationMeans,  alternative="less")

myDefaultProfitMeans <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myDefaultProfitMeans <- append(myDefaultProfitMeans, mean(mySubset[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')]))
}
myDefaultProfitMeans <- as.numeric(myDefaultProfitMeans)

myFixedProfitMeans <- list()
for (i in 1:100) {
  mySubset <- subset(fixedOligData.sub, X.run.number. == i)
  myFixedProfitMeans <- append(myFixedProfitMeans, mean(mySubset[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')]))
}
myFixedProfitMeans <- as.numeric(myFixedProfitMeans)

t.test(myFixedProfitMeans, myDefaultProfitMeans,  alternative="greater")

################################################

writeLines(lineDivider)
writeLines("olig-donation-sweep")

oligSweepDataName <- paste(runDataFolderName, "/olig-donation-sweep_cleaned.csv", sep = "")
oligarchDonationSweep <- read.csv(file= oligSweepDataName,head=TRUE,sep=",")
oligarchDonationSweep.sub <- subset(oligarchDonationSweep, X.step. > warmUp & X.step. <= warmUp + steps)

writeLines("")
writeLines("high-olig-defeats-center")
library(Hmisc)
mySums1 <- lapply(aggregate(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0)$winner == "O", list(RunNum = subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0)$X.run.number.), sum), function(x) {x / steps})
mySums2 <- lapply(aggregate(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.2)$winner == "O", list(RunNum = subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.2)$X.run.number.), sum), function(x) {x / steps})
mySums3 <- lapply(aggregate(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.4)$winner == "O", list(RunNum = subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.4)$X.run.number.), sum), function(x) {x / steps})
mySums4 <- lapply(aggregate(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.6)$winner == "O", list(RunNum = subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.6)$X.run.number.), sum), function(x) {x / steps})
mySums5 <- lapply(aggregate(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.8)$winner == "O", list(RunNum = subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.8)$X.run.number.), sum), function(x) {x / steps})
mySums6 <- lapply(aggregate(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 1)$winner == "O", list(RunNum = subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 1)$X.run.number.), sum), function(x) {x / steps})
allSums <- c(mySums1$x, mySums2$x, mySums3$x, mySums4$x, mySums5$x, mySums6$x)
allFactors <- c( rep(0.0, 20), rep(0.2, 20), rep(0.4, 20), rep(0.6, 20), rep(0.8, 20), rep(1, 20) )
oligDonationSweepHighOligCorr <- rcorr(allFactors, allSums, type="spearman")$r[1,2]
cat(oligDonationSweepHighOligCorr)

writeLines("")
writeLines("mean-Party-olig")
oligDonationSweepMeanOligCorr <- rcorr(oligarchDonationSweep.sub$OLIGARCH.INITIAL.DONATION, oligarchDonationSweep.sub$precision.mean..olig..of.parties.3, type="spearman")$r[1,2]
cat(oligDonationSweepMeanOligCorr)

writeLines("")
writeLines("tax-rate")
oligDonationSweepTaxCorr <- rcorr(oligarchDonationSweep.sub$OLIGARCH.INITIAL.DONATION, oligarchDonationSweep.sub$precision.voter.tax.rate.3, type="spearman")$r[1,2]
cat(oligDonationSweepTaxCorr)

writeLines("")
writeLines("oligarch-profit")
oligDonationSweepProfitCorr <- rcorr(oligarchDonationSweep.sub$OLIGARCH.INITIAL.DONATION, oligarchDonationSweep.sub$precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3, type="spearman")$r[1,2]
cat(oligDonationSweepProfitCorr)

################################################

writeLines(lineDivider)
writeLines("ad-decay-sweep")

adDecaySweepDataName <- paste(runDataFolderName, "/ad-decay-sweep_cleaned.csv", sep = "")
adDecaySweep <- read.csv(file= adDecaySweepDataName,head=TRUE,sep=",")
adDecaySweep.sub <- subset(adDecaySweep, X.step. > warmUp & X.step. <= warmUp + steps)

writeLines("")
writeLines("high-olig-defeats-center")
mySums1 <- lapply(aggregate(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.12)$winner == "O", list(RunNum = subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.12)$X.run.number.), sum), function(x) {x / steps})
mySums2 <- lapply(aggregate(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.1)$winner == "O", list(RunNum = subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.1)$X.run.number.), sum), function(x) {x / steps})
mySums3 <- lapply(aggregate(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.08)$winner == "O", list(RunNum = subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.08)$X.run.number.), sum), function(x) {x / steps})
mySums4 <- lapply(aggregate(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.06)$winner == "O", list(RunNum = subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.06)$X.run.number.), sum), function(x) {x / steps})
mySums5 <- lapply(aggregate(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.04)$winner == "O", list(RunNum = subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.04)$X.run.number.), sum), function(x) {x / steps})
mySums6 <- lapply(aggregate(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.02)$winner == "O", list(RunNum = subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.02)$X.run.number.), sum), function(x) {x / steps})

allSums <- c(mySums1$x, mySums2$x, mySums3$x, mySums4$x, mySums5$x, mySums6$x)
allFactors <- c( rep(-0.12, 20), rep(-0.1, 20), rep(-0.08, 20), rep(-0.06, 20), rep(-0.04, 20), rep(-0.02, 20) )
adSweepHighOligCorr <- rcorr(allFactors, allSums, type="spearman")$r[1,2]
cat(adSweepHighOligCorr)

writeLines("")
writeLines("mean-Party-olig")
adSweepMeanOligCorr <- rcorr(adDecaySweep.sub$VOTER.AD.DECAY.FACTOR, adDecaySweep.sub$precision.mean..olig..of.parties.3, type="spearman")$r[1,2]
cat(adSweepMeanOligCorr)

writeLines("")
writeLines("tax-rate")
adSweepTaxCorr <- rcorr(adDecaySweep.sub$VOTER.AD.DECAY.FACTOR, adDecaySweep.sub$precision.voter.tax.rate.3, type="spearman")$r[1,2]
cat(adSweepTaxCorr)

writeLines("")
writeLines("oligarch-profit")
adSweepProfitCorr <- rcorr(adDecaySweep.sub$VOTER.AD.DECAY.FACTOR, adDecaySweep.sub$precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3, type="spearman")$r[1,2]
cat(adSweepProfitCorr)

writeLines("")
writeLines("donation-size")
adSweepDonationCorr <- rcorr(adDecaySweep.sub$VOTER.AD.DECAY.FACTOR, adDecaySweep.sub$precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3, type="spearman")$r[1,2]
cat(adSweepDonationCorr)

################################################

writeLines(lineDivider)
writeLines("voter-salience-sweep")

salienceSweepDataName <- paste(runDataFolderName, "/voter-salience-sweep_cleaned.csv", sep = "")
oligSalienceSweep <- read.csv(file= salienceSweepDataName,head=TRUE,sep=",")
oligSalienceSweep.sub <- subset(oligSalienceSweep, X.step. > warmUp & X.step. <= warmUp + steps)

writeLines("")
writeLines("high-olig-defeats-center")
mySums1 <- lapply(aggregate(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0)$winner == "O", list(RunNum = subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0)$X.run.number.), sum), function(x) {x / steps})
mySums2 <- lapply(aggregate(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.2)$winner == "O", list(RunNum = subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.2)$X.run.number.), sum), function(x) {x / steps})
mySums3 <- lapply(aggregate(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.4)$winner == "O", list(RunNum = subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.4)$X.run.number.), sum), function(x) {x / steps})
mySums4 <- lapply(aggregate(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.6)$winner == "O", list(RunNum = subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.6)$X.run.number.), sum), function(x) {x / steps})
mySums5 <- lapply(aggregate(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.8)$winner == "O", list(RunNum = subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.8)$X.run.number.), sum), function(x) {x / steps})
mySums6 <- lapply(aggregate(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 1)$winner == "O", list(RunNum = subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 1)$X.run.number.), sum), function(x) {x / steps})

allSums <- c(mySums1$x, mySums2$x, mySums3$x, mySums4$x, mySums5$x, mySums6$x)
allFactors <- c( rep(0.0, 20), rep(0.2, 20), rep(0.4, 20), rep(0.6,
20), rep(0.8, 20), rep(1, 20) )
salienceSweepHighOligCorr <- rcorr(allFactors, allSums, type="spearman")$r[1,2]
cat(salienceSweepHighOligCorr)

writeLines("")
writeLines("tax-rate")
salienceSweepTaxCorr <- rcorr(oligSalienceSweep.sub$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub$precision.voter.tax.rate.3, type="spearman")$r[1,2]
cat(salienceSweepTaxCorr)

writeLines("")
writeLines("mean-Party-olig")
salienceSweepMeanOligCorr <- rcorr(oligSalienceSweep.sub$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub$precision.mean..olig..of.parties.3, type="spearman")$r[1,2]
cat(salienceSweepMeanOligCorr)

writeLines("")
writeLines("oligarch-profit")
salienceSweepProfitCorr <- rcorr(oligSalienceSweep.sub$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub$precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3, type="spearman")$r[1,2]
cat(salienceSweepProfitCorr)

writeLines("")
writeLines("donation-size")
salienceSweepDonationCorr <- rcorr(oligSalienceSweep.sub$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub$precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3, type="spearman")$r[1,2]
cat(salienceSweepDonationCorr)

################################################

writeLines(lineDivider)
writeLines("voter-salience-sweep LOW-SALIENCE")

oligSalienceSweep.sub.lowSal <- subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE <= 0.6)

writeLines("")
writeLines("tax-rate")
salienceSweepLowSalTaxCorr <- rcorr(oligSalienceSweep.sub.lowSal$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub.lowSal$precision.voter.tax.rate.3, type="spearman")$r[1,2]
cat(salienceSweepLowSalTaxCorr)

writeLines("")
writeLines("mean-Party-olig")
salienceSweepLowSalMeanOligCorr <- rcorr(oligSalienceSweep.sub.lowSal$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub.lowSal$precision.mean..olig..of.parties.3, type="spearman")$r[1,2]
cat(salienceSweepLowSalMeanOligCorr)

writeLines("")
writeLines("oligarch-profit")
salienceSweepLowSalProfitCorr <- rcorr(oligSalienceSweep.sub.lowSal$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub.lowSal$precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3, type="spearman")$r[1,2]
cat(salienceSweepLowSalProfitCorr)

writeLines("")
writeLines("donation-size")
salienceSweepLowSalDonationCorr <- rcorr(oligSalienceSweep.sub.lowSal$VOTER.INITIAL.SALIENCE, oligSalienceSweep.sub.lowSal$precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3, type="spearman")$r[1,2]
cat(salienceSweepLowSalDonationCorr)

################################################

writeLines(lineDivider)
writeLines("voter-memory-sweep")

memorySweepDataName <- paste(runDataFolderName, "/voter-memory-sweep_cleaned.csv", sep = "")
memoryStrengthSweep <- read.csv(file=memorySweepDataName,head=TRUE,sep=",")
memoryStrengthSweep.sub <- subset(memoryStrengthSweep, X.step. > warmUp & X.step. <= warmUp + steps)

writeLines("")
writeLines("high-olig-defeats-center")
mySums1 <- lapply(aggregate(subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.1)$winner == "O", list(RunNum = subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.1)$X.run.number.), sum), function(x) {x / steps})
mySums2 <- lapply(aggregate(subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.3)$winner == "O", list(RunNum = subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.3)$X.run.number.), sum), function(x) {x / steps})
mySums3 <- lapply(aggregate(subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.5)$winner == "O", list(RunNum = subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.5)$X.run.number.), sum), function(x) {x / steps})
mySums4 <- lapply(aggregate(subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.7)$winner == "O", list(RunNum = subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.7)$X.run.number.), sum), function(x) {x / steps})
mySums5 <- lapply(aggregate(subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.9)$winner == "O", list(RunNum = subset(memoryStrengthSweep.sub, VOTER.MEMORY.STRENGTH == 0.9)$X.run.number.), sum), function(x) {x / steps})

allSums <- c(mySums1$x, mySums2$x, mySums3$x, mySums4$x, mySums5$x)
allFactors <- c( rep(0.1, 20), rep(0.3, 20), rep(0.5, 20), rep(0.7, 20), rep(0.9, 20) )
memorySweepHighOligCorr <- rcorr(allFactors, allSums, type="spearman")$r[1,2]
cat(memorySweepHighOligCorr)

writeLines("")
writeLines("tax-rate")
memorySweepTaxCorr <- rcorr(memoryStrengthSweep.sub$VOTER.MEMORY.STRENGTH, memoryStrengthSweep.sub$precision.voter.tax.rate.3, type="spearman")$r[1,2]
cat(memorySweepTaxCorr)

writeLines("")
writeLines("mean-Party-olig")
memorySweepMeanOligCorr <- rcorr(memoryStrengthSweep.sub$VOTER.MEMORY.STRENGTH, memoryStrengthSweep.sub$precision.mean..olig..of.parties.3, type="spearman")$r[1,2]
cat(memorySweepMeanOligCorr)

writeLines("")
writeLines("oligarch-profit")
memorySweepProfitCorr <- rcorr(memoryStrengthSweep.sub$VOTER.MEMORY.STRENGTH, memoryStrengthSweep.sub$precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3, type="spearman")$r[1,2]
cat(memorySweepProfitCorr)

writeLines("")
writeLines("donation-size")
memorySweepDonationCorr <- rcorr(memoryStrengthSweep.sub$VOTER.MEMORY.STRENGTH, memoryStrengthSweep.sub$precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3, type="spearman")$r[1,2]
cat(memorySweepDonationCorr)

headerString <- "\n\nrun name,mean ideo,mean olig,mean salience,mean tax,mean donation,tax-salience corr max,profit-salience corr max,donation-salience corr max,blue mean olig,red mean olig,blue mean ideo,red mean ideo,fixed olig donation mean,profit mean,fixed olig profit mean,donation-sweep olig-defeats-center corr,donation-sweep mean-Party-olig corr,donation-sweep tax corr,donation-sweep profit corr,ad-sweep mean-Party-olig corr,ad-sweep tax corr,ad-sweep profit corr,ad-sweep donation corr,salience-sweep olig-defeats-center corr,salience-sweep mean-Party-olig corr,salience-sweep tax corr,salience-sweep profit corr,salience-sweep donation corr,salience-sweep-low-sal mean-Party-olig corr,salience-sweep-low-sal tax corr,salience-sweep-low-sal profit corr,salience-sweep-low-sal donation corr,memory-sweep olig-defeats-center corr,memory-sweep mean-Party-olig corr,memory-sweep tax corr,memory-sweep profit corr,memory-sweep donation corr"
cat(headerString)

pr <- function(x) format(round(x, 3), nsmall=3)
writeLines("")
myTitle <- paste(myArg, steps, sep = "_")
resultString <- paste(myTitle, pr(mean(myIdeoMeans)), pr(mean(myOligMeans)), pr(mean(mySalienceMeans)), pr(mean(myTaxRateMeans)), pr(mean(myDonationMeans)), pr(maxTaxSalienceCorr), pr(maxProfitSalienceCorr), pr(maxDonationSalienceCorr), pr(blueMeanOlig), pr(redMeanOlig), pr(blueMeanIdeo), pr(redMeanIdeo), pr(mean(myFixedDonationMeans)), pr(mean(myDefaultProfitMeans)), pr(mean(myFixedProfitMeans)), pr(oligDonationSweepHighOligCorr), pr(oligDonationSweepMeanOligCorr), pr(oligDonationSweepTaxCorr), pr(oligDonationSweepProfitCorr), pr(adSweepMeanOligCorr), pr(adSweepTaxCorr), pr(adSweepProfitCorr), pr(adSweepDonationCorr), pr(salienceSweepHighOligCorr), pr(salienceSweepMeanOligCorr), pr(salienceSweepTaxCorr), pr(salienceSweepProfitCorr), pr(salienceSweepDonationCorr), pr(salienceSweepLowSalMeanOligCorr), pr(salienceSweepLowSalTaxCorr), pr(salienceSweepLowSalProfitCorr), pr(salienceSweepLowSalDonationCorr), pr(memorySweepHighOligCorr), pr(memorySweepMeanOligCorr), pr(memorySweepTaxCorr), pr(memorySweepProfitCorr), pr(memorySweepDonationCorr), sep = ",")
cat(resultString)

writeLines("")

################################################
### Figures

myXVals <- list()
mySalienceVals <- list()
myTaxRateVals <- list()
for (myStep in warmUp:(warmUp + steps)) {
	myXVals <- append(myXVals, myStep)
	mySubset <- subset(defaultData.sub, X.step. == myStep)
	mySalienceVals <- append(mySalienceVals, mean(mySubset[,c('precision.mean..olig.salience..of.voters.3')]))
	myTaxRateVals <- append(myTaxRateVals, mean(mySubset[,c('precision.voter.tax.rate.3')]))
}
colors <- rainbow(2) 
figureFolderName <- paste("/Users/masonwright/Dropbox/Work Notes/ViMaP/Oligarchy/Automation/Results/", myArg, "/Figures", steps, sep = "")
pdfName <- paste(figureFolderName, "/16-DefMod Tax and Sal v Time.pdf", sep = "")
pdf(pdfName)
plot(myXVals, myTaxRateVals, type="n", xlab="Time Step", ylab="", ylim=c(0, 1), frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(myXVals, myTaxRateVals, type="l", col=colors[1])
lines(myXVals, mySalienceVals, type="l", col=colors[2])
legend(warmUp, 1, legend=c("Tax Rate", "Voter Olig-Salience"), cex=0.8, col=colors, lty=1)
invisible(dev.off())

###########################
### mean over all runs

myMeanSpeq <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myts <- ts(mySubset["precision.voter.tax.rate.3"])
  mySpectrum <- spectrum(myts, spans=c(3,3), plot = FALSE)
  if (length(myMeanSpeq) == 0) {
    myMeanSpeq <- mySpectrum$spec
  } else {
    myMeanSpeq <- apply(rbind(myMeanSpeq, mySpectrum$spec), 2 , mean, na.rm = TRUE)
  }
}
pdfName <- paste(figureFolderName, "/16E-DefMod Mean Tax Spectra 3-3 Smooth.pdf", sep = "")
pdf(pdfName)
plot(mySpectrum$freq, log(myMeanSpeq), type="n", xlab="Frequency", ylab="Log Spectrum", frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(mySpectrum$freq, log(myMeanSpeq), type="l", col="black")
invisible(dev.off())

###########################
### for just one run

runNumber <- 1
mySubset <- subset(defaultData.sub, X.run.number. == runNumber)
myts <- ts(mySubset["precision.voter.tax.rate.3"])
mySpectrum <- spectrum(myts, spans=c(5,5), plot = FALSE)
pdfName <- paste(figureFolderName, "/16F-DefMod OneRun Tax Spectra 5-5 Smooth.pdf", sep = "")
pdf(pdfName)
plot(mySpectrum$freq, log(mySpectrum$spec), type="n", xlab="Frequency", ylab="Log Spectrum", frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(mySpectrum$freq, log(mySpectrum$spec), type="l", col="black")
invisible(dev.off())

###########################

runNumber <- 1
defaultData.sub.oneRun <- subset(defaultData.sub, X.run.number. == runNumber)
myXVals <- list()
mySalienceVals <- list()
myTaxRateVals <- list()
for (myStep in warmUp:(warmUp + steps)) {
	myXVals <- append(myXVals, myStep)
	mySubset <- subset(defaultData.sub.oneRun, X.step. == myStep)
	mySalienceVals <- append(mySalienceVals, mean(mySubset[,c('precision.mean..olig.salience..of.voters.3')]))
	myTaxRateVals <- append(myTaxRateVals, mean(mySubset[,c('precision.voter.tax.rate.3')]))
}
colors <- rainbow(2) 
pdfName <- paste(figureFolderName, "/16B-OneRun DefMod Tax n Sal v Time.pdf", sep = "")
pdf(pdfName)
plot(myXVals, myTaxRateVals, type="n", xlab="Time Step", ylab="", ylim=c(0, 1), frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(myXVals, myTaxRateVals, type="l", col=colors[1])
lines(myXVals, mySalienceVals, type="l", col=colors[2])
legend(warmUp, 1, legend=c("Tax Rate", "Voter Olig-Salience"), cex=0.8, col=colors, lty=1)
invisible(dev.off())

###########################

maWidth <- 10
ma <- function(x,n=maWidth){filter(x,rep(1/n,n), sides=1)}
myXVals <- list()
mySalienceVals <- list()
myTaxRateVals <- list()
salTs <- ts(defaultData.sub.oneRun["precision.mean..olig.salience..of.voters.3"])
taxTs <- ts(defaultData.sub.oneRun["precision.voter.tax.rate.3"])
salMa <- ma(salTs)
taxMa <- ma(taxTs)
for (myStep in 1:steps) {
	myXVals <- append(myXVals, myStep + warmUp)
	mySalienceVals <- append(mySalienceVals, salMa[myStep])
	myTaxRateVals <- append(myTaxRateVals, taxMa[myStep])
}
colors <- rainbow(2) 
pdfName <- paste(figureFolderName, "/16C-MA10 OneRun DefMod Tax n Sal v Time.pdf", sep = "")
pdf(pdfName)
plot(myXVals, myTaxRateVals, type="n", xlab="Time Step", ylab="", ylim=c(0, 1), frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(myXVals, myTaxRateVals, type="l", col=colors[1])
lines(myXVals, mySalienceVals, type="l", col=colors[2])
legend(warmUp, 1, legend=c("Tax Rate", "Voter Olig-Salience"), cex=0.8, col=colors, lty=1)
invisible(dev.off())

###########################

maWidth <- 50
ma <- function(x,n=maWidth){filter(x,rep(1/n,n), sides=1)}
myXVals <- list()
mySalienceVals <- list()
myTaxRateVals <- list()
salTs <- ts(defaultData.sub.oneRun["precision.mean..olig.salience..of.voters.3"])
taxTs <- ts(defaultData.sub.oneRun["precision.voter.tax.rate.3"])
salMa <- ma(salTs)
taxMa <- ma(taxTs)
for (myStep in 1:steps) {
	myXVals <- append(myXVals, myStep + warmUp)
	mySalienceVals <- append(mySalienceVals, salMa[myStep])
	myTaxRateVals <- append(myTaxRateVals, taxMa[myStep])
}
colors <- rainbow(2) 
pdfName <- paste(figureFolderName, "/16D-MA50 OneRun DefMod Tax n Sal v Time.pdf", sep = "")
pdf(pdfName)
plot(myXVals, myTaxRateVals, type="n", xlab="Time Step", ylab="", ylim=c(0, 1), frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(myXVals, myTaxRateVals, type="l", col=colors[1])
lines(myXVals, mySalienceVals, type="l", col=colors[2])
legend(warmUp, 1, legend=c("Tax Rate", "Voter Olig-Salience"), cex=0.8, col=colors, lty=1)
invisible(dev.off())

###########################

myXVals <- list()
myYVals <- list()
for (myStep in warmUp:(warmUp + steps)) {
	myXVals <- append(myXVals, myStep)
	mySubset <- subset(defaultData.sub, X.step. == myStep)
	myYVals <- append(myYVals, mean(mySubset[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')]))
}
pdfName <- paste(figureFolderName, "/17-DefMod Profit v Time.pdf", sep = "")
pdf(pdfName)
plot(myXVals, myYVals, type="n", xlab="Time Step", ylab="Mean Oligarch Profit", ylim=c(0, 25), frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(myXVals, myYVals, type="l")
invisible(dev.off())

###########################
### mean over all runs

myMeanSpeq <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  myts <- ts(mySubset["precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3"])
  mySpectrum <- spectrum(myts, spans=c(3,3), plot = FALSE)
  if (length(myMeanSpeq) == 0) {
    myMeanSpeq <- mySpectrum$spec
  } else {
    myMeanSpeq <- apply(rbind(myMeanSpeq, mySpectrum$spec), 2 , mean, na.rm = TRUE)
  }
}
pdfName <- paste(figureFolderName, "/17B-DefMod Profit Mean Spectrum 3-3 Smooth.pdf", sep = "")
pdf(pdfName)
plot(mySpectrum$freq, log(myMeanSpeq), type="n", xlab="Frequency", ylab="Log Spectrum", frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(mySpectrum$freq, log(myMeanSpeq), type="l", col="black")
invisible(dev.off())

###########################
### for just one run

runNumber <- 1
mySubset <- subset(defaultData.sub, X.run.number. == runNumber)
myts <- ts(mySubset["precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3"])
mySpectrum <- spectrum(myts, spans=c(5,5), plot = FALSE)
pdfName <- paste(figureFolderName, "/17C-DefMod Profit OneRun Spectrum 5-5 Smooth.pdf", sep = "")
pdf(pdfName)
plot(mySpectrum$freq, log(mySpectrum$spec), type="n", xlab="Frequency", ylab="Log Spectrum", frame=F, col.axis="#888888", col.lab="#888888", las=1)
lines(mySpectrum$freq, log(mySpectrum$spec), type="l", col="black")
invisible(dev.off())

###########################

myCcfs <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  ccfData <- ccf(mySubset[,c('precision.voter.tax.rate.3')], mySubset[,c('precision.mean..olig.salience..of.voters.3')], lag.max = 5, type = c("correlation"),
    plot = FALSE)
  myCcfs <- append(myCcfs, ccfData)
}
myLags <- list()
myMeans <- list()
for (currentLag in -5:5) {
  myLags <- c(myLags, currentLag)
  currentLagCcfs <- 0
  for (currentRunNumber in 1:100) {
    currentLagCcfs <- currentLagCcfs + myCcfs[currentRunNumber * 6 - 5]$acf[currentLag + 6]
  }
  myMeans <- c(myMeans, currentLagCcfs / 100)
}
pdfName <- paste(figureFolderName, "/19-DefMod Tax to Salience Corr.pdf", sep = "")
pdf(pdfName)
plot(myLags, myMeans, xlab="Lag", ylab="Correlation", ylim=c(-1, 1), xaxp = c(-5, 5, 10),
frame=F, col.axis="#888888", col.lab="#888888", las=1)
invisible(dev.off())


myCcfs <- list()
for (i in 1:100) {
  mySubset <- subset(defaultData.sub, X.run.number. == i)
  ccfData <- ccf(mySubset[,c('precision.mean..olig.salience..of.voters.3')], mySubset[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], lag.max = 5, type = c("correlation"),
    plot = FALSE)
  myCcfs <- append(myCcfs, ccfData)
}
myLags <- list()
myMeans <- list()
for (currentLag in -5:5) {
  myLags <- c(myLags, currentLag)
  currentLagCcfs <- 0
  for (currentRunNumber in 1:100) {
    currentLagCcfs <- currentLagCcfs + myCcfs[currentRunNumber * 6 - 5]$acf[currentLag + 6]
  }
  myMeans <- c(myMeans, currentLagCcfs / 100)
}
pdfName <- paste(figureFolderName, "/20-DefMod Salience to Donation Corr.pdf", sep = "")
pdf(pdfName)
plot(myLags, myMeans, xlab="Lag", ylab="Correlation", ylim=c(-1, 1), xaxp = c(-5, 5, 10),
frame=F, col.axis="#888888", col.lab="#888888", las=1)
invisible(dev.off())


pdfName <- paste(figureFolderName, "/21-Fixed v Free Donations.pdf", sep = "")
pdf(pdfName)
boxplot(fixedOligData.sub[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], defaultData.sub[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], names=c("Fixed Party Olig", "Free Party Olig"), ylab="Mean Donation Size",
frame=F, col ="gray", boxwex=0.3, col.axis="#888888", col.lab="#888888", las=1, staplewex=0.4, outcex=0.8, outcol="gray")
invisible(dev.off())


pdfName <- paste(figureFolderName, "/22-OligSweep Abs Mean Ideo.pdf", sep = "")
pdf(pdfName)
boxplot(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0)[,c('precision.abs.mean..ideo..of.parties.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.2)[,c('precision.abs.mean..ideo..of.parties.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.4)[,c('precision.abs.mean..ideo..of.parties.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.6)[,c('precision.abs.mean..ideo..of.parties.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.8)[,c('precision.abs.mean..ideo..of.parties.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 1)[,c('precision.abs.mean..ideo..of.parties.3')], 
names=c("0.0", "0.2", "0.4", "0.6", "0.8", "1"), xlab="Voter Olig Salience", ylab="Abs Val Mean Party Ideo",
frame=F, col ="gray", boxwex=0.3, col.axis="#888888", col.lab="#888888", las=1, staplewex=0.4, outcex=0.8, outcol="gray")
invisible(dev.off())


pdfName <- paste(figureFolderName, "/23-OligSweep Tax Rate.pdf", sep = "")
pdf(pdfName)
boxplot(subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0)[,c('precision.voter.tax.rate.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.2)[,c('precision.voter.tax.rate.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.4)[,c('precision.voter.tax.rate.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.6)[,c('precision.voter.tax.rate.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 0.8)[,c('precision.voter.tax.rate.3')], 
subset(oligSalienceSweep.sub, VOTER.INITIAL.SALIENCE == 1)[,c('precision.voter.tax.rate.3')], 
names=c("0.0", "0.2", "0.4", "0.6", "0.8", "1"), xlab="Voter Olig Salience", ylab="Tax Rate",
frame=F, col ="gray", boxwex=0.3, col.axis="#888888", col.lab="#888888", las=1, staplewex=0.4, outcex=0.8, outcol="gray")
invisible(dev.off())


pdfName <- paste(figureFolderName, "/24-DonatSweep Profit.pdf", sep = "")
pdf(pdfName)
boxplot(subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0)[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], 
subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.2)[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], 
subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.4)[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], 
subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.6)[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], 
subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 0.8)[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], 
subset(oligarchDonationSweep.sub, OLIGARCH.INITIAL.DONATION == 1)[,c('precision...voter.tax.rate...VOTER.COUNT...VOTER.GROSS.INCOME....sum..donation.size...party.olig.difference.scaled...gross.income..of.oligarchs.....OLIGARCH.COUNT..3')], 
names=c("0.0", "0.2", "0.4", "0.6", "0.8", "1"), xlab="Donation Size", ylab="Mean Oligarch Profit",
frame=F, col ="gray", boxwex=0.3, col.axis="#888888", col.lab="#888888", las=1, staplewex=0.4, outcex=0.8, outcol="gray")
invisible(dev.off())


pdfName <- paste(figureFolderName, "/25-AdSweep Tax Rate.pdf", sep = "")
pdf(pdfName)
boxplot(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.12)[,c('precision.voter.tax.rate.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.10)[,c('precision.voter.tax.rate.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.08)[,c('precision.voter.tax.rate.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.06)[,c('precision.voter.tax.rate.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.04)[,c('precision.voter.tax.rate.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.02)[,c('precision.voter.tax.rate.3')], 
names=c("-0.12", "-0.1", "-0.08", "-0.06", "-0.04", "-0.02"), xlab="Ad Decay Factor", ylab="Tax Rate",
frame=F, col ="gray", boxwex=0.3, col.axis="#888888", col.lab="#888888", las=1, staplewex=0.4, outcex=0.8, outcol="gray")
invisible(dev.off())


pdfName <- paste(figureFolderName, "/26-AdSweep Donation.pdf", sep = "")
pdf(pdfName)
boxplot(subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.12)[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.10)[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.08)[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.06)[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.04)[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], 
subset(adDecaySweep.sub, VOTER.AD.DECAY.FACTOR == -0.02)[,c('precision.mean..donation.size...party.olig.difference.scaled..of.oligarchs.3')], 
names=c("-0.12", "-0.1", "-0.08", "-0.06", "-0.04", "-0.02"), xlab="Ad Decay Factor", ylab="Donation Size",
frame=F, col ="gray", boxwex=0.3, col.axis="#888888", col.lab="#888888", las=1, staplewex=0.4, outcex=0.8, outcol="gray")
invisible(dev.off())
