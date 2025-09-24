# Assignment 2: Document Similarity using MapReduce

**Course**: Cloud Computing for Data Analysis (ITCS 6190/8190)   
**Student**: Jeevith Doddalingegowda Rama

## Project Overview

This project implements a Hadoop MapReduce application to compute Jaccard Similarity between multiple text documents. The system processes collections of documents, extracts unique word sets, and calculates pairwise similarities using distributed processing.

## Implementation Details

### Algorithm: Jaccard Similarity
The Jaccard Similarity measures how similar two sets are:
Jaccard Similarity (A, B) = |A ∩ B| / |A ∪ B|

Where:
- A and B are sets of unique words from two documents
- |A ∩ B| is the number of words common to both documents  
- |A ∪ B| is the total number of unique words across both documents

### MapReduce Architecture

**Single-Stage MapReduce Design:**
- **Mapper**: Parses documents, normalizes text (lowercase, removes punctuation), extracts unique words per document
- **Reducer**: Receives all document word sets, computes pairwise Jaccard similarities for all document combinations

**Text Preprocessing:**
- Converts all text to lowercase
- Removes punctuation using regex `[^a-zA-Z0-9\\s]`
- Tokenizes on whitespace
- Maintains unique word sets per document

## Dataset Specifications

Three datasets were programmatically generated with varying sizes:

| Dataset | Documents | Words | Document Pairs | Characteristics |
|---------|-----------|-------|----------------|-----------------|
| Dataset1 | 100 | ~5,000 | 4,950 | Tech/Business themes |
| Dataset2 | 200 | ~15,000 | 19,900 | Business/Science themes |
| Dataset3 | 300 | ~25,000 | 44,850 | Nature/Science themes |

**Dataset Generation Features:**
- Thematic clustering for realistic similarity patterns
- Varied vocabulary with controlled overlap
- Realistic sentence structure (8-15 words per sentence)
- Mixed themes to create diverse similarity scores

## Performance Analysis

### Experimental Setup

**3-Node Cluster Configuration:**
- 1 NameNode
- 3 DataNodes (datanode1, datanode2, datanode3)
- 3 NodeManagers
- 1 ResourceManager
- Docker-based Hadoop 3.2.1 cluster

**1-Node Cluster Configuration:**
- 1 NameNode  
- 1 DataNode (datanode1 only)
- 3 NodeManagers (maintained for YARN processing)
- 1 ResourceManager

### Execution Results

| Dataset | 3 Data Nodes | 1 Data Node | Performance Gain | Improvement |
|---------|--------------|-------------|------------------|-------------|
| Dataset1 | 23.10s | 27.49s | 3-node faster | **16.0%** |
| Dataset2 | 20.10s | 20.67s | 3-node faster | **2.8%** |
| Dataset3 | 20.41s | 21.26s | 3-node faster | **4.0%** |

### Key Performance Insights

1. **Distributed Processing Overhead**: For moderate-sized datasets, the coordination overhead in multi-node clusters can nearly offset parallelism benefits.

2. **Dataset Size vs Performance**: Counter-intuitively, Dataset1 showed the largest performance improvement with distributed processing, while larger datasets showed diminishing returns.

3. **MapReduce Framework Dominance**: Job startup, container initialization, and framework overhead constitute significant portions of total execution time for these dataset sizes.

4. **Single Reducer Bottleneck**: Since all documents must be processed by one reducer to compute all pairwise similarities, the reduce phase becomes a bottleneck that limits distributed processing benefits.

5. **I/O vs Computation Balance**: The relatively simple Jaccard similarity computation means that data shuffling and I/O operations dominate the processing time.

## Architecture Analysis

### Strengths
- **Scalable Design**: Architecture supports much larger datasets through HDFS distribution
- **Fault Tolerance**: Hadoop's built-in replication and failure recovery
- **Text Processing**: Robust normalization handles various text formats
- **Accurate Results**: Precise Jaccard similarity calculations with proper set operations

