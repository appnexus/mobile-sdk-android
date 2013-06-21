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

package com.appnexus.opensdk;

public interface AdRequester {
	/**
	 * Called when the request made by the requester fails.
	 * 
	 * @param request
	 */
	public void failed(AdRequest request);

	/**
	 * Called when a response is received
	 * 
	 * @param response
	 */
	public void onReceiveResponse(AdResponse response);
}
