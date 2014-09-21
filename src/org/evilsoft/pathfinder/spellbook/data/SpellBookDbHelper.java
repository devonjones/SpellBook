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
import org.evilsoft.pathfinder.spellbook.data.SpellBookSpellContract.SpellbookSpellEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for weather data.
 */
public class SpellBookDbHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database
	// version.
	private static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "user_data.db";

	public SpellBookDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		final String SQL_CREATE_SPELLBOOK_TABLE = "CREATE TABLE "
				+ SpellbookEntry.TABLE_NAME + " (" + SpellbookEntry._ID
				+ " INTEGER PRIMARY KEY," + SpellbookEntry.COLUMN_NAME
				+ " TEXT NOT NULL, " + SpellbookEntry.COLUMN_SPELL_CLASS
				+ " TEXT NOT NULL);";
		sqLiteDatabase.execSQL(SQL_CREATE_SPELLBOOK_TABLE);

		final String SQL_CREATE_SPELLBOOK_SPELLS_TABLE = "CREATE TABLE "
				+ SpellbookSpellEntry.TABLE_NAME + " ("
				+ SpellbookSpellEntry._ID + " INTEGER PRIMARY KEY, "
				+ SpellbookSpellEntry.COLUMN_SPELLBOOK_ID
				+ " INTEGER NOT NULL, " + SpellbookSpellEntry.COLUMN_URL
				+ " TEXT NOT NULL, " + SpellbookSpellEntry.COLUMN_NAME
				+ " TEXT NOT NULL, " + SpellbookSpellEntry.COLUMN_LEVEL
				+ " INTEGER NOT NULL);";
		sqLiteDatabase.execSQL(SQL_CREATE_SPELLBOOK_SPELLS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,
			int newVersion) {
	}
}
