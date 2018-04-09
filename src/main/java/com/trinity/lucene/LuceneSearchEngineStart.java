package com.trinity.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.trinity.lucene.analyzers.EnglishStopAnalyzer;
import com.trinity.lucene.analyzers.NgramAnalyzer;
import com.trinity.lucene.indexer.LuceneIndexer;
import com.trinity.lucene.searcher.LuceneSearcher;

public class LuceneSearchEngineStart {
	
		// All the required paths
		static String INPUT_DOC_PATH = "/input_docs/";
		static String INDEX_PATH = "/indexer_docs";
		static String OUTPUT_PATH_STANDARD = "/output/output_standard.txt";
		static String OUTPUT_PATH_NGRAM = "/output/output_ngram.txt";
		static String QUERY_PATH = "/queries/CS7IS3-Assignment2-Topics";
		static String OUTPUT_PATH_STANDARD1 = "/output/output_standard1.txt";
		static String OUTPUT_PATH_NGRAM1 = "/output/output_ngram1.txt";
		

	public static void main(String[] args) {
		String systemPath = System.getProperty("user.dir");
		//StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
		//LuceneIndexer.index(systemPath + INPUT_DOC_PATH, systemPath + INDEX_PATH, standardAnalyzer);
		//LuceneSearcher.runQueries(systemPath + QUERY_PATH, standardAnalyzer, systemPath + INDEX_PATH, systemPath + OUTPUT_PATH_STANDARD, systemPath + OUTPUT_PATH_STANDARD1);
		//NgramAnalyzer ngramAnalyzer = new NgramAnalyzer();
		EnglishStopAnalyzer ngramAnalyzer = new EnglishStopAnalyzer();
		//LuceneIndexer.index(systemPath + INPUT_DOC_PATH, systemPath + INDEX_PATH, ngramAnalyzer);
		LuceneSearcher.runQueries(systemPath + QUERY_PATH, ngramAnalyzer, systemPath + INDEX_PATH, systemPath + OUTPUT_PATH_NGRAM, systemPath + OUTPUT_PATH_NGRAM1);

	}

}
