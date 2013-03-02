package com.example.testapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class News extends Activity {
	private final String REFRESH_MSG_CONNECTION_FAILURE	= "FAIL";
	private final String REFRESH_MSG_REFRESH_NOT_NEEDED	= "NOT_NEEDED";
	
	private final String rss_feed				= "http://paggebella.tumblr.com/rss";
	private final long validTime				= 300000L;
	
	private static ArrayList<Post> postList		= null;
	
	private ArrayList<Post> backupPostList		= null;
	
	private static long lastUpdateTime			= -1L;
	private static String lastUpdateDate		= null;
	private ProgressDialog showProgress;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		newsList = (ListView) findViewById(R.id.news_list);
		showProgress = ProgressDialog.show(News.this, "", Utils.MSG_LOADING_NEWS);
		
		new NewsTask(News.this, showProgress).execute("");
	}
	
	class NewsTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 0;
		private final int MSG_USE_CACHED_DATA		= 1;
		private final int MSG_LOAD_FROM_FILE		= 2;
		private final int MSG_ERROR_NO_DATA			= 3;
		private final int MSG_ERR_USE_CACHED_DATA	= 4;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;

		public NewsTask(Activity a, ProgressDialog pd) {
			this.activity = a;
			this.showProgress = pd;
			this.array = null;
		}
		
		@Override
		protected void onPreExecute() {
			showProgress.show();

			long timeDiff = System.currentTimeMillis() - lastUpdateTime;
			
			if(postList != null && timeDiff < validTime) {
				Log.i(Utils.TAG, "NEWS USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
				newsList.setAdapter(new NewsAdapter(News.this, postList));
				showProgress.dismiss();
			} else {
				backupPostList = postList;
				postList = new ArrayList<Post>();
				new LoadingTask(getApplicationContext()).execute(rss_feed);
			}
			
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			int msg = -1;
			/*
			 Prio: Cache -> Fil -> Internet
			 Första gången appen öppnas laddas alla nyheter ner
			 Därefter laddas endast de saknade nyheterna ner och fyller på listan som sparas i fil
			 */
			if(lastUpdateDate != null) { // CACHE AVAILABLE
				String updateDate = refreshNeeded();
				if(updateDate == REFRESH_MSG_REFRESH_NOT_NEEDED) {
					msg = MSG_USE_CACHED_DATA;
				} else if(updateDate == REFRESH_MSG_CONNECTION_FAILURE) {
					msg = MSG_ERR_USE_CACHED_DATA;
				} else {
					this.array = updateNewsInfo(updateDate);
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else
						msg = MSG_ERR_USE_CACHED_DATA;
				}
			} else { // CACHE EMPTY
				loadFromFile(true);
				if(lastUpdateDate != null) {
					String updateDate = refreshNeeded();
					if(updateDate == REFRESH_MSG_REFRESH_NOT_NEEDED || updateDate == REFRESH_MSG_CONNECTION_FAILURE)
						msg = MSG_LOAD_FROM_FILE;
					else {
						this.array = updateNewsInfo(updateDate);
						if(this.array != null)
							msg = MSG_REFRESH_FROM_DOWNLOAD;
						else
							msg = MSG_LOAD_FROM_FILE;
					}
				} else {
					this.array = updateNewsInfo(null);
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else 
						msg = MSG_ERROR_NO_DATA;
				}
			}
			return msg;
		}
		
		@Override
		protected void onPostExecute(final Integer msg) {
			String errMsg;
			switch(msg) {
				case MSG_REFRESH_FROM_DOWNLOAD:
					Log.i(Utils.TAG, "NEWS USING FRESHLY DOWNLOADED");
					initFromDownload();
					//FIXME
					break;
				case MSG_ERR_USE_CACHED_DATA:
					break;
				case MSG_USE_CACHED_DATA:
					break;
				case MSG_LOAD_FROM_FILE:
					break;
				default:
					break;
			}
		}

		private String refreshNeeded() {
			// TODO Auto-generated method stub
			return null;
		}
		
		private JSONArray updateNewsInfo(String updateDate) {
			// TODO Auto-generated method stub
			return null;
		}
		
		private void initFromDownload() {
			// TODO Auto-generated method stub
			
		}
		
		private void saveToFile() {
			
		}
		
		private void loadFromFile(boolean b) {
			
		}
		
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
				error = Utils.ECODE_NO_INTERNET_CONNECTION;
				return "";
			}
			sh.parseContent("");
			
			return "";
		}
		
		@SuppressWarnings("unchecked")
		protected void onPostExecute(String s) {
			switch(error) {
				case Utils.ECODE_NO_INTERNET_CONNECTION:
					showProgress.dismiss();
					if(backupPostList != null) {
						postList = backupPostList;
						backupPostList = null;
						Log.v(Utils.TAG, "Ingen connection Läser in gammalt");
						newsList.setAdapter(new NewsAdapter(News.this, postList));
						String errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
						Utils.showToast(context, errMsg, Toast.LENGTH_LONG);
					} else {
						SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
						try {
							postList = (ArrayList<Post>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_NEWS, null));
							lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_NEWS_DATE, -1L);
						} catch (IOException e) {
							postList = null;
							lastUpdateTime = -1L;
							Log.e(Utils.TAG, "NEWS retrieve_from_file IOException");
							e.printStackTrace();
						} catch (ClassCastException e) {
							postList = null;
							lastUpdateTime = -1L;
							Log.e(Utils.TAG, "NEWS retrieve_from_file ClassCastException");
							e.printStackTrace();
						}
	
						if (postList != null) {
							Log.i(Utils.TAG, "NEWS (no connection)  USING STORED VERSION");
							newsList.setAdapter(new NewsAdapter(News.this, postList));
							String errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
							Utils.showToast(context, errMsg, Toast.LENGTH_LONG);
						} else {
							Log.i(Utils.TAG, "NEWS (no connection) NO DATA TO SHOW");
							Utils.showToast(context, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
						}
					}
					break;
				default:
					Log.i(Utils.TAG, "NEWS USING FRESHLY DOWNLOADED");
					newsList.setAdapter(new NewsAdapter(News.this, postList));
					showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					break;
			}
		}
		
		private void saveToFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			try {
				editor.putString(Utils.PREFS_KEY_NEWS, ObjectSerializer.serialize(postList));
				editor.putLong(Utils.PREFS_KEY_NEWS_DATE, lastUpdateTime);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "NEWS save_to_file IOException");
			}
			editor.commit();
		}
		
		public void raiseError(int errorCode) {
			this.error = errorCode;
		}
	}
	
	class SAXHelper {
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
				task.raiseError(Utils.ECODE_NO_INTERNET_CONNECTION);
				e.printStackTrace();
			}
			return df;
		}
	}
	
	class RSSHandler extends DefaultHandler {
		private StringBuffer chars = new StringBuffer();
		private Post currentPost = new Post(0);
		private boolean isItem = false;
		private int counter = 1;
		
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
				currentPost = new Post(counter);
				counter++;
				isItem = false;
			}
		}
		
		@Override
		public void characters(char ch[], int start, int length) {
			chars.append(new String(ch, start, length));
		}
	}
}
