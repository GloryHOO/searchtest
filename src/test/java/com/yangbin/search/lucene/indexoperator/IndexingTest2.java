package com.yangbin.search.lucene.indexoperator;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import junit.framework.TestCase;

/**
 * 域加权索引
 * @author yolan
 *
 */
public class IndexingTest2 extends TestCase{
	private String ids[]={"1","2","3","4"};
	private String authors[]={"Jack","Marry","John","Json"};
	private String positions[]={"accounting","technician","salesperson","boss"};
	private String titles[]={"Java is a good language.","Java is a cross platform language","Java powerful","You should learn java"};
	private String contents[]={
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode,",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
	};
	
	/**
	 * 索引目录
	 */
	Directory directory ;


	/**
	 * 创建索引
	 */
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		IndexWriter writer = getWriter();
		for(int i=0;i<ids.length;i++){
			Document doc=new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("authors", authors[i], Field.Store.YES));
			
			doc.add(new StringField("positions", positions[i], Field.Store.YES));
			
			TextField title = new TextField("titles", titles[i], Field.Store.YES);
			/*if("boss".equals(positions[i])){
				title.setBoost(1.5f);  //加权
			}*/
			doc.add(title);
			doc.add(new TextField("contents", contents[i], Field.Store.NO));
			writer.addDocument(doc);
		}
		writer.commit();
	}
	
	public IndexWriter getWriter() throws IOException{
		directory = FSDirectory.open(Paths.get("E:/myprogrammer/index"));
		Analyzer a = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(a);
		IndexWriter writer  = new IndexWriter(directory, conf);
		
		return writer;
	}
	
	
	public void testSearch() throws IOException{
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Term t = new Term("titles","java");
		Query q = new TermQuery(t);
		
		TopDocs hits = searcher.search(q, 10);
		System.out.println("匹配 '"+q+"'，总共查询到"+hits.totalHits+"个文档");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("authors"));
		}
		reader.close();
	}
	
}
