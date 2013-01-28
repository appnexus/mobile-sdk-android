package com.appnexus.opensdk;

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

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
