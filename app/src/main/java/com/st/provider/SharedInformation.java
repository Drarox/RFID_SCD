
package com.st.provider;

import android.provider.BaseColumns;

public class SharedInformation {

	public SharedInformation() {
	}

	public static final class Livre implements BaseColumns {
		private Livre() {
		}

		public static final String ID = "ID";
		public static final String CODEBARRE = "CODEBARRE";
		public static final String TITRE = "TITRE";
		public static final String RECOLEMENT = "RECOLEMENT";
	}
}
