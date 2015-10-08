package com.egima.androidtest;


import com.egima.androidtest.CardListActivity.TimelineAdapter;

import android.app.Application;
import android.database.Cursor;
import android.util.Log;

public class AppDataz extends Application {
	private static final String TAG = AppDataz.class.getSimpleName();
	DbHelper db;
	TimelineAdapter adapter;
	CardListActivity cardList;
	public CardListActivity getCardList() {
		return cardList;
	}

	public void setCardList(CardListActivity cardList) {
		this.cardList = cardList;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreated");
		db=new DbHelper(this);

	}
	
	public TimelineAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(TimelineAdapter adapter) {
		this.adapter = adapter;
	}


	public DbHelper getDb() {
		
		return db;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.i(TAG, "onTerminated");

	}

}
