package com.example.controller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.example.DocumentSimilarityMapper;
import com.example.DocumentSimilarityReducer;

public class DocumentSimilarityDriver {
    
    public static void main(String[] args) throws Exception {
        
        if (args.length != 2) {
            System.err.println("Usage: DocumentSimilarityDriver <input path> <output path>");
            System.exit(-1);
        }
        
        Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf, "document similarity");
        job.setJarByClass(DocumentSimilarityDriver.class);
        
        // Set mapper and reducer classes
        job.setMapperClass(DocumentSimilarityMapper.class);
        job.setReducerClass(DocumentSimilarityReducer.class);
        
        // Set output key and value types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        
        // Set mapper output key and value types
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        // Set input and output formats
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        
        // Set input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        // Set number of reducers to 1 (since we need all docs in one reducer)
        job.setNumReduceTasks(1);
        
        // Wait for job completion
        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}