package com.trinity.lucene.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class EnglishStopAnalyzer extends Analyzer{
	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		 final Tokenizer source = new StandardTokenizer();
		    TokenStream result = new StandardFilter(source);
		    result = new EnglishPossessiveFilter(result);
		    result = new LowerCaseFilter(result);
		    result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		    result = new PorterStemFilter(result);
		    return new TokenStreamComponents(source, result);

	}
}
