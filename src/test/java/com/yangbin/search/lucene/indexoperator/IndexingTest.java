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
 * 索引的基本操作实验
 * @author yolan
 *
 */
public class IndexingTest extends TestCase {
	/**
	 * 需要索引的内容
	 */
	protected String[] ids = {"1","2","3"};
	protected String[] name = {"sky","sea","earth"};
	protected String[] content = {"所谓文字，是记录信息的图像符号。错误观点：文字是记录语言的符号。错误原因：西方中心论。世界上现在除了中国文字的象形文字，其他都是拼音文字。拼音文字是语言的记录，但象形文字不是语言的记录，而是事件的记录。比如中文‘牛’字，最初就是一个牛头画，看了这个画就知道是牛，而这个牛字，可以念各种音，甚至可以念：cow。最早埃及有象形文字，苏美尔有楔形文字，都不是拼音文字，但埃及的象形文字肯定不是语言的记录，只是它们都湮灭了说文字是记录语言的符号，还有一个原因，就是现在还有很多民族，只有语言没有文字。但是这不能证明中国文字也是语言的记录，也就是说不能证明中国文字产生在语言之后。人类最早在地上或岩石上画画时，可能五个元音还发不全，只能嗷嗷、啊啊、喔喔地叫唤。而当他们能够有意识地发出辅音+元音的单音（如妈、八、哈）的时候，人类可能早已懂得结绳记事了。现如今中国有几十种语言，但是都能对应同一种文字，这是西方观点怎么也解释不了的。",
			"北京（Beijing），简称京，中华人民共和国首都、直辖市、国家中心城市、超大城市，全国政治中心、文化中心、国际交往中心、科技创新中心，是中国共产党中央委员会、中华人民共和国中央人民政府和全国人民代表大会的办公所在地。",
			"熊(英文名称:Bears):是食肉目熊科动物的通称,熊平时还算温和,但是受到挑衅或遇到危险时,容易暴怒,打斗起来非常凶猛。虽然一般人把熊看做是危险的动物,但在马戏团"};
	
	//索引所在目录
	private Directory directory;
	/**
	 * 每次测试前运行，创建索引
	 */
	protected void setUp() throws Exception {
		//Directory dir = FSDirectory.open(Paths.get(indexDir)); 
		//使用内存作为索引库地址，速度快
		//directory = new RAMDirectory();
		directory = FSDirectory.open(Paths.get("E:/myprogrammer/index"));
		
		IndexWriter writer = getWriter();
		
		for(int i=0;i<3;i++){
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("name", name[i], Field.Store.YES));
			doc.add(new TextField("contente", content[i], Field.Store.NO));
			//添加文档
			//索引常用操作1
			writer.addDocument(doc);
		}
		writer.close();
	}
	
	/**
	 * 创建indexwriter 对象的 建造器
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
	 * 搜索查看命中数
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */

	public  int getHitCount() throws IOException, ParseException{
		IndexReader r = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(r);
		//Term t = new Term("contente", "sun");
		//Query query = new TermQuery(t);
		//标准分析器，分析英文
		/*		Analyzer analyzer = new StandardAnalyzer();
				//解析查询字符串
				QueryParser parser = new QueryParser("contente", analyzer);
				Query query = parser.parse("sun");*/
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		QueryParser parser = new QueryParser("contente", analyzer);
		Query query = parser.parse("Bears");
		
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits);
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			//显示文件名 (Field.Store.YES))
			System.out.println(doc.get("name"));
		}
		r.close();
		return hits.totalHits;
		
	}
	
	/**
	 * 1.写文档 addDocument
	 * @throws IOException
	 */
	public void testIndexWriter() throws IOException{
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());
		writer.close();
	}
	/**
	 * 读索引
	 * @throws IOException
	 */
	public void testIndexReader() throws IOException{
		IndexReader r = DirectoryReader.open(directory);
		System.out.println(r.maxDoc());
		System.out.println(r.numDocs());
		r.close();
	}
	/**
	 * 删除索引 deleteDocuments(
	 * @throws IOException 
	 */
	public void testDeleteIndex() throws IOException{
		IndexWriter writer = getWriter();
		//删除索引中文档
		writer.deleteDocuments(new Term("id","1"));
		//提交
		writer.commit();
		System.out.println(writer.maxDoc());
		System.out.println(writer.numDocs());
		writer.close();
	}
	
	/**
	 * 删除索引,然后合并 forceMergeDeletes();
	 * @throws IOException 
	 */
	public void testDeleteIndexAfterOptimize1() throws IOException{
		IndexWriter writer = getWriter();
		//删除索引中文档
		writer.deleteDocuments(new Term("id","1"));
		//强制合并
		writer.forceMergeDeletes();
		//提交
		writer.commit();
		System.out.println(writer.maxDoc());
		System.out.println(writer.numDocs());
		writer.close();
	}
	
	/**
	 * 更新索引
	 * 先删后加
	 * @throws IOException
	 */
	public void testUpdateIndex()throws IOException{
		IndexWriter writer = getWriter();
		Document doc = new Document();
		doc.add(new StringField("id","3", Field.Store.YES));
		doc.add(new StringField("name", "earth", Field.Store.YES));
		doc.add(new TextField("contente", "The sun shines all over the world.but havnt earth!", Field.Store.NO));
		//更新只能以文档为单位
		writer.updateDocument(new Term("id","3"), doc);;;
		
		writer.close();
	}
}
