package com.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class ClassifierReducer extends Reducer<Text,Text,Text,Text> {
	
	@Override
	public void reduce(Text key, Iterable<Text> values,  Context context) throws IOException {
		
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		
	}

}
