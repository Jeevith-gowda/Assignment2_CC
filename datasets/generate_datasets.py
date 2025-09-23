#!/usr/bin/env python3
"""
Dataset Generator for Document Similarity MapReduce Assignment
Generates three datasets with increasing sizes and realistic text content
"""

import random
import itertools

# Word pools for different themes to create realistic documents with varying similarity
TECH_WORDS = [
    "algorithm", "machine", "learning", "data", "analysis", "computer", "software", 
    "programming", "artificial", "intelligence", "network", "system", "database",
    "application", "development", "technology", "digital", "innovation", "automation",
    "cloud", "computing", "cybersecurity", "blockchain", "neural", "processing"
]

BUSINESS_WORDS = [
    "company", "business", "market", "customer", "revenue", "profit", "strategy",
    "management", "operations", "finance", "investment", "growth", "sales",
    "marketing", "product", "service", "enterprise", "organization", "leadership",
    "team", "project", "planning", "execution", "performance", "results"
]

NATURE_WORDS = [
    "forest", "tree", "animal", "wildlife", "environment", "ecosystem", "nature",
    "habitat", "species", "biodiversity", "conservation", "climate", "weather",
    "mountain", "river", "ocean", "plant", "flower", "grass", "soil", "rock",
    "bird", "fish", "mammal", "insect", "butterfly", "sustainability"
]

SCIENCE_WORDS = [
    "research", "experiment", "laboratory", "hypothesis", "theory", "discovery",
    "investigation", "methodology", "analysis", "observation", "evidence", "study",
    "physics", "chemistry", "biology", "mathematics", "statistics", "variables",
    "measurement", "calculation", "formula", "equation", "scientific", "academic"
]

COMMON_WORDS = [
    "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with",
    "from", "by", "about", "through", "during", "before", "after", "over",
    "under", "between", "among", "within", "without", "including", "such"
]

def generate_document_text(doc_id, target_words, themes=None):
    """Generate realistic document text with specified word count and themes"""
    if themes is None:
        themes = random.sample([TECH_WORDS, BUSINESS_WORDS, NATURE_WORDS, SCIENCE_WORDS], 2)
    
    # Combine theme words with common words
    word_pool = []
    for theme in themes:
        word_pool.extend(theme)
    word_pool.extend(COMMON_WORDS * 2)  # Make common words more frequent
    
    # Generate sentences
    sentences = []
    words_generated = 0
    
    while words_generated < target_words:
        # Generate sentence with 8-15 words
        sentence_length = random.randint(8, 15)
        sentence_words = []
        
        for i in range(sentence_length):
            if words_generated >= target_words:
                break
            word = random.choice(word_pool)
            sentence_words.append(word)
            words_generated += 1
        
        sentences.append(" ".join(sentence_words))
    
    return " ".join(sentences)

def generate_dataset(filename, num_docs, total_target_words):
    """Generate a complete dataset file"""
    words_per_doc = total_target_words // num_docs
    
    with open(filename, 'w') as f:
        for i in range(1, num_docs + 1):
            # Vary word count slightly per document (±20%)
            doc_words = int(words_per_doc * random.uniform(0.8, 1.2))
            
            # Assign themes based on document clusters for realistic similarity
            if i <= num_docs // 4:
                themes = [TECH_WORDS, BUSINESS_WORDS]
            elif i <= num_docs // 2:
                themes = [BUSINESS_WORDS, SCIENCE_WORDS]
            elif i <= 3 * num_docs // 4:
                themes = [NATURE_WORDS, SCIENCE_WORDS]
            else:
                themes = [TECH_WORDS, NATURE_WORDS]
            
            doc_text = generate_document_text(doc_words, doc_words, themes)
            f.write(f"Document{i} {doc_text}\n")

def main():
    """Generate all three datasets"""
    
    print("Generating Dataset1: ~100 documents, ~5,000 words...")
    generate_dataset("dataset1.txt", 100, 5000)
    
    print("Generating Dataset2: ~200 documents, ~15,000 words...")
    generate_dataset("dataset2.txt", 200, 15000)
    
    print("Generating Dataset3: ~300 documents, ~25,000 words...")
    generate_dataset("dataset3.txt", 300, 25000)
    
    # Print statistics
    for dataset in ["dataset1.txt", "dataset2.txt", "dataset3.txt"]:
        with open(dataset, 'r') as f:
            lines = f.readlines()
            total_words = sum(len(line.split()) - 1 for line in lines)  # -1 for document ID
            print(f"{dataset}: {len(lines)} documents, {total_words} words")

if __name__ == "__main__":
    main()
