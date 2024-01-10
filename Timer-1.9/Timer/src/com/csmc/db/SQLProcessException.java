package com.csmc.db;

public class SQLProcessException extends Exception {

		public SQLProcessException() {
			super();
		}

		public SQLProcessException(String str) {
			super(str);
		}

		public SQLProcessException(String str, Throwable nested) {
			super(str, nested);
		}
}
