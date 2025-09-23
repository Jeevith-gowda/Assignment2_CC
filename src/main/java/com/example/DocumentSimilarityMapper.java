package com.example;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {
    
    private final static Text constantKey = new Text("all_docs");
    private Text docData = new Text();
    
    @Override
    public void map(LongWritable key, Text value, Context context) 
            throws IOException, InterruptedException {
        
        String line = value.toString().trim();
        if (line.isEmpty()) {
            return;
        }
        
        // Split into document ID and content
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) {
            return; // Skip malformed lines
        }
        
        String docId = parts[0];
        String content = parts[1];
        
        // Extract unique words
        Set<String> uniqueWords = extractUniqueWords(content);
        
        // Create document data: docId + unique words separated by tabs
        StringBuilder sb = new StringBuilder();
        sb.append(docId).append("\t");
        for (String word : uniqueWords) {
            sb.append(word).append(",");
        }
        // Remove trailing comma
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        
        docData.set(sb.toString());
        context.write(constantKey, docData);
    }
    
    private Set<String> extractUniqueWords(String text) {
        Set<String> words = new HashSet<>();
        
        // Convert to lowercase and remove punctuation
        text = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
        
        StringTokenizer tokenizer = new StringTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().trim();
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        
        return words;
    }
}