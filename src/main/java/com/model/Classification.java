package com.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Standardize;

/**
 * Hello world!
 *
 */
public class Classification 
{
    public static void main( String[] args ) throws Exception
    {
    	String sampleFile = "data/house.arff";
        BuildSampleDataset(sampleFile);
		Instances data = new Instances(new BufferedReader(new FileReader(sampleFile)));
		data.setClassIndex(data.numAttributes() - 1);
		StandardizeFeatures(data);
		//build model
		//LinearRegression model = new LinearRegression();
		NaiveBayes model = new NaiveBayes();
		String[] options = {"-K"};
		model.setOptions(options);
		model.buildClassifier(data); //the last instance with missing class is not used
		System.out.println(model);
		Instance test = null;
		double testClass = 0;
		double predictionClass = 0;
		int count = 0;
		int total = data.numInstances();
		double tolerance = 0.05;
		double percentDiff = 0;
		
//		test=data.lastInstance();
//		System.out.println(test);
//		
//		for(double distribution : model.distributionForInstance(test)) {
//			System.out.println(distribution);
//		}
		
		for(int i=0; i<total; i++) {
			test = data.instance(i);
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
    
    public static void BuildSampleDataset (String fileName) throws Exception {
    	PrintStream ps = new PrintStream(new FileOutputStream(fileName));
    	String header = "@RELATION guest\n"+
    					"@ATTRIBUTE timeOnSite NUMERIC\n"+
    					"@ATTRIBUTE specialOffers NUMERIC\n"+
    					"@ATTRIBUTE contentResort NUMERIC\n"+
    					"@ATTRIBUTE contentTicket NUMERIC\n"+
    					"@ATTRIBUTE income NUMERIC\n"+
    					"@ATTRIBUTE children NUMERIC\n"+
    					"@ATTRIBUTE class {-1,+1}\n"+
    					//"@ATTRIBUTE class NUMERIC\n"+
    					"@DATA";
    	ps.println(header);
    	double[] features = new double[6];
    	String booker = "";
    	for(int i=0; i<10; i++) {
    		for(int j=0; j<features.length; j++) {
    			
    			if(Math.rint(Math.random())==1) {
    				features[j]=Math.random();
    			}
    			else {
    				features[j]=Math.random();	
    			}
    		}
    		booker = Math.rint(Math.random())==1?"+1":"-1";
    		for(int j=0; j<features.length; j++) {
    			if(booker.equals("+1")) {
    		//	System.out.print(features[j]+",");
    			ps.print((features[j]+10)+",");
    			}
    			else {
    			//	System.out.print(features[j]+",");
        			ps.print(features[j]+",");	
    			}
    		}	
    	//System.out.println(booker);
    	ps.println(booker);
    	}
    	ps.close();
    }
    
    public static void StandardizeFeatures(Instances features) throws Exception {
		Standardize filter = new Standardize();
		filter.setInputFormat(features);
		Instances new_features = Filter.useFilter(features, filter);
		System.out.println("BEFORE\n"+features);
		features = new_features;
		System.out.println("AFTER\n"+features);
	}
}
