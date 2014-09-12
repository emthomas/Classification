package com.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;

public class ClassifierOutputFileFormat 
	extends MultipleTextOutputFormat<Text, Text> {
		 
	    @Override
	    protected String generateFileNameForKeyValue(Text key, Text value, String name) {
	    	return (key.toString()+"part.txt");
	    }

	    @Override
	    protected Text generateActualKey(Text key, Text Value) {
	    	return null;
	    }
}
