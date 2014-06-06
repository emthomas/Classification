package com.model;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Standardize;

public class Classification 
{
	private static NaiveBayes model;
	
    public static void main( String[] args ) throws Exception
    {
    	String sampleFile = "data/guests.arff";
//		Instances dataTrain = BuildSampleDataset();
//    	Instances dataTest = BuildSampleDataset();
    	Instances bookersTrain = LoadDataset("data/headers.tab","data/bookers.sample.train",true);
    	Instances nonBookerTrain = LoadDataset("data/headers.tab","data/nonbookers.sample.train",false);
    	Instances dataTrain = Instances.mergeInstances(bookersTrain, nonBookerTrain);
    	
    	Instances bookersTest = LoadDataset("data/headers.tab","data/bookers.sample.test",true);
    	Instances nonBookerTest = LoadDataset("data/headers.tab","data/nonbookers.sample.test",false);
    	Instances dataTest = Instances.mergeInstances(bookersTest, nonBookerTest);
    	setModel();
		train(model, dataTrain);
    	//System.out.println(model);
    	evaluate(dataTrain,dataTest);
    	
    	evaluateAttributes(dataTrain);
    	
		//System.out.println(dataTrain.toSummaryString());
		//System.out.println(dataTrain);
		//test(model, dataTest);
    	
    }
    
    public static void evaluateAttributes(Instances dataTrain) throws Exception {
    	AttributeSelection attSelection = new AttributeSelection();
    	attSelection.SelectAttributes(dataTrain);
    	InfoGainAttributeEval infoGain = new InfoGainAttributeEval();
    	infoGain.buildEvaluator(dataTrain);
    	Enumeration<Attribute> list = dataTrain.enumerateAttributes();
    	while(list.hasMoreElements()) {
    		Attribute next = list.nextElement();
    		if(infoGain.evaluateAttribute(next.index())>0) {
    		System.out.println(next.name()+":"+infoGain.evaluateAttribute(next.index()));
    		}
    	}
    }
    
    public static void setModel() throws Exception {
    	model = new NaiveBayes();
    	String[] options = {"-K"};
		model.setOptions(options);
    }
    
    public static void train(Classifier model, Instances dataset) throws Exception {
    	model.buildClassifier(dataset);
    }
    
    public static void test(Classifier model, Instances dataset) throws Exception {
    	Instance test = null;
		double testClass = 0;
		double predictionClass = 0;
		int count = 0;
		int total = dataset.numInstances();
		double tolerance = 0.05;
		double percentDiff = 0;
		
		for(int i=0; i<total; i++) {
			test = dataset.instance(i);
			testClass = test.classValue()+1;
			predictionClass = model.classifyInstance(test)+1;
			percentDiff = Math.abs(((testClass/predictionClass)-1));
			if(percentDiff<=tolerance) {
				count++;
			}
		}
		
		double accuracy = Math.round(100.0*count/total);
		System.out.println("Accuracy: "+accuracy+"%");
    }
    
    public static void evaluate(Instances dataTrain, Instances dataTest) throws Exception {
    	Evaluation evaluation = new Evaluation(dataTrain);
		evaluation.evaluateModel(model, dataTest);
		System.out.println(evaluation.toSummaryString(true));
		System.out.println(evaluation.toClassDetailsString("=== Class ==="));
		System.out.println(evaluation.toMatrixString("=== Matrix ==="));
		//System.out.println(evaluation.toCumulativeMarginDistributionString());
    }
    
    public static Instances BuildSampleDataset() throws Exception {
    	int numOfAttribute = 300;
    	int numOfInstances = 1000;
    	Instances data = null;
    	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    	
    	for(int i=0; i<numOfAttribute; i++) {
    		attributes.add(new Attribute("att_"+(i+1)));
    		if(i==numOfAttribute-1) {
    			attributes.add(new Attribute("class",Arrays.asList("+1","-1")));
    		}
    	}
    	
	 	data = new Instances("train_dataset", attributes, 0);
	 	data.setClassIndex(data.numAttributes() - 1);
    
	 for(int j=0; j<numOfInstances; j++)
	 {
	 	Instance inst = new DenseInstance(data.numAttributes());
	 	inst.setDataset(data);
	 	String booker = Math.rint(Math.random()*2)==1?"+1":"-1";
	 	inst.setClassValue(booker);
	 	for(int i=0; i<numOfAttribute; i++) {
    		if(booker.equals("+1")) {
    			//inst.setValue(i, (i<10)?Math.random()+10:Math.random());
    			inst.setValue(i, (i%24==0)?(int)(Math.random()*10)+10:(int)(Math.random()*6)+10);
        			}
        	else {
            	//inst.setValue(i, Math.random());	
    			inst.setValue(i, (i%18==0)?(int)(Math.random()*9):(int)(Math.random()*4));
        		}
    	}
	 	data.add(inst);
    }
	 	StandardizeFeatures(data);
    	return data;
    }
    
