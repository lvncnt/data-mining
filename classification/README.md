# Classification by decision tree and naive Bayesian

### Run the R code 
```sh
$ source('source.R')
```

### Data sets 

**Table 1. Summary of the two datasets used in clustering analysis**

|               | Name           | Task  | Source |
| ------------- |:-------------:|:-----:|:-----:|
| **Dataset 1**     | Pima Indians Diabetes Data Set | Predict whether a patient has diabetes | [Pima+Indians+Diabetes][Diabetes] |
| **Dataset 2**     | Census Income Data Set      |   Predict whether income exceeds $50K/yr based on census data |[Census+Income][Income] |


### Classifier
The caret package in R was used for splitting the data into training and test sets, for model training, for 10-fold cross validation, and for evaluating the performance of classification models (including generating the confusion matrix and computing ROC curves). A description of the models used in the caret package is shown in Table 2. 

**Table 2. Clustering models used in training**


|     Model    | method value   | Package  | Algorithm Description | Reference | 
| ------------- |:-------------:|:-----:|:-----:|:-----:|
|Recursive partitioning| ctree|	party|	Implementation of Conditional inference trees (CTree). It uses a significance test procedure to select variables instead of selecting the variable that maximizes an information measure.|Hothorn et al. 2006|
|Naive Bayes|	nb|	klaR|	Computes the conditional a-posterior probabilities of a categorical class variable given independent predictor variables using the Bayes rule.|	Weihs et al. 2005|

### Results and analysis
On Data Set 1, first, both models were evaluated in terms of confusion matrix. ctree gives an Accuracy of 69.79% and an error rate of 30.2%. By comparison, naive Bayes gives a higher accuracy (77.6%) and a lower error rate (22.39%). Similarly, the precision for ctree classifier is 66.67%, its recall is 53.73% and F measure is 59.50%. The precision for naive Bayes is 70.69%, its recall is 61.19% and F measure is 65.6%. Those metrics suggested better performance for naive Bayes for Data Set 1. 

On the other hand, when evaluated by ROC curve, ctree model has a slight early advantage. But once the false positive rate gets above around 0.1, naive Bayes classifier (AUC=0.8487) takes over as the dominant model (Fig. 1). Overall, naive Bayes has better performance. 

For Data Set 2, naive Bayes(AUC=0.8833) and ctree classifier (AUC=0.8835) give quite similar performance (Fig. 2). The confusion matrix also shows similar result, they have similar accuracy (82.82% for ctree and 81.72% for naive Bayes) as well as error rate (17.17% for ctree and 18.28% for naive Bayes). The precision for ctree classifier is 66.69%, its recall is 55.60% and F measure is 61.93%. The precision for naive Bayes is 59.99%, its recall is 73.33% and F measure is 65.99%. The difference in classifier’s prediction between these two data sets was probably due to the attribute type of the attributes, and the number of discrete and continuous attributes, whereas the algorithms used here are designed to work primarily with categorical attributes.   

   ![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/classification/Fig1-ROC.jpg)

![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/classification/Fig2-ROC.jpg)
 
   [Diabetes]: <https://archive.ics.uci.edu/ml/datasets/Pima+Indians+Diabetes>
   [Income]: <https://archive.ics.uci.edu/ml/datasets/Census+Income>


 
    
