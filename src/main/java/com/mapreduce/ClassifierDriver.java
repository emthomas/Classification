package com.mapreduce;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/*This class is responsible for running map reduce job*/
public class ClassifierDriver extends Configured implements Tool {

public int run(String[] args) throws Exception
 {
	
 if(args.length !=2) {
 System.err.println("Usage: ClassifierDriver <input path> <outputpath>");
 System.exit(-1);
 }

 Job job = new Job();
 job.setJarByClass(com.mapreduce.ClassifierDriver.class);
 job.setJobName("Classifier");

 File dir = new File(args[1]);
 if(dir.exists()) {
	 try{
		 
         delete(dir);

     }catch(IOException e){
         e.printStackTrace();
         System.exit(0);
     }
 }
 
 FileInputFormat.addInputPath(job, new Path(args[0]));
 FileOutputFormat.setOutputPath(job,new Path(args[1]));

 job.setMapperClass(com.mapreduce.ClassifierMapper.class);
 //job.setCombinerClass(com.mapreduce.ClassifierCombiner.class);
 //job.setReducerClass(com.mapreduce.ClassifierReducer.class);

 job.setOutputKeyClass(Text.class);
 job.setOutputValueClass(Text.class);
 
 System.exit(job.waitForCompletion(true) ? 0:1); 
 boolean success = job.waitForCompletion(true);
 return success ? 0 : 1;
 }

public static void main(String[] args) throws Exception {
	ClassifierDriver driver = new ClassifierDriver();
	String[] dirs = {"data/headers.tab","output"};
	int exitCode = ToolRunner.run(driver, dirs);
	System.exit(exitCode);
 }

public static void delete(File file)
    	throws IOException{
 
    	if(file.isDirectory()){
 
    		if(file.list().length==0){
 
    		   file.delete();
 
    		}else{
        	   String files[] = file.list();
 
        	   for (String temp : files) {
        	      File fileDelete = new File(file, temp);
 
        	     delete(fileDelete);
        	   }
 
        	   if(file.list().length==0){
           	     file.delete();
        	   }
    		}
 
    	} else{
    		file.delete();
    	}
    }
}
