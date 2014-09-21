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

public class SpellBookContract {
	public static final String CONTENT_AUTHORITY = "org.evilsoft.pathfinder.spellbook";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);
	public static final String PATH_SPELLBOOK = "spellbook";

	public static final class SpellbookEntry implements BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SPELLBOOK).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ CONTENT_AUTHORITY + "/" + PATH_SPELLBOOK;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ CONTENT_AUTHORITY + "/" + PATH_SPELLBOOK;

		public static final String TABLE_NAME = "spellbooks";

		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_SPELL_CLASS = "spell_class";

		public static Uri buildSpellbookUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