    public static Instances LoadDataset(String headerFile, String dataFile, boolean booker) throws Exception {
    	
    	int numOfInstances = 1000;
    	BufferedReader br = new BufferedReader(new FileReader(headerFile));
    	String[] headers = br.readLine().split("\t");
    	br.close();

    	int numOfAttribute = headers.length;
    	System.out.println("num att: "+numOfAttribute);
    	Instances data = null;
    	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    	for(int i=0; i<numOfAttribute; i++) {
    		attributes.add(new Attribute(headers[i]));
    	}
    	//add class attribute
    	attributes.add(new Attribute("class",Arrays.asList("+1","-1")));
	 	data = new Instances(dataFile, attributes, 0);
	 	data.setClassIndex(data.numAttributes() - 1);
	 	
	 	//Load data
    	br = new BufferedReader(new FileReader(dataFile));
    	
    	String strLine = null;
    	while ((strLine = br.readLine()) != null) 
    	{
	 	Instance inst = new DenseInstance(data.numAttributes());
	 	inst.setDataset(data);
	 	inst.setClassValue(booker?"+1":"-1");
	 	String[] att = strLine.split("\t");
	 	//System.out.println(strLine);
	 	//System.out.println("line: "+att.length);
	 	for(int i=0; i<numOfAttribute; i++) {
	 		try{
    			inst.setValue(i,att[i].hashCode());	
	 		}
	 		catch (Exception e) {
	 			
	 		}
    			//System.out.print(headers[i]+":"+att[i]+"\t");
    	}
	 	//System.out.println();
	 	data.add(inst);
    }
	 	StandardizeFeatures(data);
    	return data;
    }
    
    public static Instances BuildSampleDataset(String fileName) throws Exception {
    	PrintStream ps = new PrintStream(new FileOutputStream(fileName));
    	String header = "@RELATION guest\n"+
    					"@ATTRIBUTE timeOnSite NUMERIC\n"+
    					"@ATTRIBUTE specialOffers NUMERIC\n"+
    					"@ATTRIBUTE contentResort NUMERIC\n"+
    					"@ATTRIBUTE contentTicket NUMERIC\n"+
    					"@ATTRIBUTE income NUMERIC\n"+
    					"@ATTRIBUTE children NUMERIC\n"+
    					"@ATTRIBUTE class {-1,+1}\n"+
    					"@DATA";
    	ps.println(header);
    	double[] features = new double[6];
    	String booker = "";
    	for(int i=0; i<1000; i++) {
    		for(int j=0; j<features.length; j++) {
    			
    			if(Math.rint(Math.random())==1) {
    				features[j]=Math.random();
    			}
    			else {
    				features[j]=Math.random();	
    			}
    		}
    		booker = Math.rint(Math.random()*5)==1?"+1":"-1";
    		for(int j=0; j<features.length; j++) {
    			if(booker.equals("+1")) {
    			ps.print((features[j]+10)+",");
    			}
    			else {
        			ps.print(features[j]+",");	
    			}
    		}
    	ps.println(booker);
    	}
    	ps.close();
    	
    	Instances data = new Instances(new BufferedReader(new FileReader(fileName)));
		data.setClassIndex(data.numAttributes() - 1);
		StandardizeFeatures(data);
		
		return data;
    }
    
    public static void StandardizeFeatures(Instances features) throws Exception {
		Standardize filter = new Standardize();
		filter.setInputFormat(features);
		Instances new_features = Filter.useFilter(features, filter);
		features = new_features;
	}
}