### Limitations  
- **Single Reducer Design**: All document pairs computed by one reducer limits scalability
- **Memory Requirements**: Large document collections require significant memory for similarity matrix
- **Framework Overhead**: MapReduce initialization costs significant for moderate datasets

### Potential Optimizations
- **Multi-stage MapReduce**: Implement distributed similarity computation across multiple reducers
- **Sampling Techniques**: Use approximate algorithms for very large document collections
- **Caching**: Leverage Hadoop caching for repeated computations
- **Custom Partitioning**: Distribute document pairs across multiple reducers

## Technical Implementation

### File Structure
```bash

src/main/java/com/example/
├── DocumentSimilarityMapper.java      # Document parsing and word extraction
├── DocumentSimilarityReducer.java     # Pairwise similarity computation
└── controller/
└── DocumentSimilarityDriver.java  # MapReduce job configuration
```
### Key Classes

**DocumentSimilarityMapper**
- Parses input format: `DocumentID content text here`
- Extracts unique words with normalization
- Outputs: `(constant_key, "docId\tword1,word2,word3")`

**DocumentSimilarityReducer**  
- Receives all document word sets
- Computes Jaccard similarity for all pairs
- Outputs: `"Document1, Document2 Similarity: 0.XX"`

**DocumentSimilarityDriver**
- Configures MapReduce job parameters
- Sets single reducer for complete pairwise computation
- Manages input/output paths

## Running the Application

### Prerequisites
- Docker and Docker Compose
- Java 8+
- Maven 3.6+
- Git

### Execution Steps

1. **Clone and Setup**
```bash
   git clone [repository-url]
   cd Assignment2_CC
```
2. **Build Project**
```bash
mvn clean package
```
2. **Start Hadoop Cluster**
```bash
docker-compose up -d
```
4. **Prepare Data**
```bash
docker cp datasets/dataset1.txt namenode:/tmp/
docker exec namenode hdfs dfs -mkdir -p /input
docker exec namenode hdfs dfs -put /tmp/dataset1.txt /input/
```
5. **Run MapReduce Job**
```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar namenode:/tmp/
docker exec namenode hadoop jar /tmp/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/dataset1.txt /output/dataset1_results
```

7. **View Results**
```bash
docker exec namenode hdfs dfs -cat /output/dataset1_results/part-r-00000
```

## Results Repository Structure

```bash
results/
├── phase1_3nodes/              # 3-node cluster results
│   ├── execution_times.txt      # Performance measurements
│   ├── dataset1_3nodes_output.txt
│   ├── dataset2_3nodes_output.txt
│   └── dataset3_3nodes_output.txt
├── phase2_1node/               # 1-node cluster results  
│   ├── execution_times.txt      # Performance measurements
│   ├── dataset1_1node_output.txt
│   ├── dataset2_1node_output.txt
│   └── dataset3_1node_output.txt
└── datasets/                    # Generated test datasets
    ├── dataset1.txt             # 100 documents, ~5K words
    ├── dataset2.txt             # 200 documents, ~15K words
    └── dataset3.txt             # 300 documents, ~25K words
```

## Conclusions

This project demonstrates both the power and limitations of distributed processing for text analytics workloads. While Hadoop MapReduce provides an excellent framework for large-scale document similarity computation, the results highlight the importance of:

Matching Architecture to Problem Scale: Distributed processing overhead must be justified by computational complexity
Understanding Framework Characteristics: MapReduce startup costs can dominate processing time for moderate datasets
Bottleneck Analysis: Single reducer designs limit the benefits of distributed data storage
Real-world Performance Testing: Theoretical scalability advantages require empirical validation

The implementation successfully demonstrates MapReduce programming concepts, HDFS usage, and performance analysis techniques essential for cloud computing and big data analytics.


## Future Enhancements

- Implement distributed similarity computation across multiple reducers
- Add support for additional similarity metrics (Cosine, Euclidean)
- Integrate with Spark for improved performance on iterative algorithms
- Add real-time streaming similarity computation capabilities
- Implement approximate algorithms for very large document collections


