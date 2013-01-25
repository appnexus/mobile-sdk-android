package com.appnexus.opensdk.test;

import com.appnexus.opensdk.HashingFunctions;

import junit.framework.TestCase;

public class TestHashingFunctions extends TestCase {

	public void testMd5() {
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", HashingFunctions.md5(""));
		assertEquals("3a730b72719bd56c4ec388e267322532", HashingFunctions.md5("hello, this is a random string"));
		assertEquals("84f9ba777a87f6088e82cf88cce77096", HashingFunctions.md5("shortstring"));
		assertEquals("5a7ef2bf21a8b48bf37698621687c91f", HashingFunctions.md5("longstringholyfuckmyleghurtsatonrightnowithinkimgoingtogoliedownifyourereadingthispleasebringmepainkillers"));
	}

	public void testSha1() {
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", HashingFunctions.sha1(""));
		assertEquals("3b949a91f7b8396dd26d4f21febc03280fc05e2b", HashingFunctions.sha1("hello, this is a random string"));
		assertEquals("789b192ca01998988b9936b2621d4d68b53e5652", HashingFunctions.sha1("shortstring"));
		assertEquals("abc86da545168a364878f102e6ee281e497062a8", HashingFunctions.sha1("longstringholyfuckmyleghurtsatonrightnowithinkimgoingtogoliedownifyourereadingthispleasebringmepainkillers"));
	}

}
