package com.egima.androidtest;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CardListActivity extends Activity implements
		TextToSpeech.OnInitListener, OnClickListener {

	private static final String TAG = "CardListActivity";
	private TimelineAdapter adapter;
	private ListView listView;
	DbHelper db;
	AppDataz data;
	String speech = "";
	public TextToSpeech tts;
	Cursor cursor;
	UpdaterService service;
	Button nameSearch;
	Button tweetSearch;
	EditText searchBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = (AppDataz) getApplication();
		data.setCardList(this);
		db = data.getDb();
		// addTweets();
		// Log.d("tweets", db.getAllContacts().toString());
		tts = new TextToSpeech(this, this);
		// service=new UpdaterService(this);
		startService(new Intent(this, UpdaterService.class));
		// db=(DbHelper)(getIntent().getExtras().get("db"));
		setContentView(R.layout.listview);
		listView = (ListView) findViewById(R.id.card_listView);
		nameSearch = (Button) findViewById(R.id.name_search);
		tweetSearch = (Button) findViewById(R.id.tweet_search);
		searchBox = (EditText) findViewById(R.id.search_word);

		listView.addHeaderView(new View(this));
		listView.addFooterView(new View(this));

		cursor = db.getAllCursor();
		startManagingCursor(cursor);
		adapter = new TimelineAdapter(getApplicationContext(), cursor);
		data.setAdapter(adapter);

		// for (int i = 0; i < 10; i++) {
		// Card card = new Card("Card " + (i+1) + " Line 1", "Card " + (i+1) +
		// " Line 2");
		// cardArrayAdapter.add(card);
		// }
		listView.setAdapter(adapter);
	}

	public void refreshCursor() {
		// Get the data from the database
		cursor = db.getAllCursor();
		startManagingCursor(cursor);
		// Set up the adapter
		// adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM,
		// TO); //
		adapter = new TimelineAdapter(this, cursor);
		listView.setAdapter(adapter); //
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Get the data from the database
		cursor = db.getAllCursor();
		startManagingCursor(cursor);
		// Set up the adapter
		// adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM,
		// TO); //
		adapter = new TimelineAdapter(this, cursor);
		listView.setAdapter(adapter); //
	}

	public void addTweets() {
//		db.addTweet(new Tweet("ben", "01", "img1", "hi there"));
//		db.addTweet(new Tweet("ben1", "012", "img11", "hi therwwwe"));

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				// btnSpeak.setEnabled(true);
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	private void speakOut() {

		// String text = txtText.getText().toString();

		tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
	}

	final String[] FROM = { DbHelper.KEY_ID, DbHelper.KEY_IMG,
			DbHelper.KEY_NAME, DbHelper.KEY_TWEET }; //
	final int[] TO = { R.id.user_id, R.id.avatar, R.id.user, R.id.tweet }; //

	public class TimelineAdapter extends SimpleCursorAdapter { //

		Context context;

		// Constructor
		public TimelineAdapter(Context context, Cursor c) { //

			super(context, R.layout.row, c, FROM, TO);
			this.context = context;
			startManagingCursor(c);
		}

		// This is where the actual binding of a cursor to view happens
		@Override
		public void bindView(View row, Context context, Cursor cursor) { //
			super.bindView(row, context, cursor);

			// manually bind image
//			byte[] avatar_bytes = cursor.getBlob(cursor
//					.getColumnIndex(DbHelper.KEY_IMG)); //
//			ByteArrayInputStream imageStream = new ByteArrayInputStream(avatar_bytes);
//			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true; 
//			Bitmap theImage = BitmapFactory.decodeStream(imageStream,null,options);
//			imageStream.reset();
			String name = cursor.getString(cursor
					.getColumnIndex(DbHelper.KEY_NAME));
			String tweet = cursor.getString(cursor
					.getColumnIndex(DbHelper.KEY_TWEET));
			final String text = name + " tweeted, " + tweet;
			ImageView avatar = (ImageView) row.findViewById(R.id.avatar); //
//			avatar.setImageBitmap(theImage);
			avatar.setImageResource(R.drawable.ic_launcher);
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// Toast.makeText(CardListActivity.this, text,1000 ).show();
					speech = text;
					speakOut();

				}
			});
		}
	}

	@Override
	public void onClick(View arg0) {
		String searchword = String.valueOf(searchBox.getText());
		if (searchword == null) {
			Toast.makeText(this, "no search params", Toast.LENGTH_SHORT);
			return;
		}
		Cursor cursor;
		int id = arg0.getId();
		switch (id) {
		case R.id.name_search:
			Toast.makeText(this, "searching users", Toast.LENGTH_SHORT).show();
			;
			cursor = db.search(searchword, DbHelper.KEY_NAME);
			startManagingCursor(cursor);
			adapter = new TimelineAdapter(this, cursor);
			listView.setAdapter(adapter);

			break;
		case R.id.tweet_search:
			Toast.makeText(this, "searching tweets", Toast.LENGTH_SHORT).show();
			cursor = db.search(searchword, DbHelper.KEY_TWEET);
			startManagingCursor(cursor);
			adapter = new TimelineAdapter(this, cursor);
			listView.setAdapter(adapter);

			break;
		default:
			break;
		}

	}
}
