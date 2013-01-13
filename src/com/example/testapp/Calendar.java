package com.example.testapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class Calendar extends Activity {

	private final String rss_feed = "http://paggebella.tumblr.com/cal/rss";
	private final long validTime = 300000L; // 5 minuter

	private static ArrayList<ScheduleItem> scheduleList = null;
	private static long lastUpdateTime = -1L;

	private ArrayList<Post> postList = null;
	private ProgressDialog showProgress;
	private ListView newsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);

		newsList = (ListView) findViewById(R.id.news_list);
		showProgress = ProgressDialog.show(Calendar.this, "", Utils.MSG_LOADING_SCHEDULE);

		long timeDiff = System.currentTimeMillis() - lastUpdateTime;

		if (scheduleList != null && timeDiff < validTime) {
			Log.i(Utils.TAG, "NEWS USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
			newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
			showProgress.dismiss();
		} else {
			postList = new ArrayList<Post>();
			new LoadingTask(getApplicationContext()).execute(rss_feed);
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
			}
			sh.parseContent("");

			return "";
		}

		protected void onPostExecute(String s) {
			switch (error) {
				case Utils.ECODE_NO_INTERNET_CONNECTION:
					showProgress.dismiss();
					if (scheduleList != null) {
						Log.i(Utils.TAG, "CALENDAR (no connection) USING CACHED VERSION");
						newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
						String errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
						Utils.showToast(context, errMsg, Toast.LENGTH_LONG);
					} else {
	
						SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
						
						try {
							scheduleList = (ArrayList<ScheduleItem>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_SCHEDULE, null));
							lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_SCHEDULE_DATE, -1L);
						} catch (IOException e) {
							Log.e(Utils.TAG, "CALENDAR retrieve_from_file IOException");
							e.printStackTrace();
						} catch (ClassCastException e) {
							Log.e(Utils.TAG, "CALENDAR retrieve_from_file ClassCastException");
							e.printStackTrace();
						}
	
						if (scheduleList != null) {
							Log.i(Utils.TAG, "CALENDAR (no connection)  USING STORED VERSION");
							newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
							String errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
							Utils.showToast(context, errMsg, Toast.LENGTH_LONG);
						} else {
							Log.i(Utils.TAG, "CALENDAR (no connection) NO DATA TO SHOW");
							Utils.showToast(context, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
						}
					}
					break;
				default:
					Log.i(Utils.TAG, "CALENDAR USING FRESHLY DOWNLOADED");
					scheduleList = processResponse(postList);
					newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
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
				editor.putString(Utils.PREFS_KEY_SCHEDULE, ObjectSerializer.serialize(scheduleList));
				editor.putLong(Utils.PREFS_KEY_SCHEDULE_DATE, lastUpdateTime);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "CALENDAR save_to_file IOException");
			}
			editor.commit();
		}

		private ArrayList<ScheduleItem> processResponse(ArrayList<Post> postList) {
			ArrayList<ScheduleItem> list = new ArrayList<ScheduleItem>();
			String post = Html.fromHtml(postList.remove(0).getDesc()).toString();

			String[] split = post.split("\n\n");
			int idCounter = 0;
			for (int i = 0; i < split.length; i++) {
				String[] inner = split[i].split("\n");
				String[] date = inner[0].split("<>");

				list.add(new CalDate(ScheduleItem.TYPE_CALDATE, idCounter, date[0], date[1]));
				idCounter++;

				for (int j = 1; j < inner.length; j++) {
					String[] desc = inner[j].split("<>");
					list.add(new CalDesc(ScheduleItem.TYPE_CALDESC, idCounter, desc[0], " " + desc[1], desc[2].trim()));
					idCounter++;
				}

				list.add(new CalSep(ScheduleItem.TYPE_CALSEP, idCounter));
				idCounter++;
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
		private StringBuffer chars = new StringBuffer();
		private Post currentPost = new Post(0);
		private boolean isItem = false;
		private int counter = 1;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) {
			this.chars = new StringBuffer();
			if (localName.equalsIgnoreCase("item")) {
				isItem = true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equalsIgnoreCase("description") && currentPost.getDesc() == null && isItem) currentPost.setDesc(chars.toString());

			if (localName.equalsIgnoreCase("item") && isItem) {
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
