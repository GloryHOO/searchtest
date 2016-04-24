package com.yangbin.search.lucene.indexoperator;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import junit.framework.TestCase;
/**
 * �����Ļ�������ʵ��
 * @author yolan
 *
 */
public class IndexingTest extends TestCase {
	/**
	 * ��Ҫ����������
	 */
	protected String[] ids = {"1","2","3"};
	protected String[] name = {"sky","sea","earth"};
	protected String[] content = {"��ν���֣��Ǽ�¼��Ϣ��ͼ����š�����۵㣺�����Ǽ�¼���Եķ��š�����ԭ�����������ۡ����������ڳ����й����ֵ��������֣���������ƴ�����֡�ƴ�����������Եļ�¼�����������ֲ������Եļ�¼�������¼��ļ�¼���������ġ�ţ���֣��������һ��ţͷ���������������֪����ţ�������ţ�֣�����������������������cow�����簣�����������֣���������Ш�����֣�������ƴ�����֣����������������ֿ϶��������Եļ�¼��ֻ�����Ƕ�������˵�����Ǽ�¼���Եķ��ţ�����һ��ԭ�򣬾������ڻ��кܶ����壬ֻ������û�����֡������ⲻ��֤���й�����Ҳ�����Եļ�¼��Ҳ����˵����֤���й����ֲ���������֮�����������ڵ��ϻ���ʯ�ϻ���ʱ���������Ԫ��������ȫ��ֻ���໡��������ีؽл������������ܹ�����ʶ�ط�������+Ԫ���ĵ��������衢�ˡ�������ʱ������������Ѷ��ý��������ˡ�������й��м�ʮ�����ԣ����Ƕ��ܶ�Ӧͬһ�����֣����������۵���ôҲ���Ͳ��˵ġ�",
			"������Beijing������ƾ����л����񹲺͹��׶���ֱϽ�С��������ĳ��С�������У�ȫ���������ġ��Ļ����ġ����ʽ������ġ��Ƽ��������ģ����й�����������ίԱ�ᡢ�л����񹲺͹���������������ȫ�����������İ칫���ڵء�",
			"��(Ӣ������:Bears):��ʳ��Ŀ�ܿƶ����ͨ��,��ƽʱ�����º�,�����ܵ����ƻ�����Σ��ʱ,���ױ�ŭ,�������ǳ����͡���Ȼһ���˰��ܿ�����Σ�յĶ���,������Ϸ��"};
	
	//��������Ŀ¼
	private Directory directory;
	/**
	 * ÿ�β���ǰ���У���������
	 */
	protected void setUp() throws Exception {
		//Directory dir = FSDirectory.open(Paths.get(indexDir)); 
		//ʹ���ڴ���Ϊ�������ַ���ٶȿ�
		//directory = new RAMDirectory();
		directory = FSDirectory.open(Paths.get("E:/myprogrammer/index"));
		
		IndexWriter writer = getWriter();
		
		for(int i=0;i<3;i++){
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("name", name[i], Field.Store.YES));
			doc.add(new TextField("contente", content[i], Field.Store.NO));
			//����ĵ�
			//�������ò���1
			writer.addDocument(doc);
		}
		writer.close();
	}
	
	/**
	 * ����indexwriter ����� ������
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	public IndexWriter getWriter() throws IOException{
		//Analyzer analyzer = new StandardAnalyzer();
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		
		return new IndexWriter(directory, conf);
	}
	
	
	/**
	 * �����鿴������
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */

	public  int getHitCount() throws IOException, ParseException{
		IndexReader r = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(r);
		//Term t = new Term("contente", "sun");
		//Query query = new TermQuery(t);
		//��׼������������Ӣ��
		/*		Analyzer analyzer = new StandardAnalyzer();
				//������ѯ�ַ���
				QueryParser parser = new QueryParser("contente", analyzer);
				Query query = parser.parse("sun");*/
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		QueryParser parser = new QueryParser("contente", analyzer);
		Query query = parser.parse("Bears");
		
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			//��ʾ�ļ��� (Field.Store.YES))
			System.out.println(doc.get("name"));
		}
		r.close();
		return hits.totalHits;
		
	}
	
	/**
	 * 1.д�ĵ� addDocument
	 * @throws IOException
	 */
	public void testIndexWriter() throws IOException{
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());
		writer.close();
	}
	/**
	 * ������
	 * @throws IOException
	 */
	public void testIndexReader() throws IOException{
		IndexReader r = DirectoryReader.open(directory);
		System.out.println(r.maxDoc());
		System.out.println(r.numDocs());
		r.close();
	}
	/**
	 * ɾ������ deleteDocuments(
	 * @throws IOException 
	 */
	public void testDeleteIndex() throws IOException{
		IndexWriter writer = getWriter();
		//ɾ���������ĵ�
		writer.deleteDocuments(new Term("id","1"));
		//�ύ
		writer.commit();
		System.out.println(writer.maxDoc());
		System.out.println(writer.numDocs());
		writer.close();
	}
	
	/**
	 * ɾ������,Ȼ��ϲ� forceMergeDeletes();
	 * @throws IOException 
	 */
	public void testDeleteIndexAfterOptimize1() throws IOException{
		IndexWriter writer = getWriter();
		//ɾ���������ĵ�
		writer.deleteDocuments(new Term("id","1"));
		//ǿ�ƺϲ�
		writer.forceMergeDeletes();
		//�ύ
		writer.commit();
		System.out.println(writer.maxDoc());
		System.out.println(writer.numDocs());
		writer.close();
	}
	
	/**
	 * ��������
	 * ��ɾ���
	 * @throws IOException
	 */
	public void testUpdateIndex()throws IOException{
		IndexWriter writer = getWriter();
		Document doc = new Document();
		doc.add(new StringField("id","3", Field.Store.YES));
		doc.add(new StringField("name", "earth", Field.Store.YES));
		doc.add(new TextField("contente", "The sun shines all over the world.but havnt earth!", Field.Store.NO));
		//����ֻ�����ĵ�Ϊ��λ
		writer.updateDocument(new Term("id","3"), doc);;;
		
		writer.close();
	}
}
