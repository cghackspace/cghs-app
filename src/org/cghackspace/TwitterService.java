package org.cghackspace;

import java.util.LinkedList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class TwitterService extends Service {

	private static final String TAG = TwitterService.class.getSimpleName();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Starting service");
		// The factory instance is re-useable and thread safe.
		Twitter twitter = new TwitterFactory().getInstance();
		Query query = new Query("cghackspace");
		QueryResult result;
		LinkedList<String> tweets;
		Intent toReturn;
		try {
			result = twitter.search(query);

			tweets = new LinkedList<String>();
			toReturn = new Intent("TWEET_RETURN");
			for (Tweet tweet : result.getTweets()) {
				ContentValues values = new ContentValues();
				values.put(FeedsProvider.AUTHOR, tweet.getFromUser());
				values.put(FeedsProvider.FROM_NETWORK, "Twitter");
				values.put(FeedsProvider.CONTENT, tweet.getText());
				// values.put(FeedsProvider.TIMESTAMP, tweet.getCreatedAt());
				Uri uri = getContentResolver().insert(
						Uri.parse("content://org.hackspace.Feeds/feeds"),
						values);
				tweets.add(tweet.getFromUser() + ":" + tweet.getText());
			}
			Log.i(TAG, "tweets found: " + tweets.size());
			toReturn.putExtra("tweets", tweets);
			this.sendBroadcast(toReturn);
		} catch (TwitterException e) {
			Log.w(TAG, "Could not retrieve tweets!", e);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
