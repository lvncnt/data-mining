
## Package required: caret pROC
require(caret)
require(pROC)
 
####-------------------------------------------------------------------- 
## Pima Indians Diabetes Data Set 
#  Predict based on diagnostic measurements whether a patient has diabetes 
# https://archive.ics.uci.edu/ml/datasets/Pima+Indians+Diabetes
# writing result to an output file
cat("Dataset: Pima Indians Diabetes Data Set\n")
sink('output.txt')
cat("=======================================\n")
cat("Dataset: Pima Indians Diabetes Data Set\n")
cat("=======================================\n")
sink()
# 1. Reading Data
url <- "https://archive.ics.uci.edu/ml/machine-learning-databases/pima-indians-diabetes/pima-indians-diabetes.data"
diabetes <- read.csv(url, header=FALSE, sep=",")
names(diabetes) <- c("Pregnancies","Glucose","BloodPressure","SkinThickness","Insulin","BMI","DiabetesPedigreeFunction","Age","Outcome")

# 2. Cleaning data to analize
diabetes$Outcome <- factor(diabetes$Outcome)
diabetes$Outcome = as.character(diabetes$Outcome)
diabetes$Outcome[diabetes$Outcome == '0'] = 'Neg'
diabetes$Outcome[diabetes$Outcome == '1'] = 'Pos'
diabetes$Outcome = factor(diabetes$Outcome, levels = c('Pos', 'Neg'))
 
# 3. Train the model
# divie data into training and test data set
trainIndex <- createDataPartition(diabetes$Outcome, p = .75, list = FALSE)
train = diabetes[ trainIndex,]
test  = diabetes[-trainIndex,]
# Get the predictors 
trainPred = train[,c(1:7)]  
testPred = test[,c(1:7)]

# Use AUC to pick the best model
# 10-fold cross validation
fit_control = trainControl(method = "cv", number = 10, summaryFunction = twoClassSummary,  classProbs = TRUE, savePredictions = TRUE)

# 3A. Create classification model with ctree
set.seed(2001)  # set the seed 
model <- train(x=trainPred, y=train$Outcome, method = 'ctree', preProcess = c('center', 'scale'), trControl = fit_control, metric = 'ROC')

### Model Predictions and Performance
# Make predictions using the test data set
pred.Outcome <- predict(model, testPred)
#Look at the confusion matrix  
table <- confusionMatrix(pred.Outcome, test$Outcome, positive = "Pos")

precision <- table$byClass['Pos Pred Value']    
recall <- table$byClass['Sensitivity']
f_measure <- 2 * ((precision * recall) / (precision + recall))

sink('output.txt', append=TRUE)
cat("classifier: ctree\n")
cat("precision =", precision*100, "%, recall =", recall*100, "%, F measure =", f_measure*100,"%\n")
sink()

#Draw the ROC curve 
test.probs <- predict(model, testPred, type='prob')
diabetes.ctree.ROC <- roc(predictor=test.probs$Pos, response=test$Outcome, levels=rev(levels(test$Outcome)))
 
# 3B. Create classification model with Naive Bayes
set.seed(2001)
model <- train(x=trainPred, y=train$Outcome, method = 'nb', preProcess = c('center', 'scale'), trControl = fit_control, metric = 'ROC',tuneGrid=data.frame(.fL=1, .usekernel=FALSE, .adjust = .1))

### Model Predictions and Performance
pred.Outcome <- predict(model, testPred)
#Look at the confusion matrix  
table <- confusionMatrix(pred.Outcome, test$Outcome, positive = "Pos")

precision <- table$byClass['Pos Pred Value']    
recall <- table$byClass['Sensitivity']
f_measure <- 2 * ((precision * recall) / (precision + recall))

sink('output.txt', append=TRUE)
cat("\nclassifier: naive bayes\n")
cat("precision =", precision*100, "%, recall =", recall*100, "%, F measure =", f_measure*100,"%\n")
sink()

#Draw the ROC curve 
test.probs <- predict(model, testPred, type='prob')
diabetes.nb.ROC <- roc(predictor=test.probs$Pos, response=test$Outcome, levels=rev(levels(test$Outcome)))
 

####-------------------------------------------------------------------- 
## Census Income Data Set
#  Predict whether income exceeds $50K/yr based on census data.
#  https://archive.ics.uci.edu/ml/datasets/Census+Income 
cat("Dataset: Census Income Data Set\n")
sink('output.txt', append=TRUE)
cat("\n\n=======================================\n")
cat("    Dataset: Census Income Data Set    \n")
cat("=======================================\n")
sink()
# 1. Reading Data
url <- "https://archive.ics.uci.edu/ml/machine-learning-databases/adult/adult.data"
adult <- read.csv(url, header=FALSE, sep=",")
names(adult) <- c("age","workclass","fnlwgt","education","education.num","marital.status","occupation","relationship","race","sex","capital.gain","capital.loss","hours.per.week","native.country","income")

# 2. Cleaning data to analize
adult$workclass <- sub(' ', '', adult$workclass)
adult$workclass <- sub('?', NA, adult$workclass, fixed = TRUE)
adult$occupation <- sub(' ', '', adult$occupation)
adult$occupation <- sub('?', NA, adult$occupation, fixed = TRUE)
adult$marital.status <- sub(' ', '', adult$marital.status)
adult$relationship <- sub(' ', '', adult$relationship)
adult$sex <- sub(' ', '', adult$sex)
adult$income <- sub(' ', '', adult$income)

