# Decision Tree
## ID3 algorithm in JAVA

## About
### This is an implementation of the [_ID3 algorithm_](https://en.wikipedia.org/wiki/ID3_algorithm) in java
* Uses `entropy loss` to split nodes
* Handles data as `descrete values`

#### Reads data from `.csv` file
* First line must be the header
* Last column is `class`

## Usage
### Options:
* Build decsion tree from training data and print decision tree
* Build decsion tree from training data and predict values from test data file

#### The test data file must be similar to the training data file, but without `class` variable (_see examples_)
1. Compile `.java` files
2. Run compiled _DecisionTree_ 

### Options
* Format: running option: `1` to print decision tree, otherwise predict values from test file (_path to test file next line_)
* Input file: the path of the training data, a `.csv`file
* Test file: if **_Format_** != `1`, the path to the test `.csv` file

**_Examples_**:
* Print learned desicion tree (_format is 1_)
    ```
    java DecisionTree < ./examples/print_tree_example.txt
    ```
* Train and predict (_format is not 1_)
    ```
    java DecisionTree < ./examples/prediction_example.txt
    ```
