package com.trinity.lucene.searcher;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.benchmark.quality.QualityBenchmark;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.QualityStats;
import org.apache.lucene.benchmark.quality.trec.*;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.SubmissionReport;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.AfterEffectB;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicModelIn;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.NormalizationH1;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;

public class LuceneSearcher {
	
	public static void runQueries(String queryfile, Analyzer analyzer, String indexDir, String outputPath, String outputPath1) {
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(queryfile))))){
			Directory dir = FSDirectory.open(Paths.get(indexDir));
	        IndexReader reader = DirectoryReader.open(dir);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        Similarity similarity[] = { new BM25Similarity(2, (float) 0.89),
					new DFRSimilarity(new BasicModelIn(), new AfterEffectB(), new NormalizationH1()),
					new LMDirichletSimilarity(1500) };
			// Set the similarity metrics to searcher
	        searcher.setSimilarity(new MultiSimilarity(similarity));
			TrecTopicsReader ttr = new TrecTopicsReader();
			QualityQuery[] qqs = ttr.readQueries(br);
			
			Set<String> fieldSet = new HashSet<>();
			fieldSet.add("title");
			fieldSet.add("description");
			fieldSet.add("narrative");
			
			String qqNames[] = fieldSet.toArray(new String[0]);
			
			//Query qqParser = MultiFieldQueryParser.parse(fieldSet.toArray(new String[0]),new String[]{"text", "title"},analyzer);
			
			QualityQueryParser qqParser = new SimpleQQParser(fieldSet.toArray(new String[0]), "text");
			
			
		    
			QueryParser qp = new QueryParser("text", analyzer);
			QueryParser qp1 = new QueryParser("title", analyzer);
			
			String[] fields = new String[] { "text", "title"};
			HashMap<String,Float> boosts = new HashMap<String,Float>();
			boosts.put("text", (float) 12);
			boosts.put("title", (float) 2);
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
			    fields, 
			    analyzer,
			    boosts
			);
			
			BufferedWriter bufferedWriterVSM = new BufferedWriter(new FileWriter(new File(outputPath1)));
			SubmissionReport submitLog = new SubmissionReport(new PrintWriter(outputPath, IOUtils.UTF_8 /* huh, no nio.Charset ctor? */), "lucene");
			for (int i=0; i<qqs.length; i++) {
				
				
			      QualityQuery qq = qqs[i];
			      System.out.println("Parsing Query ID:"+ qq.getQueryID());
			      // generate query
			      
				    BooleanQuery.Builder bq = new BooleanQuery.Builder();
//				    Query q = bq.build();
//				    BooleanQuery.Builder bq1 = new BooleanQuery.Builder();
//				    BooleanQuery.Builder bqt = new BooleanQuery.Builder();
				    for (int j = 0; j < qqNames.length; j++) {
				      bq.add(queryParser.parse(QueryParserBase.escape(qq.getValue(qqNames[j]))), BooleanClause.Occur.SHOULD);
				    	  //bq1.add(qp1.parse(QueryParserBase.escape(qq.getValue(qqNames[j]))), BooleanClause.Occur.SHOULD);
				    }
//				    bq.build().createWeight(searcher, true, 10);
//				    bq1.build().createWeight(searcher, true, 5);
//				    bqt.add(bq.build(),BooleanClause.Occur.SHOULD);
//				    bqt.add(bq1.build(),BooleanClause.Occur.SHOULD);
			      // search with this query 
			      long t1 = System.currentTimeMillis();
			      TopDocs td = searcher.search(bq.build(),1000);
			      generateTrecResultsFile(bufferedWriterVSM, td.scoreDocs, qq.getQueryID(), searcher);
			      long searchTime = System.currentTimeMillis()-t1;
			      //most likely we either submit or judge, but check both
			      if (submitLog!=null) {
			    	  submitLog.report(qq,td,"doc_number",searcher);
			      }
			    }
			bufferedWriterVSM.close();
			
			
//			QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, "text");
//			qrun.setMaxResults(1000);
//			qrun.setMaxQueries(50);
//			PrintWriter logger = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()), true);
//			QualityStats stats[] = qrun.execute(null, submitLog, logger);
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public static void generateTrecResultsFile(BufferedWriter writer, ScoreDoc[] hits, String queryIndex, IndexSearcher searcher)
			throws IOException {
		int hitCounter = 0;

		for (ScoreDoc hit : hits) {
			int docId = hit.doc;
			Document doc = searcher.doc(docId);
			writer.write(queryIndex + " Q0 " + doc.get("doc_number") + " " + hitCounter + " " + hit.score
					+ " lucene" + "\n");
			hitCounter++;
		}
	}

}
