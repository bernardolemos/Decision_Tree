import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

// ID3 algorithm

public class DecisionTree extends Node{
	
    static int classIndex,nLines;
    //Tree representation
    static HashMap<String,Node> tree = new HashMap<String,Node>(); 
    static LinkedList<String> globalAttributes = new LinkedList<String>();

		/*************************************************************************************/
		/********************************* MAIN *********************************************/
    public static void main(String args[]) throws IOException {
			//nevermind the format prints
			Scanner in = new Scanner(System.in);
			System.out.print("Format: ");
			int format = in.nextInt();
			System.out.print("File: ");
			String file = in.next();
			System.out.println("............................................");
			//Read file
			LinkedList<String> lineList = readFile(file);
			//Store attributes (global variable)
			getAttributes(lineList);
			classIndex = globalAttributes.size() - 1;
			//Value table
			String[][] values = getAllValues(lineList);
			//examples
			LinkedList<Integer> examples = new LinkedList<Integer>();
			for(int i=0; i<nLines; i++)
				examples.addLast(i);
			//Attribute List
			LinkedList<String> attributes = new LinkedList<String>(globalAttributes);
			//Removes ID
			attributes.removeFirst();
			//Removes CLASS
			attributes.removeLast();
			Node root = new Node();
			root = id3(attributes, values, examples, null, 0, null, 0);
			if(format==1)
				printTree(root);
			else{
				System.out.print("Examples: ");
				String otherFile = in.next();
				//Read file
				lineList = new LinkedList<String>();
				lineList = readFile(otherFile);
				lineList.removeFirst(); // skip header
				String[][] exampleValues = getAllValues(lineList);
				//printin(exampleValues);
				printClasses(exampleValues, root);
			}
			in.close();
			System.out.println("............................................");
    }
		/***************************** End of main *******************************************/
		/*************************************************************************************/
    
    
    //ID3 algorithm
    public static Node id3(LinkedList<String> attributes, String[][] values, LinkedList<Integer> examples, String father, int level, String defaultV, int deafultC){
		//no more examples
		if(examples.isEmpty()){
			//adds Majority value from previous class- 'defaultV'
			return new Node(level-1, father, defaultV, deafultC, true);
		}
		//all elems. belong to same class
		else if(sameClass(examples,values)){
			//add new pure node
			String classValue = values[examples.getFirst()][classIndex];
			int counter = classCount(examples,values,classValue);
			return new Node(level-1, father, classValue, counter, true);
		}
		//no more attributes
		else if(attributes.isEmpty()){
			//adds a new node - Majority
			String classValue = getMajorityValue(examples, values);
			int counter=classCount(examples, values, classValue);
			return new Node(level-1, father, classValue, counter, true);
		}
		//general case - recursive
		else{
	   	  String bestAttribute = getMinEntropyAttribute(attributes, values, examples);
	    	int attributeIndex = globalAttributes.indexOf(bestAttribute);
	    	//current node
	    	LinkedList<Node> childs = new LinkedList<Node>();
	   		Node node = new Node(level, bestAttribute, childs, father);
	    	attributes.remove(bestAttribute);

				//values from examples' attribute
	    	LinkedList<String> attributeValues = getValuesFrom(values, examples, attributeIndex);
	   		//Child nodes
	   		for(String currentValue: attributeValues){
	   			//gets examples from 'currentValue'
	   			LinkedList<Integer> valuesExamples = getExamplesFrom(values, currentValue, examples, attributeIndex);
	   			//major value
	   			String mv = getMajorityValue(examples, values);
	   			//major count
	   			int mc=classCount(examples, values, mv);
	   			childs.add(id3(attributes, values, valuesExamples, currentValue, level+1, mv, mc));
    		}
	    	node.setChilds(childs);
	     	return node;
		}
    }


    //count class occurence
    public static int classCount(LinkedList<Integer> examples, String[][] values, String wantedClass){
    	int count=0;
    	for(int i=0; i<nLines; i++)
    		if(examples.contains(i) && values[i][classIndex].equals(wantedClass))
    			count++;
    	return count;
    }


