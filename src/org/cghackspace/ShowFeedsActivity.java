package org.cghackspace;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShowFeedsActivity extends Activity {
	
	private static final String TAG = ShowFeedsActivity.class.getSimpleName();
	private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
		
		@Override
		@SuppressWarnings("unchecked")
		public void onReceive(Context context, Intent intent) {
			List<String> tweets = (List<String>) intent.getSerializableExtra("tweets");
			fillTweets(tweets);
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        IntentFilter filter = new IntentFilter("TWEET_RETURN");
        this.registerReceiver(notificationReceiver, filter);
        this.startService(new Intent(this, TwitterService.class));
    }
    
    protected void fillTweets(List<String> tweets) {
    	ListView feeds = (ListView) findViewById(R.id.feeds);
    	ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.entry_view, tweets);
    	feeds.setAdapter(adapter);
	}

	@Override
    protected void onStart() {
    	Log.d(TAG, "iniciei");
		Toast.makeText(this, "Loading, please wait...", Toast.LENGTH_LONG).show();
		super.onStart();
    }
    
    @Override
    protected void onStop() {
    	Log.d(TAG, "parei!");
    	Toast.makeText(this, "Pausing", Toast.LENGTH_LONG).show();
    	super.onPause();
    }
}