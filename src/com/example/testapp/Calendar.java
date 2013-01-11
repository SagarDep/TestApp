package com.example.testapp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.example.testapp.News.LoadingTask;
import com.example.testapp.News.RSSHandler;
import com.example.testapp.News.SAXHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class Calendar extends Activity {

	private final String rss_feed = "http://paggebella.tumblr.com/cal/rss";
	
	private ProgressDialog showProgress;
	private ArrayList<Post> postList;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);
		
		postList = new ArrayList<Post>();
		newsList = (ListView) findViewById(R.id.news_list);
		
		showProgress = ProgressDialog.show(Calendar.this, "", "Laddar schema...");
		new LoadingTask(getApplicationContext()).execute(rss_feed);
	}
	
	class LoadingTask extends AsyncTask<String, Void, String> {
		private Context context;
		private int error = Utils.ECODE_NO_ERROR;

		public LoadingTask(Context context) {
			this.context = context;
		}
		
		@Override
		protected String doInBackground(String... urls) {
			SAXHelper sh = null;
			
			try {
				sh = new SAXHelper(this, urls[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			sh.parseContent("");
			
			return "";
		}
		
		protected void onPostExecute(String s) {
			switch(error) {
				case Utils.ECODE_NO_INTERNET_CONNECTION:
					showProgress.dismiss();
					Utils.showToast(context, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					break;
				default:
					ArrayList<ScheduleItem> list = processResponse(postList);
					newsList.setAdapter(new CalAdapter(Calendar.this, list));
					showProgress.dismiss();
					break;
			}
		}

		private ArrayList<ScheduleItem> processResponse(ArrayList<Post> postList) {
			ArrayList<ScheduleItem> list = new ArrayList<ScheduleItem>();
			String post = Html.fromHtml(postList.remove(0).getDesc()).toString();
			
			String[] split = post.split("\n\n");

			for (int i = 0; i < split.length; i++) {
				String[] inner = split[i].split("\n");
				String[] date = inner[0].split("<>");
				
				list.add(new CalDate(ScheduleItem.TYPE_CALDATE, date[0], date[1]));
				
				for (int j = 1; j < inner.length; j++) {
					String[] desc = inner[j].split("<>");
					list.add(new CalDesc(ScheduleItem.TYPE_CALDESC, desc[0], " " + desc[1], desc[2].trim()));
				}
				
				list.add(new CalSep(ScheduleItem.TYPE_CALSEP));
			}
			return list;
		}

		public void raiseError(int errorCode) {
			this.error = errorCode;
		}
	}
	
	class SAXHelper {
		public HashMap<String, String> userList = new HashMap<String, String>();
		private URL url;
		private LoadingTask task;

		public SAXHelper(LoadingTask loadingTask, String url) throws MalformedURLException {
			this.task = loadingTask;
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
				Log.i(Utils.TAG, Utils.EMSG_NO_INTERNET_CONNECTION);
				task.raiseError(Utils.ECODE_NO_INTERNET_CONNECTION);
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
