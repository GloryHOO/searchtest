package com.yangbin.search.lucene.Seacher;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * lucene 搜索类
 * @author yolan
 *
 */
public class Seacher {
	/**
	 * 
	 * @param indexDir 索引文件夹
	 * @param q 查询字符串
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void search(String indexDir,String q) throws IOException, ParseException{
		//打开索引文件
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader r = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(r); 
		//标准分析器，分析英文
		Analyzer analyzer = new StandardAnalyzer();
		//解析查询字符串
		QueryParser parser = new QueryParser("contents", analyzer);
		Query query = parser.parse(q);
		
		//搜索索引，返回匹配图前10个文档
		long start=System.currentTimeMillis();
		TopDocs hits = is.search(query, 10);
		long end=System.currentTimeMillis();
		
		System.out.println("匹配 "+q+" ，总共花费"+(end-start)+"毫秒"+"查询到"+hits.totalHits+"个记录");
		
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			//显示文件名 (Field.Store.YES))
			System.out.println(doc.get("fullpath"));
		}
		
		r.close();
		dir.close();
	}
	
	
	public static void main(String[] args) throws IOException, ParseException {
		String indexDir="E:/myprogrammer/index";
		//String q="complete application";
		//String q="patent AND NOT apache";
		String q="+copyright +developers";
		
		search(indexDir, q);
	}
}
