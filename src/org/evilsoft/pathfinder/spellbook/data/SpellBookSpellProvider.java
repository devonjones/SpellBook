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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.evilsoft.pathfinder.spellbook.api.BaseApiHelper;
import org.evilsoft.pathfinder.spellbook.data.SpellBookSpellContract.SpellbookSpellEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class SpellBookSpellProvider extends ContentProvider {

	// The URI Matcher used by this content provider.
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private SpellBookDbHelper mOpenHelper;

	private static final int SPELLBOOK_SPELL = 200;
	private static final int SPELLBOOK_SPELL_ID = 201;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = SpellBookSpellContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, SpellBookSpellContract.PATH_SPELLBOOK + "/#/"
				+ SpellBookSpellContract.PATH_SPELLS, SPELLBOOK_SPELL);
		matcher.addURI(authority, SpellBookSpellContract.PATH_SPELLBOOK + "/#/"
				+ SpellBookSpellContract.PATH_SPELLS + "/#", SPELLBOOK_SPELL_ID);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new SpellBookDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor retCursor;
		List<String> args;
		switch (sUriMatcher.match(uri)) {
		// "spellbook/*/spells/*"
		case SPELLBOOK_SPELL_ID:
			Long spellbookId = Long.parseLong(uri.getPathSegments().get(
					uri.getPathSegments().size() - 3));
			Long spellId = ContentUris.parseId(uri);
			if (selection == null) {
				selection = "spellbook_id = ? AND _id = ?";
				args = new ArrayList<String>();
			} else {
				selection = selection + " AND spellbook_id = ? AND _id = ?";
				args = Arrays.asList(selectionArgs);
			}
			if (sortOrder == null) {
				sortOrder = "level, name";
			}
			args.add(spellbookId.toString());
			args.add(spellId.toString());
			selectionArgs = BaseApiHelper.toStringArray(args);
			retCursor = mOpenHelper.getReadableDatabase().query(
					SpellbookSpellEntry.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		// "spellbook/*/spells"
		case SPELLBOOK_SPELL:
			spellbookId = Long.parseLong(uri.getPathSegments().get(
					uri.getPathSegments().size() - 2));
			if (selection == null) {
				selection = "spellbook_id = ?";
				args = new ArrayList<String>();
			} else {
				selection = selection + " AND spellbook_id = ?";
				args = new ArrayList<String>();
				for (int i = 0; i < selectionArgs.length; i++) {
					args.add(selectionArgs[i]);
				}
			}
			args.add(spellbookId.toString());
			selectionArgs = BaseApiHelper.toStringArray(args);
			retCursor = mOpenHelper.getReadableDatabase().query(
					SpellbookSpellEntry.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
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

		switch (match) {
		case SPELLBOOK_SPELL:
			return SpellbookSpellEntry.CONTENT_TYPE;
		case SPELLBOOK_SPELL_ID:
			return SpellbookSpellEntry.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		switch (match) {
		case SPELLBOOK_SPELL: {
			long _id = db.insert(SpellbookSpellEntry.TABLE_NAME, null, values);
			if (_id > 0)
				returnUri = SpellbookSpellEntry.buildSpellbookSpellUri(
						values.getAsLong("spellbook_id"), _id);
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
		switch (match) {
		case SPELLBOOK_SPELL_ID:
			rowsDeleted = db.delete(SpellbookSpellEntry.TABLE_NAME,
					SpellbookSpellEntry._ID + " = '" + ContentUris.parseId(uri)
							+ "'", null);
			break;
		case SPELLBOOK_SPELL:
			rowsDeleted = db.delete(SpellbookSpellEntry.TABLE_NAME, selection,
					selectionArgs);
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

		switch (match) {
		case SPELLBOOK_SPELL:
			rowsUpdated = db.update(SpellbookSpellEntry.TABLE_NAME, values,
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
		switch (match) {
		case SPELLBOOK_SPELL:
			db.beginTransaction();
			int returnCount = 0;
			try {
				for (ContentValues value : values) {
					long _id = db.insert(SpellbookSpellEntry.TABLE_NAME, null,
							value);
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
