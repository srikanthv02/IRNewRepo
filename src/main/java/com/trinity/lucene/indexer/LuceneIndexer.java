package com.trinity.lucene.indexer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tartarus.snowball.ext.PorterStemmer;
import org.apache.commons.lang.StringEscapeUtils;


public class LuceneIndexer {
	
public static void index(String docsPath, String indexPath, Analyzer analyzer) {
		
		boolean create = true;
		
		final Path docDir = Paths.get(docsPath);
		
		// Check if the specified document directory exists and if JVM has permissions to read files
		// in this directory
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		// Get the system time when indexing starts so that we can determine the number of seconds
		// it takes to index the documents
		Date start = new Date();
		
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");
			
			FileUtils.cleanDirectory(new File(indexPath)); 
			
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			
			// Initialize a StandardAnalyzer object. This analyzer converts tokens
			// to lowercase and filters out stopwords
			
			// IndexWriterConfig stores all the configuration parameters for IndexWriter
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
						
			if (create) {
				// A new index will be created and any existing indexes will be removed
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// An index already exists so we add new documents to it
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			
			// IndexWriter object creates and maintains an index
			IndexWriter writer = new IndexWriter(dir, iwc);
			
			// Call the indexDocs function that will start the indexing process
			indexDocs(writer, docDir);
			
			// Close the IndexWriter
			writer.close();
			
			// Get the current system time and print the time it took to index all documents
			Date end = new Date();
			System.out.println("Documents Indexed In " + ((end.getTime() - start.getTime()) / 1000.0) + " Seconds");
			
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
		
		
	}
	
	/*
	 * This method crawls the documents path folder and then creates Document objects 
	 * with all the files inside. The Document object instances are then added to the IndexWriter
	 */
	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						// Recurse the directory tree
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						// Ignore files that cannot be read
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}
	
	
	/*
	 * Indexes a document
	 */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		
		try (InputStream stream = Files.newInputStream(file)) {
			
			// Call the parseHTML() method to parse the html file
			org.jsoup.nodes.Document document = parseHTML(file);

			for (Element e : document.getElementsByTag("doc")) {
				String doc_number;
				String text;
				String publication;
				String title;
				String date;
				// Reading Foreign Broadcast Information Service (1996)- fbis
				if(file.toString().contains("fbis")) {
					Document doc = new Document();
					// Add the path of the file as a field named "path". 
					Field pathField = new StringField("path", file.toString(), Field.Store.YES);
					doc.add(pathField);      
			
					
					// Add the last modified date of the file a field named "modified".
					Field modifiedField = new LongPoint("modified", lastModified);
					doc.add(modifiedField);
					
					publication = "fbis";
					doc_number = e.getElementsByTag("docno").text();
					text = e.getElementsByTag("text").text();
					title = e.getElementsByTag("h3").text();
					date = e.getElementsByTag("date1").text();
					System.out.println("Doc Number:"+ doc_number + "\n" + "Abs Text:" + text +"\n" + "Title:" + title + "\n"  + "Date:" + date + "\n\n");
					addDocumentToIndex(doc, doc_number, text, title, date, publication, writer, file);
				}
				
				// Reading from Los Angeles Times (1989, 1990)- latimes
				if(file.toString().contains("latimes")) {
					Document doc = new Document();
					// Add the path of the file as a field named "path". 
					Field pathField = new StringField("path", file.toString(), Field.Store.YES);
					doc.add(pathField);      
			
					
					// Add the last modified date of the file a field named "modified".
					Field modifiedField = new LongPoint("modified", lastModified);
					doc.add(modifiedField);
					
					publication = "latimes";
					doc_number = e.getElementsByTag("docno").text();
					text = e.getElementsByTag("text").text();
					title = e.getElementsByTag("headline").text();
					date = e.getElementsByTag("date").text();
					System.out.println("Doc Number:"+ doc_number + "\n" + "Abs Text:" + text +"\n" + "Title:" + title + "\n"  + "Date:" + date + "\n\n");
					addDocumentToIndex(doc, doc_number, text, title, date, publication, writer, file);

				}
				
				// Reading from Financial Times Limited (1991, 1992, 1993, 1994)- ft
				if(file.toString().contains("ft")) {
					Document doc = new Document();
					// Add the path of the file as a field named "path". 
					Field pathField = new StringField("path", file.toString(), Field.Store.YES);
					doc.add(pathField);      
			
					
					// Add the last modified date of the file a field named "modified".
					Field modifiedField = new LongPoint("modified", lastModified);
					doc.add(modifiedField);
					
					publication = "ft";
					doc_number = e.getElementsByTag("docno").text();
					text = e.getElementsByTag("text").text();
					title = e.getElementsByTag("headline").text();
					date = e.getElementsByTag("date").text();
					System.out.println("Doc Number:"+ doc_number + "\n" + "Abs Text:" + text +"\n" + "Title:" + title + "\n"  + "Date:" + date + "\n\n");
					addDocumentToIndex(doc, doc_number, text, title, date, publication, writer, file);
				}
				
				// Reading from Fr94
				if(file.toString().contains("fr94")) {
					Document doc = new Document();
					// Add the path of the file as a field named "path". 
					Field pathField = new StringField("path", file.toString(), Field.Store.YES);
					doc.add(pathField);      
			
					
					// Add the last modified date of the file a field named "modified".
					Field modifiedField = new LongPoint("modified", lastModified);
					doc.add(modifiedField);
					
					publication = "fr94";
					doc_number = e.getElementsByTag("docno").text();
					text = org.jsoup.parser.Parser.unescapeEntities(e.getElementsByTag("text").text(), true).replaceAll("\\b&[^&]*;\\b", "");
					System.out.println("Doc Number:"+ doc_number + "\n" + "Abs Text:" + text + "\n\n");
					addDocumentToIndex(doc, doc_number, text, "", "", publication, writer, file);
				}
			}
			
		}
	}


	/*
	 * This method creates a Scanner object to read all lines from the html file.
	 * The raw html contents gets stored in the String rawContents.
	 * Jsoup then parses rawContents and creates a parsed document and returns it.
	 */	
	private static org.jsoup.nodes.Document parseHTML(Path file) throws IOException {	
		
		// Parse the raw html using Jsoup
		org.jsoup.nodes.Document document = Jsoup.parse(new File(file.toString()), "utf-8");
		return document;
	}
	
	/**
	 * This function adds documents with respective fields in the IndexWriter
	 * 
	 * @param  indexWriter  list of parsed input cranfield documents to be indexed
	 * @param  collectionDoc the analyzer to be used during the indexing
	 */
	private static void addDocumentToIndex(Document doc, String doc_number, String text, String title, String date, String publication, IndexWriter writer, Path file) throws IOException {
		doc.add(new TextField("text", text, Field.Store.YES));
		doc.add(new StringField("doc_number", doc_number, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new TextField("date", date, Field.Store.YES));
		doc.add(new TextField("publication", publication, Field.Store.YES));
		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			// New index, so we just add the document (no old document can be there):
			System.out.println("Adding " + file);
			writer.addDocument(doc);
		} else {
			// An index already exists and an old version of the file might exist
			// so we update the file
			System.out.println("Updating " + file);
			writer.updateDocument(new Term("path", file.toString()), doc);
		}
	}
}
