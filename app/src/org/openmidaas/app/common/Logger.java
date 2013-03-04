/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.app.common;

/**
 * Simple logging utility class
 */

import org.openmidaas.app.Settings;

import android.util.Log;

public class Logger{
	
	private static final int VERBOSE = 2; 	
	private static final int ASSERT = 7; 	
	private static final int ERROR = 4; 		
	private static final int WARNING = 8; 	
	private static final int INFO = 16; 		
	private static final int DEBUG = 32; 	
	
	public static void error(Class<?> cls, String message) {
		log(cls, ERROR, message);
	}
	
	public static void error(Class<?> cls, Exception e) {
		log(cls, ERROR, e.toString());
	}
	
	public static void verbose(Class<?> cls, String message) {
		log(cls, VERBOSE, message);
	}
	
	public static void verbose(Class<?> cls, Exception e) {
		log(cls, VERBOSE, e.toString());
	}
	public static void warn(Class<?> cls, String message) {
		log(cls, WARNING, message);
	}
	
	public static void warn(Class<?> cls, Exception e) {
		log(cls, WARNING, e.toString());
	}
	
	public static void info(Class<?> cls, String message) {
		log(cls, INFO, message);
	}
	
	public static void info(Class<?> cls, Exception e) {
		log(cls, INFO, e.toString());
	}
	
	public static void debug(Class<?> cls, String message) {
		log(cls, DEBUG, message);
	}
	
	public static void debug(Class<?> cls, Exception e) {
		log(cls, DEBUG, e.toString());
	}
	
	private static void log(Class<?> cls, int level, String msg) {
		if(shouldLog()) {
			switch(level) {
				case VERBOSE:
					Log.v(cls.getName(), msg);
					break;
				case ERROR:
					Log.e(cls.getName(), msg);
					break;
				case WARNING:
					Log.w(cls.getName(), msg);
					break;
				case INFO:
					Log.i(cls.getName(), msg);
					break;
				case DEBUG:
					Log.d(cls.getName(), msg);
					break;
				default:
					Log.v(cls.getName(), msg);	
					break;
			}
		}
	}
	
	private static boolean shouldLog() {
		return Settings.SHOULD_LOG;
	}
}
