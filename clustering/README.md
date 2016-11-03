# Classification by decision tree and naive Bayesian

### Run the R code 
```sh
$ source('source.R')
```

### Data sets 

**Table 1. Summary of the two datasets used in clustering analysis**

|               | Name           | Task  | Source |
| ------------- |:-------------:|:-----:|:-----:|
| **Dataset 1**     | Seeds Data Set (210 instances) | Perform cluster analysis of three varieties of wheat seed using geometric parameters of wheat kernels  | [Seeds][Seeds] |
| **Dataset 2**     |  Wholesale customers Data Set (440 instances)      |   Perform cluster analysis of customers based on their annual spending in diverse product categories |[Wholesale+customers][Wholesale] |


### Clustering algorithms 
Two R functions, kmeans and agnes, which computes k-means clustering and agglomerative hierarchical clustering of the dataset, respectively, are in performing the clustering analysis. A description of these two algorithms is shown in Table 2. 

**Table 2. Clustering algorithms used in R**


|     Algorithm    | Arguments   | Package  | Algorithm Description | Reference | 
| ------------- |:-------------:|:-----:|:-----:|:-----:|
|K-means clustering |	kmeans(x, k, algorithm =‘Hartigan-Wong’)|	stats|	The k-means method partitions the points into k groups such that the sum of squares from points to the assigned cluster centers is minimized. |	Hartigan and Wong (1979) |
|Agglomerative nesting|	agnes(x, metric = "euclidean", method = "ward")	|cluster|	The agnes-algorithm constructs a hierarchy of clusterings. Initially, each observation is considered as a single-element cluster. At each iteration, two nearest clusters are merged, until only one cluster remains.|	Kaufman and Rousseeu (1990)|

### Results and analysis
The K-means results are shown in Fig 2-3,6 and dendrogem from agnes shown in Fig 4,7-8. Here, I will focus on analyzing Data Set 1 in comparing the two algorithms used. First, in agree with the three varieties for seed indicated in the dataset (variety 1, 2, and 3), the number of clusters is also estimated to be 3 (k = 3) by applying the gap statistic (Fig 1). As the dataset provides the true label for each instance, the confusion matrix as well as the accuracy for each predicted cluster was then computed for both algorithm clustering results, as summarized in Table 3. As shown in the table, K-means and agnes algorithm shows similar performance in terms of accuracy (classified into the correct cluster). Also, the accuracy for Cluster 1 is lower than that for the other two, suggesting that both algorithms are less successful in identifying seed variety 1 (Fig 3).  

   ![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/clustering/Table3.png)

![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/clustering/Fig_1.jpg)

![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/clustering/Fig_2.jpg)

![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/clustering/Fig_3.jpg)

![alt tag](https://raw.githubusercontent.com/lvncnt/data-mining/master/clustering/FIg_4.jpg)

 
   [Seeds]: <https://archive.ics.uci.edu/ml/datasets/seeds#>
   [Wholesale]: <https://archive.ics.uci.edu/ml/datasets/Wholesale+customers>
   
   



 
    
