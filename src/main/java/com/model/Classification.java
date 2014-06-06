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
		Instances dataTrain = BuildSampleDataset();
    	Instances dataTest = BuildSampleDataset();
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
    	System.out.println(next.name()+":"+infoGain.evaluateAttribute(next.index()));
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
		System.out.println(evaluation.toCumulativeMarginDistributionString());
    }
    
    public static Instances BuildSampleDataset() throws Exception {
    	int numOfAttribute = 30;
    	int numOfInstances = 100;
    	Instances data = null;
    	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    	
    	for(int i=0; i<numOfAttribute; i++) {
    		attributes.add(new Attribute("att_"+i));
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
    			inst.setValue(i, (i<10)?Math.random()+10:Math.random());
        			}
        	else {
            	inst.setValue(i, Math.random());	
        		}
    	}
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