    //retorns major class
    public static String getMajorityValue(LinkedList<Integer> examples, String[][] values){
    	int max = Integer.MIN_VALUE;
    	String majorityClass = null;
    	//numb. of time that a class occurs
		HashMap<String, Integer> classCount = new HashMap<String, Integer>();
		for(int i=0; i<nLines; i++){
	    	//init. count
	    	if(examples.contains(i)){
	    		if(!classCount.containsKey(values[i][classIndex]))
					classCount.put(values[i][classIndex], 1);
	    		//update count
	    		else if(classCount.containsKey(values[i][classIndex])){
					int aux = classCount.get(values[i][classIndex]);
					classCount.put(values[i][classIndex], aux+1);
	    		}
			}
		}
		//find major value
		for(int i=0; i<nLines; i++)
			if(examples.contains(i) && classCount.get(values[i][classIndex])>max){
				max = classCount.get(values[i][classIndex]);
				majorityClass = values[i][classIndex];
			}
		return majorityClass;
    }

        
    //check values- class
    public static boolean sameClass(LinkedList<Integer> examples, String[][] values){
    	String prevClass = values[examples.getFirst()][classIndex];
    	for(int i=0; i<nLines; i++){
    		if(examples.contains(i) && !prevClass.equals(values[i][classIndex]))
    			return false;
    	}
    	return true;
    }
        
    
    //returns the examples corresponding to a value - starting from a set of examples
    public static LinkedList<Integer> getExamplesFrom(String[][] values, String wantedValue, LinkedList<Integer> examples, 
    	int attributeIndex){
    	LinkedList<Integer> valuesExamples = new LinkedList<Integer>();
    	for(int i=0; i<nLines; i++)
    		if(examples.contains(i) && values[i][attributeIndex].equals(wantedValue))
    			valuesExamples.add(i);
    	return valuesExamples;
    }
    


    //returns the values of an attribute corresponding to the examples
    public static LinkedList<String> getValuesFrom(String[][] values, LinkedList<Integer> examples,int attributeIndex){
    	LinkedList<String> attributeValues = new LinkedList<String>();
    	for(int i=0;i<nLines;i++)
    		if(examples.contains(i) && !attributeValues.contains(values[i][attributeIndex]))
    			attributeValues.add(values[i][attributeIndex]);
    	return attributeValues;
    }
    
    
    //Reads file 'file' - seperated by lines
    public static LinkedList<String> readFile(String file) {
		LinkedList<String> lineList = new LinkedList<String>();
		BufferedReader buffer = null;
		try {
	    	String line;
	    	buffer = new BufferedReader(new FileReader(file));
	    	while ((line = (buffer.readLine())) != null)
			lineList.addLast(line);
		} catch (IOException e) {
	    	e.printStackTrace();
		} finally {
	    	try {
			if (buffer != null)
		    	buffer.close();
	    	} catch (IOException e) {
			e.printStackTrace();
	    	}
		}
		return lineList;
    }
    
    
    //returns an array with all attributes (can only be called once)
    /* define attributes*/
    public static void getAttributes(LinkedList<String> lineList) {
		String line=lineList.removeFirst();	//Removes 1st line in list - contains attributes
		String[] split=line.split(",");	//parsing 
		for(String attribute: split)
			globalAttributes.addLast(attribute);
	}
    
    
    /*define values
     * 'getAttributes'  must be called before this method
     */
    public static String[][] getAllValues(LinkedList<String> lineList){
		//the first row is created out of the loop only to set the size of the table of values
		int i=0,j=0;
		String line=lineList.removeFirst();
		String[] split=line.split(",");
		String[][] values=new String[lineList.size()+1][split.length];
		nLines=lineList.size()+1;
		split=line.split(",");
		for(String value:split){
	    	values[i][j]=value;
	    	j++;
		}
		while(!lineList.isEmpty()){
	    	i++;
	    	j=0;
	    	line=lineList.removeFirst();
	    	split=line.split(",");
	    	for(String value:split){
				values[i][j]=value;
				j++;
		    }
		}
	return values;
    }
     
    
    //return entroypy of 'wantedValue'
    public static double getEntropy(String[][] values, String wantedValue, int attributeIndex, LinkedList<Integer> examples){
		//count class values
		HashMap<String, Integer> classValues=new HashMap<String,Integer>();
		//count 'wantedValue' 
		int totalWantedValues=0;
		//adds all possible values of the class associated with 'wantedValue' and 'examples'
		for(int i=0;i<nLines;i++){
	 	    if(examples.contains(i)){
	    		//start count
	    		if(!classValues.containsKey(values[i][classIndex]) && values[i][attributeIndex].equals(wantedValue)){
					classValues.put(values[i][classIndex], 1);
					totalWantedValues++;
	    		}
	    		//update
	    		else if(classValues.containsKey(values[i][classIndex]) && values[i][attributeIndex].equals(wantedValue)){
					int aux=classValues.get(values[i][classIndex]);
					classValues.put(values[i][classIndex], aux+1);
					totalWantedValues++;	
	    		}
			}
		}
		//store the values of the classes whose probability has already been calculated
		LinkedList<String> checkedClassValues=new LinkedList<String>();
		double entropy=0;
		//cálculo da entropia
		for(int i=0;i<nLines;i++)
			if(examples.contains(i))
		    	if(values[i][attributeIndex].equals(wantedValue) && !checkedClassValues.contains(values[i][classIndex])){
					checkedClassValues.add(values[i][classIndex]);
					//compute probability
					double p=(double) classValues.get(values[i][classIndex])/totalWantedValues;
					//compute entropy
					entropy-=(double) p*(Math.log(p)/Math.log(2));	
		    	}
		return entropy;		
    }
    
    
    //returns the attribute with the least entropy
    public static String getMinEntropyAttribute(LinkedList<String> attributes, String[][] values, LinkedList<Integer> examples){
		String bestAttribute=new String();
		double min=(double) Integer.MAX_VALUE;
		for(String attribute: attributes){
	    	int attributeIndex=globalAttributes.indexOf(attribute);
	    	LinkedList<String> checkedValues=new LinkedList<String>(); //list of computed values
	    	double entropy=(double) 0;
	    	for(int i=0;i<nLines;i++){
				//get least entropy
					if(examples.contains(i) && !checkedValues.contains(values[i][attributeIndex])){
						checkedValues.add(values[i][attributeIndex]);
						double p=getProbability(values[i][attributeIndex],attributeIndex,examples,values);
						entropy+=(double) p*getEntropy(values, values[i][attributeIndex], attributeIndex, examples);
					}
			}			
			if(min>entropy){
				min=entropy;
				bestAttribute=attribute;
			}
		}
		return bestAttribute;
    }

    
  	//computes the probability of a value according to a set of examples
  	public static double getProbability(String wantedValue, int attributeIndex, LinkedList<Integer> examples,
  		String[][] values){
  		int c=0;
  		for(int i=0;i<nLines;i++)
  			if(values[i][attributeIndex].equals(wantedValue) && examples.contains(i))
  				c++;
  		return (double) c/examples.size();
  	}


