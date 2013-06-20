/*
 *    Copyright 2013 APPNEXUS INC
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingFunctions {
	static public String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			return HashingFunctions.byteToHex(messageDigest);
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	static public String sha1(String s) {
		try {
			// Create SHA-1 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			return HashingFunctions.byteToHex(messageDigest);
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	private static String byteToHex(byte[] messageDigest) {
		StringBuilder buf = new StringBuilder();
		for (byte b : messageDigest) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
						: (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}
