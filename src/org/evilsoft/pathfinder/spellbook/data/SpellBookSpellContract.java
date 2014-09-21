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

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class SpellBookSpellContract {
	public static final String CONTENT_AUTHORITY = "org.evilsoft.pathfinder.spellbook.spell";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);
	public static final String PATH_SPELLBOOK = "spellbook";
	public static final String PATH_SPELLS = "spells";
	public static final String PATH_FULL = "full";

	/* Inner class that defines the table contents of the spells table */
	public static final class SpellbookSpellEntry implements BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SPELLBOOK).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ CONTENT_AUTHORITY + "/" + PATH_SPELLS;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ CONTENT_AUTHORITY + "/" + PATH_SPELLS;

		public static final String TABLE_NAME = "spellbook_spells";

		public static final String COLUMN_SPELLBOOK_ID = "spellbook_id";
		public static final String COLUMN_URL = "url";
		public static final String COLUMN_LEVEL = "level";
		public static final String COLUMN_NAME = "name";

		public static Uri buildSpellbookSpellsUri(long spellbookId) {
			return ContentUris
					.withAppendedId(SpellbookSpellEntry.CONTENT_URI,
							spellbookId).buildUpon().appendPath(PATH_SPELLS)
					.build();
		}

		public static Uri buildSpellbookSpellUri(long spellbookId, long spellId) {
			return ContentUris
					.withAppendedId(SpellbookSpellEntry.CONTENT_URI,
							spellbookId).buildUpon().appendPath(PATH_SPELLS)
					.appendPath(((Long) spellId).toString()).build();
		}
	}
}
