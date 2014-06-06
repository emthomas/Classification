package com.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class ClassifierMapper extends Mapper<LongWritable,Text,Text,Text> {
	
	@Override
	public void map(LongWritable key, Text value,  Context context) throws IOException, InterruptedException {
		context.write(new Text(key.toString()), value);
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		
	}

}