  	//print complete tree
  	public static void printTree(Node node){
  		//System.out.println();
  		//lista de nós filhos
  		LinkedList<Node> childs=node.getChilds();
  		//attribute node
  		if(!node.isClass()){
  			//<value>
  			//sapcing
  			if (node.getValue() != null){
  				for(int i=0;i<node.getLevel();i++)
  					System.out.print("      ");
   					System.out.println(node.getValue()+": ");}
  			//<attribute>
   			//spacing
   			if(node.getValue()!=null)
   				for(int i=0;i<node.getLevel()+1;i++)
  					System.out.print("     ");
  			System.out.println(node.getAttribute());
      		//print attribute's children
  	  		while(!childs.isEmpty())
  	  			printTree(childs.remove());
  		}
  		//pure node
  		else{
  			//spacing
  			for(int i=0;i<node.getLevel()+1;i++)
  				System.out.print("      ");
   			//<value>: <class> (count)
  			System.out.println(node.getAttribute()+": "+node.getValue()+" ("+node.getCount()+")");
   		}
  	}


  	//prints the class of all values
  	public static void printClasses(String[][] values, Node root){
  		for(int i=0;i<nLines;i++){
  			Node node=new Node(root);
  			String[] examplesLine=new String[globalAttributes.size()-2];
  			for(int j=1;j<globalAttributes.size()-1;j++)
  				examplesLine[j-1]=values[i][j];
  			getClass(examplesLine,node);
  		}
  	}


  	//print the class of an example
  	public static void getClass(String[] values, Node node){
  		//pure node
  		if(node.isClass()){
   			System.out.println(node.getValue());
  		}
  		else{
  			LinkedList<Node> childs=node.getChilds();
  			int attributeIndex=globalAttributes.indexOf(node.getAttribute())-1;
  			String attribute=node.getAttribute();
  			String val=node.getValue();
  			while(!childs.isEmpty()){
  				Node child=new Node(childs.remove());
   				if(!child.isClass() && values[attributeIndex].equals(child.getValue()))
  					getClass(values,child);
  				else if(child.isClass() && values[attributeIndex].equals(child.getAttribute()))
  					getClass(values,child);
  			}
  		}
  	}




  	public static void printin(String[][] values){
  		for(String s: globalAttributes)
  			System.out.printf("%-7s",s);
  		System.out.println();
  		for(int i=0;i<nLines;i++){
  			for(int j=0;j<globalAttributes.size();j++)
  				System.out.printf("%-7s",values[i][j]);
  			System.out.println();
  		}
  	}
}

