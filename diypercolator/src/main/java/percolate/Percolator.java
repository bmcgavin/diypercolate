package percolate;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class Percolator {
	
	public static final Version VERSION = Version.LUCENE_43;
	public static final String F_CONTENT = "content";
	private List<Query> queries;
	private MemoryIndex index;
	
	public Percolator() {
		queries = new ArrayList<Query>();
		index = new MemoryIndex();
	}
	
	public void addQuery(String query) throws ParseException {
		Analyzer analyzer = new SimpleAnalyzer(VERSION);
		QueryParser parser = new QueryParser(Version.LUCENE_43, F_CONTENT, analyzer);
		queries.add(parser.parse(query));
	}
	
	
	public List<Query> getMatchingQueries(String doc) {
		synchronized (index) {
			index.reset();
			index.addField(F_CONTENT, doc, new SimpleAnalyzer(VERSION));
		}
		
		List<Query> matching = new ArrayList<Query>();
		for (Query qry : queries) {
			if (index.search(qry) > 0.0f) {
				matching.add(qry);
			}
		}
		
		return matching;
	}
	

	public static void main(String[] args) throws ParseException {
		Percolator percolator = new Percolator();
		percolator.addQuery("one");
		percolator.addQuery("two");
		
		System.out.println(percolator.getMatchingQueries("one two three"));
		System.out.println(percolator.getMatchingQueries("two"));
		System.out.println(percolator.getMatchingQueries("three"));
	}
}
