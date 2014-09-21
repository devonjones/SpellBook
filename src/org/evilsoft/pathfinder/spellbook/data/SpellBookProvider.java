/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evilsoft.pathfinder.spellbook.data;

import org.evilsoft.pathfinder.spellbook.data.SpellBookContract.SpellbookEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class SpellBookProvider extends ContentProvider {
	private static final String TAG = "SpellBookProvider";

	// The URI Matcher used by this content provider.
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private SpellBookDbHelper mOpenHelper;

	private static final int SPELLBOOK = 100;
	private static final int SPELLBOOK_ID = 101;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = SpellBookContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, SpellBookContract.PATH_SPELLBOOK, SPELLBOOK);
		matcher.addURI(authority, SpellBookContract.PATH_SPELLBOOK + "/#",
				SPELLBOOK_ID);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		mOpenHelper = new SpellBookDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor retCursor;
		Log.d(TAG, "query");
		switch (sUriMatcher.match(uri)) {
		// "spellbook/*"
		case SPELLBOOK_ID: {
			retCursor = mOpenHelper.getReadableDatabase().query(
					SpellbookEntry.TABLE_NAME,
					projection,
					SpellbookEntry._ID + " = '" + ContentUris.parseId(uri)
							+ "'", null, null, null, sortOrder);
			break;
		}
		// "spellbook"
		case SPELLBOOK: {
			retCursor = mOpenHelper.getReadableDatabase().query(
					SpellbookEntry.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Override
	public String getType(Uri uri) {

		// Use the Uri Matcher to determine what kind of URI this is.
		final int match = sUriMatcher.match(uri);

		Log.d(TAG, "getType");
		switch (match) {
		case SPELLBOOK:
			return SpellbookEntry.CONTENT_TYPE;
		case SPELLBOOK_ID:
			return SpellbookEntry.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		Log.d(TAG, "insert");
		switch (match) {
		case SPELLBOOK: {
			long _id = db.insert(SpellbookEntry.TABLE_NAME, null, values);
			if (_id > 0)
				returnUri = SpellbookEntry.buildSpellbookUri(_id);
			else
				throw new android.database.SQLException(
						"Failed to insert row into " + uri);
			break;
		}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;
		Log.d(TAG, "delete");
		switch (match) {
		case SPELLBOOK:
			rowsDeleted = db.delete(SpellbookEntry.TABLE_NAME, selection,
					selectionArgs);
			break;
		case SPELLBOOK_ID:
			rowsDeleted = db.delete(SpellbookEntry.TABLE_NAME,
					SpellbookEntry._ID + " = '" + ContentUris.parseId(uri)
							+ "'", null);
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Because a null deletes all rows
		if (selection == null || rowsDeleted != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;

		Log.d(TAG, "update");
		switch (match) {
		case SPELLBOOK:
			rowsUpdated = db.update(SpellbookEntry.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		if (rowsUpdated != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Log.d(TAG, "bulkInsert");
		switch (match) {
		case SPELLBOOK:
			db.beginTransaction();
			int returnCount = 0;
			try {
				for (ContentValues value : values) {
					long _id = db
							.insert(SpellbookEntry.TABLE_NAME, null, value);
					if (_id != -1) {
						returnCount++;
					}
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return returnCount;
		default:
			return super.bulkInsert(uri, values);
		}
	}
}
