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
 * lucene ������
 * @author yolan
 *
 */
public class Seacher {
	/**
	 * 
	 * @param indexDir �����ļ���
	 * @param q ��ѯ�ַ���
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void search(String indexDir,String q) throws IOException, ParseException{
		//�������ļ�
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader r = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(r); 
		//��׼������������Ӣ��
		Analyzer analyzer = new StandardAnalyzer();
		//������ѯ�ַ���
		QueryParser parser = new QueryParser("contents", analyzer);
		Query query = parser.parse(q);
		
		//��������������ƥ��ͼǰ10���ĵ�
		long start=System.currentTimeMillis();
		TopDocs hits = is.search(query, 10);
		long end=System.currentTimeMillis();
		
		System.out.println("ƥ�� "+q+" ���ܹ�����"+(end-start)+"����"+"��ѯ��"+hits.totalHits+"����¼");
		
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			//��ʾ�ļ��� (Field.Store.YES))
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
