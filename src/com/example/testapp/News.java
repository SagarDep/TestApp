package com.example.testapp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class News extends Activity {
	private final String rss_feed = "http://paggebella.tumblr.com/rss";
	
	private ProgressDialog showProgress;
	private ArrayList<Post> postList;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		postList = new ArrayList<Post>();
		newsList = (ListView) findViewById(R.id.news_list);
		
		showProgress = ProgressDialog.show(News.this, "", "Laddar nyheter...");
		new LoadingTask().execute(rss_feed);
		
//		newsList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(postList.get(position).getUrl()));
//				startActivity(intent);
//			}
//		});
	}
	
	class LoadingTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			SAXHelper sh = null;
			
			try {
				sh = new SAXHelper(urls[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			sh.parseContent("");
			return "";
		}
		
		protected void onPostExecute(String s) {
			newsList.setAdapter(new EfficientAdapter(News.this, postList));
			showProgress.dismiss();
		}
	}
	
	public class SAXHelper {
		public HashMap<String, String> userList = new HashMap<String, String>();
		private URL url;

		public SAXHelper(String url) throws MalformedURLException {
			this.url = new URL(url);
		}

		public RSSHandler parseContent(String parseContent) {
			RSSHandler df = new RSSHandler();
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(df);
				xr.parse(new InputSource(url.openStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return df;
		}
	}
	
	class RSSHandler extends DefaultHandler {
		private Post currentPost = new Post();
		private StringBuffer chars = new StringBuffer();
		private boolean isItem = false;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) {
			this.chars = new StringBuffer();
			if(localName.equalsIgnoreCase("item")) {
				isItem = true;
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(localName.equalsIgnoreCase("title") && currentPost.getTitle() == null && isItem)
				currentPost.setTitle(chars.toString());
			
			if(localName.equalsIgnoreCase("pubDate") && currentPost.getPubDate() == null && isItem)
				currentPost.setPubDate(chars.toString());
			
			if(localName.equalsIgnoreCase("thumbnail") && currentPost.getThumbnail() == null && isItem)
				currentPost.setThumbnail(chars.toString());
			
			if(localName.equalsIgnoreCase("link") && currentPost.getUrl() == null && isItem)
				currentPost.setUrl(chars.toString());
			
			if(localName.equalsIgnoreCase("description") && currentPost.getDesc() == null && isItem)
				currentPost.setDesc(chars.toString());
			
			if(localName.equalsIgnoreCase("item") && isItem) {
				postList.add(currentPost);
				currentPost = new Post();
				isItem = false;
			}
		}
		
		@Override
		public void characters(char ch[], int start, int length) {
			chars.append(new String(ch, start, length));
		}
	}
	
	
}
