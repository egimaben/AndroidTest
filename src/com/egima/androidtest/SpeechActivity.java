package com.egima.androidtest;

import java.util.Locale;
 



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class SpeechActivity extends Activity implements
        TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */
 
    private TextToSpeech tts;
    private Button btnSpeak;
    private Button wifiCheck;
    private EditText txtText;
    public DbHelper db;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tts = new TextToSpeech(this, this);
 
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
 
        txtText = (EditText) findViewById(R.id.txtText);
        wifiCheck=(Button) findViewById(R.id.btnCheckWifi);
        wifiCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				boolean connected=Util.haveNetworkConnection(SpeechActivity.this);
//				Toast.makeText(SpeechActivity.this, connected?"true":"false", 1000).show();
//				Bundle b=new Bundle();
//				b.putSerializable("db", db);
				Intent intent=new Intent(SpeechActivity.this, CardListActivity.class);
//				intent.putExtra("db", b);
				startActivity(intent);
				
			}
		});
        // button on click event
        btnSpeak.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                speakOut();
            }
 
        });
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
                btnSpeak.setEnabled(true);
                speakOut();
            }
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
 
    }
 
    private void speakOut() {
 
        String text = txtText.getText().toString();
 
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
