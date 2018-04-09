package com.trinity.lucene.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class NgramAnalyzer extends Analyzer {
	
	@Override

	protected TokenStreamComponents createComponents(String arg0) {
		 final Tokenizer source = new NGramTokenizer(3,3);
		    TokenStream result = new StandardFilter(source);
		    result = new EnglishPossessiveFilter(result);
		    result = new LowerCaseFilter(result);
//		    result = new StopFilter(result, stopwords);
//		    if(!stemExclusionSet.isEmpty())
//		      result = new SetKeywordMarkerFilter(result, stemExclusionSet);
		    result = new PorterStemFilter(result);
		    return new TokenStreamComponents(source, result);

	}

}
