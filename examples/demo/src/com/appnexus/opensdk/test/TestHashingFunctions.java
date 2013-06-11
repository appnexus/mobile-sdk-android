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

package com.appnexus.opensdk.test;

import com.appnexus.opensdk.utils.HashingFunctions;

import junit.framework.TestCase;

public class TestHashingFunctions extends TestCase {

	public void testMd5() {
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", HashingFunctions.md5(""));
		assertEquals("3a730b72719bd56c4ec388e267322532", HashingFunctions.md5("hello, this is a random string"));
		assertEquals("84f9ba777a87f6088e82cf88cce77096", HashingFunctions.md5("shortstring"));
		assertEquals("d8fa883ffd2495723300e395aafc47d6", HashingFunctions.md5("thisisareallylongPCstringbecausekarlobjectedtomyuseofexpletivesincodethatmayonedaybeopensource"));
	}

	public void testSha1() {
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", HashingFunctions.sha1(""));
		assertEquals("3b949a91f7b8396dd26d4f21febc03280fc05e2b", HashingFunctions.sha1("hello, this is a random string"));
		assertEquals("789b192ca01998988b9936b2621d4d68b53e5652", HashingFunctions.sha1("shortstring"));
		assertEquals("38e3a45be637d191e2d969884d6af93b9f0108c9", HashingFunctions.sha1("thisisareallylongPCstringbecausekarlobjectedtomyuseofexpletivesincodethatmayonedaybeopensource"));
	}

}
