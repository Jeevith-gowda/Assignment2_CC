package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, NullWritable> {
    
    private Text result = new Text();
    
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) 
            throws IOException, InterruptedException {
        
        List<DocumentInfo> documents = new ArrayList<>();
        
        // Parse all document data
        for (Text value : values) {
            String docData = value.toString();
            String[] parts = docData.split("\t");
            if (parts.length >= 2) {
                String docId = parts[0];
                Set<String> words = new HashSet<>();
                
                // Parse words (comma-separated)
                if (!parts[1].trim().isEmpty()) {
                    String[] wordArray = parts[1].split(",");
                    for (String word : wordArray) {
                        if (!word.trim().isEmpty()) {
                            words.add(word.trim());
                        }
                    }
                }
                
                documents.add(new DocumentInfo(docId, words));
            }
        }
        
        // Compute pairwise similarities
        for (int i = 0; i < documents.size(); i++) {
            for (int j = i + 1; j < documents.size(); j++) {
                DocumentInfo doc1 = documents.get(i);
                DocumentInfo doc2 = documents.get(j);
                
                double similarity = calculateJaccardSimilarity(doc1.words, doc2.words);
                
                // Format: "Document1, Document2 Similarity: 0.56"
                String output = String.format("%s, %s Similarity: %.2f", 
                    doc1.docId, doc2.docId, similarity);
                
                result.set(output);
                context.write(result, NullWritable.get());
            }
        }
    }
    
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        // Create copies to avoid modifying original sets
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.size() == 0) {
            return 0.0;
        }
        
        return (double) intersection.size() / union.size();
    }
    
    // Inner class to hold document information
    private static class DocumentInfo {
        String docId;
        Set<String> words;
        
        DocumentInfo(String docId, Set<String> words) {
            this.docId = docId;
            this.words = words;
        }
    }
}