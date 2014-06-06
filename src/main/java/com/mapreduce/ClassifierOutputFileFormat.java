package com.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;

public class ClassifierOutputFileFormat 
	extends MultipleTextOutputFormat<Text, Text> {
		 
	    @Override
	    protected String generateFileNameForKeyValue(Text key, Text value, String name) {
	    	if(key.toString().split("_").length==2) {
	    		String booker = key.toString().split("_")[1].equals("true")?"bookers":"non-bookers";
	    		return booker+"/"+key.toString().split("_")[0]+".json";
	    	}
	    	return (key.hashCode()%10)+"part.txt";
	    }

	    @Override
	    protected Text generateActualKey(Text key, Text Value) {
	    	return null;
	    }
}
