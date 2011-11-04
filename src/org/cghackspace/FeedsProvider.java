package org.cghackspace;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class FeedsProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "org.hackspace.Feeds";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/feeds");

	public static final String TABLE_NAME = "TABLE_FEEDS";
	public static final String ID = "id";
	public static final String AUTHOR = "author";
	public static final String FROM_NETWORK = "network";
	public static final String CONTENT = "content";
	public static final String TIMESTAMP = "timestamp";

	public static final int FEEDS = 1;
	public static final int FEED_ID = 2;
	public static final int AUTHORS = 3;
	public static final int CONTENTS = 4;

	private SQLiteDatabase feedsDB;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "feeds", FEEDS);
		uriMatcher.addURI(PROVIDER_NAME, "feeds/#", FEED_ID);
		uriMatcher.addURI(PROVIDER_NAME, "feeds/authors", AUTHORS);
		uriMatcher.addURI(PROVIDER_NAME, "feeds/contents", CONTENTS);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public static final String DB_NAME = "feeds.db";
		public static final int DB_VERSION = 1;

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + FeedsProvider.TABLE_NAME + " ("
					+ FeedsProvider.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ FeedsProvider.AUTHOR + " TEXT," + FeedsProvider.FROM_NETWORK
					+ " TEXT," + FeedsProvider.TIMESTAMP + " INTEGER,"
					+ FeedsProvider.CONTENT + " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + FeedsProvider.TABLE_NAME);
			onCreate(db);
		}

	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		// ---get all books---
		case FEEDS:
			return "vnd.android.cursor.dir/org.hackspace.feeds ";
			// ---get a particular book---
		case FEED_ID:
			return "vnd.android.cursor.item/org.hackspace.feeds ";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = feedsDB.insert(TABLE_NAME, "", values);
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		feedsDB = dbHelper.getWritableDatabase();
		return (feedsDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(TABLE_NAME);

		switch (uriMatcher.match(uri)) {
		case FEED_ID:
			sqlBuilder.appendWhere(ID + " = " + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		if (sortOrder == null || sortOrder == "")
			sortOrder = TIMESTAMP;

		Cursor c = sqlBuilder.query(feedsDB, projection, selection,
				selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