adult$education = as.character(adult$education)
adult$education <- sub(' ', '', adult$education)

adult$education[adult$education %in% c('10th', '11th', '12th','1st-4th','5th-6th','7th-8th','9th','HS-grad','Preschool','Some-college')] <- 'High-School'
adult$education[adult$education %in% c('Assoc-acdm','Assoc-voc')] <- 'Associates'

adult$education <- factor(adult$education)
adult$workclass <- factor(adult$workclass) 
adult$marital.status <- factor(adult$marital.status)
adult$occupation <- factor(adult$occupation) 

adult$relationship <- factor(adult$relationship)
adult$sex <- factor(adult$sex)

adult<-as.data.frame(na.omit(adult)) # remove rows with missing values

adult$income <- factor(adult$income)
adult$income = as.character(adult$income)
adult$income[adult$income == '<=50K'] = 'Neg'
adult$income[adult$income == '>50K'] = 'Pos'
adult$income = factor(adult$income, levels = c('Pos', 'Neg'))

# 3. Train the model
# divie data into training and test data set
trainIndex <- createDataPartition(adult$income, p = .75, list = FALSE)
train = adult[ trainIndex,]
test  = adult[-trainIndex,]
# Get the predictors 
select=c(1:2,4:8,10,13)
trainPred = train[,select] 
testPred = test[,select]

# Use AUC to pick the best model
# 10-fold cross validation
fit_control = trainControl(method = "cv", number = 10, summaryFunction = twoClassSummary,  classProbs = TRUE, savePredictions = TRUE)

# 3A. Create classification model with ctree
set.seed(2001)  # set the seed 
model <- train(x=trainPred, y=train$income, method = 'ctree', preProcess = c('center', 'scale'), trControl = fit_control, metric = 'ROC') 

### Model Predictions and Performance
pred.income <- predict(model, testPred)
table <- confusionMatrix(pred.income, test$income, positive = "Pos")

precision <- table$byClass['Pos Pred Value']    
recall <- table$byClass['Sensitivity']
f_measure <- 2 * ((precision * recall) / (precision + recall))

sink('output.txt', append=TRUE)
cat("classifier: ctree\n")
cat("precision =", precision*100, "%, recall =", recall*100, "%, F measure =", f_measure*100,"%\n")
sink()

#Draw the ROC curve 
test.probs <- predict(model, testPred, type='prob')
adult.ctree.ROC <- roc(predictor=test.probs$Pos, response=test$income, levels=rev(levels(test$income)))

# 3B. Create classification model with Naive Bayes
set.seed(2001)
model <- train(x=trainPred, y=train$income, method = 'nb', preProcess = c('center', 'scale'), trControl = fit_control, metric = 'ROC',tuneGrid=data.frame(.fL=1, .usekernel=FALSE, .adjust = .1))

### Model Predictions and Performance
pred.income <- predict(model, testPred)
#Look at the confusion matrix  
table <- confusionMatrix(pred.income, test$income, positive = "Pos")

precision <- table$byClass['Pos Pred Value']    
recall <- table$byClass['Sensitivity']
f_measure <- 2 * ((precision * recall) / (precision + recall))

sink('output.txt', append=TRUE)
cat("\nclassifier: naive bayes\n")
cat("precision =", precision*100, "%, recall =", recall*100, "%, F measure =", f_measure*100,"%\n")
sink()

#Draw the ROC curve 
test.probs <- predict(model, testPred, type='prob')
adult.nb.ROC <- roc(predictor=test.probs$Pos, response=test$income, levels=rev(levels(test$income)))

####---------------------------------------------
## Output ROC curve
cat("Output ROC curve\n")
pdf("results-ROC.pdf", onefile=TRUE)
 
par(pty="s")
plot(diabetes.ctree.ROC, lty = 1, lwd = 2, main="Dataset: Pima Indians Diabetes",  col="red", print.auc = TRUE)
plot(diabetes.nb.ROC, lty = 1, lwd = 2, main="Dataset: Pima Indians Diabetes", add = TRUE, col="blue", print.auc = TRUE, print.auc.y = .4)
legend(x=1,y=1,c("ctree","naive bayes"),cex=.8, col=c('red', 'blue'),bty='n', lty=1) 

plot(adult.ctree.ROC, lty = 1, lwd = 2, main="Dataset: Census Income",  col="red", print.auc = TRUE)
plot(adult.nb.ROC, lty = 1, lwd = 2, main="Dataset: Pima Indians Diabetes", add = TRUE, col="blue", print.auc = TRUE, print.auc.y = .4)
legend(x=1,y=1,c("ctree","naive bayes"),cex=.8, col=c('red', 'blue'),bty='n', lty=1) 

dev.off()

cat("\nDatasets: Pima Indians Diabetes and Census Income\n")
cat("\nClassifier: decision tree and naive bayes\n") 
cat("\nOutputs: output.txt (Accuracy metrics); results-ROC.pdf (ROC curve)\n")
##----------------------------------------------
