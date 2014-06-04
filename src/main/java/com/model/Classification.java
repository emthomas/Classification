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
		//build model
		//LinearRegression model = new LinearRegression();
		NaiveBayes model = new NaiveBayes();
		model.buildClassifier(data); //the last instance with missing class is not used
		System.out.println(model);
		Instance test = null;
		double testClass = 0;
		double predictionClass = 0;
		int count = 0;
		int total = data.numInstances();
		
		for(int i=0; i<total; i++) {
			test = data.instance(i);
			testClass = test.classValue();
			predictionClass = model.classifyInstance(test);
			if(testClass==predictionClass) {
				count++;
			}
		}
		
		double accuracy = Math.round(100.0*count/total);
		System.out.println("Accuracy: "+accuracy+"%");
		
    }
    
    public static void BuildSampleDataset (String fileName) throws Exception {
    	PrintStream ps = new PrintStream(new FileOutputStream(fileName));
    	String header = "@RELATION house\n"+
    					"@ATTRIBUTE size NUMERIC\n"+
    					"@ATTRIBUTE land NUMERIC\n"+
    					"@ATTRIBUTE rooms NUMERIC\n"+
    					"@ATTRIBUTE granite NUMERIC\n"+
    					"@ATTRIBUTE extra_bathroom NUMERIC\n"+
    					"@ATTRIBUTE extra_bathroom_2 NUMERIC\n"+
    					"@ATTRIBUTE class {-1,+1}\n"+
    					"@DATA";
    	ps.println(header);
    	double[] features = new double[6];
    	String booker = "";
    	for(int i=0; i<1000; i++) {
    		for(int j=0; j<features.length; j++) {
    			features[j]=Math.random();
    		}
    		booker = Math.rint(Math.random())==1?"+1":"-1";
    		for(int j=0; j<features.length; j++) {
    		//	System.out.print(features[j]+",");
    			ps.print(features[j]+",");
    		}	
    	//System.out.println(booker);
    	ps.println(booker);
    	}
    }
}
