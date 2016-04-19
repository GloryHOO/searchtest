package com.yangbin.search.lucene.indexer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	private IndexWriter writer; //写索引对象
	/**
	 * 创建index writer
	 * @param indexDir
	 * @throws IOException
	 */
	public Indexer(String indexDir) throws IOException{
		Directory dir = FSDirectory.open(Paths.get(indexDir)); 
		
		Analyzer analyzer = new StandardAnalyzer();
	
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		
		writer = new IndexWriter(dir, conf);
		
	}
	/**
	 * 关闭index writer
	 * @throws IOException
	 */
	public void close() throws IOException{
		writer.close();
	}
	/**
	 * 创建索引，返回文件数
	 * @param dataDir
	 * @param filter
	 * @return
	 * @throws IOException 
	 */
	public int index(String dataDir,FileFilter filter) throws IOException{
		File[] files = new File(dataDir).listFiles();
		
		for(File f: files){
			if(!f.isDirectory()&& !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f)) ){
				indexFile(f);
			}
		}
		return writer.numDocs();
	}
	/**
	 * 根据传入文件，创建索引
	 * @param f
	 * @throws IOException 
	 */
	public void indexFile(File f) throws IOException{
		System.out.println("Indexing:"+f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc);
	}
	/**
	 * 创建文档内容
	 * @param f
	 * @return
	 * @throws IOException 
	 */
	protected Document getDocument(File f) throws IOException{
		Document doc = new Document();
		doc.add(new TextField("contents", new FileReader(f)));
		doc.add(new StringField("filename", f.getName(), Field.Store.YES));
		doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));
		return doc;
	}
	/**
	 * 文件过滤器
	 * @author yolan
	 *
	 */
	private static class TextFilesFilter implements FileFilter{
		public boolean accept(File pathname) {
			// TODO Auto-generated method stub
			return pathname.getName().toLowerCase().endsWith(".txt");
		}
	}
	
	public static void main(String[] args) throws IOException {
		String indexDir = "E:/myprogrammer/index";
		String dataDir = "E:/myprogrammer/lucene-6.0.0";
		
		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		
		int numIndexed;
		try{
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		}finally{
			indexer.close();
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Indexing "+ numIndexed + "files took "+(end-start)+ " millseconds");
	}
}
