package com.egima.androidtest;

import java.util.List;

import com.egima.androidtest.CardListActivity.TimelineAdapter;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class UpdaterService extends Service {
	public static final String TAG = "UpdaterService";
	static final int DELAY = 60000; // a minute
	private boolean runFlag = false; //
	private Updater updater;
	AppDataz data;
	DbHelper db;
//	SQLiteDatabase db;
	private final static String CONSUMER_KEY = "JvHlHhZ33mYFMaOwkzlptYnvs";
	private final static String CONSUMER_KEY_SECRET = "hWH7gWOkDJgbc5Ac0YmkCNkZyL8gDTWOECyEio1yptY0VcFUmD";
	private final static String ACCESS_TOKEN="2230264626-aIdV1tUdbM6DWWSqKiUB1VyBfqrTC8e6jqL1dam";
	private final static String ACCESS_TOKEN_SECRET="EpewAGssgv4Rilc0zctsiv0sD20eEAdhF9Wp4z7Rvs8Vf";
	Activity con;
//public UpdaterService(Activity context) {
//	// TODO Auto-generated constructor stub
//	this.con=context;
//}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		data=(AppDataz) getApplication();
		db=data.getDb();
		Log.d(TAG, "oncreated");
		this.updater = new Updater();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "ondestroyed");
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onstarted");
		this.runFlag = true;
		this.updater.start();
		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void refreshCursor(){
		data.getCardList().refreshCursor();
	}
	/** * Thread that performs the actual update from the online service */
	private class Updater extends Thread { //
//		List<Twitter.Status> timeline;

		public Updater() {
			super("UpdaterService-Updater"); // }
			
		}

		@Override
		public void run() { //
			UpdaterService updaterService = UpdaterService.this;
	target:		while (updaterService.runFlag) {
				Log.d(TAG, "Running background thread");
				try {
//					if(Util.haveNetworkConnection(data.getCardList()))
					fetchTimeLine(); //
//					else{
//						data.getCardList().runOnUiThread(new Runnable() {
//						    public void run() {
//						        Toast.makeText(data, "u have no net", Toast.LENGTH_SHORT).show();
//						       /
////						        data.getCardList().refreshCursor();
//						    }
//						});}
						Log.d(TAG, "finished running background thread");
					
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
		public void fetchTimeLine(){
			twitter4j.Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);

			// here's the difference
			String accessToken = ACCESS_TOKEN;
			String accessTokenSecret = ACCESS_TOKEN_SECRET;
			AccessToken oathAccessToken = new AccessToken(accessToken,
					accessTokenSecret);

			twitter.setOAuthAccessToken(oathAccessToken);
			// end of difference

//			try {
//				twitter.updateStatus("Hi, testing twitter4j from real device");
//			} catch (TwitterException e1) {
//				Log.d(TAG,"failed to post");
//				e1.printStackTrace();
//			}

			Log.d(TAG,"Timeline messages/n");

			// I'm reading your timeline
			Paging paging=new Paging(1, 5);
			ResponseList<Status> list=null;
			try {
//				if(Util.haveNetworkConnection(data.getCardList()))
				list = twitter.getHomeTimeline(paging);
				
//				}
			} catch (TwitterException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			Log.d(TAG, "dbsize befor truncating  "+db.getAllContacts().toString());
			

			//truncate existing table
			db.deleteAll();
			Log.d(TAG, "finished dropping all");
			Log.d(TAG,"current db size "+db.getAllContacts().toString());
			for (Status each : list) {
				String id=String.valueOf(each.getUser().getId());
				String img=each.getUser().getProfileImageURL();
				String name=each.getUser().getName();
				String tweet= each.getText();
				String txt="Sent by: @" + each.getUser().getScreenName()
						+ " - " + each.getUser().getName() + "\n" + each.getText()
						+ "\n"+"Profile img"+each.getUser().getProfileImageURL();
//				Log.d(TAG,txt);
				Log.d(TAG, "adding to db ");
//				String imageBytes=Util.getImageBytes(img);
				db.addTweet(new Tweet(name, id, img, tweet));
				Log.d(TAG, "finished adding ");

				
				
			}
//			Log.d(TAG, "dbsize after adding  "+db.getAllContacts().toString());
//			Log.d(TAG, "refreshing cursor");
			data.getCardList().runOnUiThread(new Runnable() {
			    public void run() {
			        Toast.makeText(data, "new tweets received", Toast.LENGTH_SHORT).show();
			        data.getCardList().refreshCursor();
			    }
			});
			
			

		}
	}
public void say(String str){}
	
}
