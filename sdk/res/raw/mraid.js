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

(function() {

	// Set up some variables
	var mraid = window.mraid = {};
	mraid.util = {};
	var listeners = [];
	listeners['ready']=[];
	listeners['error']=[];
	listeners['stateChange']=[];
	listeners['viewableChange']=[];
	listeners['sizeChange']=[];
	var state='loading'; //Can be loading, default, expanded, hidden, or resized
	var placement_type='inline';
	var is_viewable=false;
	var expand_properties={width:-1, height:-1, useCustomClose:false, isModal:true};
	var orientation_properties={allowOrientationChange:true, forceOrientation:"none"};
	var resize_properties={width:-1, height:-1, offsetX: 0, offsetY: 0, customClosePosition: 'top-right', allowOffscreen: true};
	var screen_size={};
	var max_size={};
	var default_position={};
	var current_position={};

	// ----- MRAID AD API FUNCTIONS -----

	// getVersion() returns string '2.0'
	mraid.getVersion=function(){ return '2.0'};

	// getVendor() returns string 'appnexus'
	mraid.getVendor=function(){ return 'appnexus'};

	/** Adds a listener to a specific event. For example, a function onReady might be defined, and then mraid.addEventListener('ready', onReady)
	  * is called. When the ready event is fired, onReady will be called.
	  * Events 'error', 'viewableChange', 'stateChange', have parameters.
         */
	mraid.addEventListener=function(event_name, method){
		if(listeners[event_name].indexOf(method) > -1) return; // Listener is already registered
		listeners[event_name].push(method);
	};

	// Removes a listener from the registry
	mraid.removeEventListener=function(event_name, method){
	    //If no method name is given, remove all listeners from event
	    if(method == null){
	        listeners[event_name].length=0;
	        return;
	    }

		var method_index = listeners[event_name].indexOf(method);
		if(method_index > -1){ //Don't try to remove unregistered listeners
			listeners[event_name].splice(method_index,1);
		}else{
		    mraid.util.errorEvent("An unregistered listener was requested to be removed.", "mraid.removeEventListener()")
		}
	};

	//returns 'loading', 'default', 'expanded', or 'hidden'
	mraid.getState=function(){
		return state;
	};

	//returns 'inline' or 'interstitial'
	mraid.getPlacementType=function(){
		return placement_type;
	};

	//returns true or false
	mraid.isViewable=function(){
		return is_viewable;
	};

	// ----- MRAID JS TO NATIVE FUNCTIONS -----

	//Closes an expanded ad or hides an ad in default state
	mraid.close=function(){
		switch(mraid.getState()){
		case 'loading':
			mraid.util.errorEvent("mraid.close() called while state is 'loading'.", "mraid.close()");
			break;
		case 'default':
			window.open("mraid://close/");
			mraid.util.stateChangeEvent('hidden');
			break;
		case 'expanded':
			window.open("mraid://close/");
			mraid.util.stateChangeEvent('default');
			break;
		case 'hidden':
			mraid.util.errorEvent("mraid.close() called while ad was already hidden", "mraid.close()");
			break;
        case 'resized':
            window.open("mraid://close/");
            mraid.util.stateChangeEvent('default');
		}
	};

	// Expands a default state ad, or unhides a hidden ad. Optionally takes a URL to load in the expanded view
	mraid.expand=function(url){
		switch(mraid.getState()){
		case 'loading':
			mraid.util.errorEvent("mraid.expand() called while state is 'loading'.", "mraid.expand()");
			break;
		case 'default':
            if((expand_properties.height>0 && expand_properties.width>0) && (expand_properties.height < current_position.height || expand_properties.width < current_position.width)){
                mraid.util.errorEvent("Can't expand to a size smaller than the default size.", "mraid.expand()");
                return;
            }
			window.open("mraid://expand/"+"?w="+mraid.getExpandProperties().width+"&h="+mraid.getExpandProperties().height+"&useCustomClose="+mraid.getExpandProperties().useCustomClose+(url!=null ? "&url="+url:""));
			mraid.util.stateChangeEvent('expanded');
			if(url!=null){
			    window.open(url);
			}
			break;
		case 'expanded':
			mraid.util.errorEvent("mraid.expand() called while state is 'expanded'.", "mraid.expand()");
			break;
		case 'hidden':
            mraid.util.errorEvent("mraid.expand() called while state is 'hidden'.", "mraid.expand()");
			break;
        case 'resized':
            mraid.util.stateChangeEvent('expanded');
		}
	};

	// Takes an object... {width:300, height:250, useCustomClose:false, isModal:false};
	mraid.setExpandProperties=function(properties){
		properties.isModal=true; // Read only property.
		expand_properties=properties;
	};


	//returns a json object... {width:300, height:250, useCustomClose:false, isModal:false};
	mraid.getExpandProperties=function(){
		return expand_properties;
	};

	// Takes a boolean
	mraid.useCustomClose=function(well_is_it){
		ep = mraid.getExpandProperties();
		ep.useCustomClose = well_is_it;
		mraid.setExpandProperties(ep);
	};

	// Loads a given URL
	mraid.open=function(url){
		window.open(url);
	};

    // MRAID 2.0 Stuff.
    mraid.resize=function(){
        if(resize_properties.height<0 || resize_properties.width<0){
            mraid.util.errorEvent("mraid.resize() called before mraid.setResizeProperties()", "mraid.resize()");
            return;
        }
        switch(mraid.getState()){
        case 'loading':
            mraid.util.errorEvent("mraid.resize() called while state is 'loading'.", "mraid.resize()");
            break;
        case 'expanded':
            mraid.util.errorEvent("mraid.resize() called while state is 'expanded'.", "mraid.resize()");
            break;
        case 'resized':
        case 'default':
            window.open("mraid://resize/?w="+resize_properties.width
                       +"&h="+resize_properties.height
                       +"&offset_x="+resize_properties.offsetX
                       +"&offset_y="+resize_properties.offsetY
                       +"&custom_close_position="+resize_properties.customClosePosition
                       +"&allow_offscreen="+resize_properties.allowOffscreen);
            mraid.util.stateChangeEvent('resized');
            break;
        case 'hidden':
            break;

        }

    }

    mraid.setResizeProperties=function(props){
        if(props.customClosePosition!=null && props.customClosePosition != "top-right"
                                       && props.customClosePosition != "top-left"
                                       && props.customClosePosition != "center"
                                       && props.customClosePosition != "bottom-left"
                                       && props.customClosePosition != "bottom-right"
                                       && props.customClosePosition != "top-center"
                                       && props.customClosePosition != "bottom-center"){
            mraid.util.errorEvent("Invalid customClosePosition.", "mraid.setResizeProperties()");
        }else{
            resize_properties=props;
        }
    }

    mraid.getResizeProperties=function(){
        return resize_properties;
    }


    //returns a json object... {allowOrientationChange:true, forceOrientation:"none"};
    mraid.getOrientationProperties=function(){
        return orientation_properties;
    }

    // Takes an object... {allowOrientationChange:true, forceOrientation:"none"};
    mraid.setOrientationProperties=function(properties){
        orientation_properties=properties;

        if (typeof properties === "undefined") {
           return;
        }


        if(properties.forceOrientation!=='portrait' && properties.forceOrientation!=='landscape' && properties.forceOrientation!=='none' ){
            mraid.util.errorEvent("Invalid orientationProperties forceOrientation property", "mraid.setOrientationProperties()");
            properties.forceOrientation='none';
        }

        if(typeof properties.allowOrientationChange !== "boolean"){
            mraid.util.errorEvent("Invalid orientationProperties allowOrientationChange property", "mraid.setOrientationProperties()");
            properties.allowOrientationChange=true;
        }

        window.open("mraid://setOrientationProperties/?allow_orientation_change="+properties.allowOrientationChange
                   +"&force_orientation="+properties.forceOrientation);
    }

    // Creates a calendar event when passed a W3C-formatted json object
    mraid.createCalendarEvent=function(event){
        window.open("mraid://createCalendarEvent/?p="+encodeURIComponent(JSON.stringify(event)));
    }

    // Plays a video in the native player
    mraid.playVideo=function(uri){
        window.open("mraid://playVideo/?uri="+encodeURIComponent(uri));
    }

    // Stores a picture on the device
    mraid.storePicture=function(uri){
        window.open("mraid://storePicture/?uri="+encodeURIComponent(uri));
    }

    // Convenience function to modify useCustomClose attribute of expandProperties
    mraid.useCustomClose=function(value){
        expand_properties.useCustomClose = value;
    }

    // Checks if a feature is supported by this device
    mraid.supports=function(feature){
        if(mraid.getState()=="loading"){
            mraid.util.errorEvent("Method 'mraid.supports()' called during loading state.", "mraid.supports()");
            return;
        }
        switch(feature){
        case 'sms':
            return supports_sms;
        case 'tel':
            return supports_tel;
        case 'calendar':
            return supports_calendar;
        case 'storePicture':
            return supports_storePicture;
        case 'inlineVideo':
            return supports_inlineVideo;
        }
        mraid.util.errorEvent("Unknown feature to check for support: "+feature, "mraid.supports()");
        return false;
    }

    // Gets the screen size of the device
    mraid.getScreenSize=function(){
        if(mraid.getState()=="loading"){
            mraid.util.errorEvent("Method 'mraid.getScreenSize()' called during loading state.", "mraid.getScreenSize()");
            return;
        }else{
            return screen_size;
        }
    }

    // Gets the max size of the ad if expanded (so it won't obscure the app's title bar)
    mraid.getMaxSize=function(){
        if(mraid.getState()=="loading"){
            mraid.util.errorEvent("Method 'mraid.getMaxSize()' called during loading state.", "mraid.getMaxSize()");
            return;
        }else{
            return max_size;
        }
    }

    // Gets the default position of the ad view, in dips offset from top left.
    mraid.getDefaultPosition=function(){
        if(mraid.getState()=="loading"){
            mraid.util.errorEvent("Method 'mraid.getDefaultPosition()' called during loading state.", "mraid.getDefaultPosition()");
            return;
        }else{
            return default_position;
        }
    }


    // Gets the current position of the ad view, in dips offset from top left.
    mraid.getCurrentPosition=function(){
        if(mraid.getState()=="loading"){
            mraid.util.errorEvent("Method 'mraid.getCurrentPosition()' called during loading state.", "mraid.getCurrentPosition()");
            return;
        }else{
            return current_position;
        }
    }

	// ----- MRAID UTILITY FUNCTIONS -----
	// These functions are called by the native SDK to drive events and update information

	mraid.util.setPlacementType=function(type){
		placement_type=type;
	};

	mraid.util.readyEvent=function(){
		for(var i=0;i<listeners['ready'].length;i++){
			listeners['ready'][i]();
		}
	};

	mraid.util.errorEvent=function(message, what_doing){
		for(var i=0;i<listeners['error'].length;i++){
			listeners['error'][i](message, what_doing);
		}
	};

	mraid.util.viewableChangeEvent=function(is_viewable_now){
	    if(state==='loading') return;
		is_viewable = is_viewable_now;
		for(var i=0;i<listeners['viewableChange'].length;i++){
			listeners['viewableChange'][i](is_viewable_now);
		}
	};

	mraid.util.setIsViewable=function(is_it_viewable){
		if(is_viewable===is_it_viewable) return;
		is_viewable=is_it_viewable;
		mraid.util.viewableChangeEvent(is_viewable);
	};

	mraid.util.stateChangeEvent=function(new_state){
		if(state===new_state && state!='resized') return;
		state=new_state;
		if(new_state==='hidden'){
			mraid.util.setIsViewable(false);
		}
		for(var i=0;i<listeners['stateChange'].length;i++){
			listeners['stateChange'][i](new_state);
		}
	};

	mraid.util.sizeChangeEvent=function(width, height){
	    if(state==='loading') return;
	    if(width != mraid.getCurrentPosition().size_event_width ||
		height != mraid.getCurrentPosition().size_event_height){
		mraid.getCurrentPosition().size_event_width = width
		mraid.getCurrentPosition().size_event_height = height
		for(var i=0;i<listeners['sizeChange'].length;i++){
                	listeners['sizeChange'][i](width, height);
            	}
            }
	}

	var supports_sms = false;
	var supports_tel = false;
	var supports_calendar = false;
	var supports_storePicture = false;
	var supports_inlineVideo = false;
	mraid.util.setSupportsSMS=function(val){
	    supports_sms = val;
	}

	mraid.util.setSupportsTel=function(val){
	    supports_tel=val;
	}

	mraid.util.setSupportsCalendar=function(val){
	    supports_calendar=val;
	}

	mraid.util.setSupportsStorePicture=function(val){
	    supports_storePicture=val;
	}

	mraid.util.setSupportsInlineVideo=function(val){
	    supports_inlineVideo=val;
	}

	mraid.util.setScreenSize=function(width, height){
	    screen_size={"width":width,
	                 "height": height};
	}

    mraid.util.setMaxSize=function(width, height){
        max_size={"width":width,
                  "height": height};
    }

    mraid.util.setDefaultPosition=function(x, y, width, height){
        default_position={"x": x,
                          "y": y,
                          "width":width,
                          "height": height,
                          "size_event_width" : 0,
                          "size_event_height": 0};
        current_position = default_position;
    }

    mraid.util.setCurrentPosition=function(x, y, width, height){
        current_position={"x": x,
                          "y": y,
                          "width":width,
                          "height": height,
                          "size_event_width" : 0,
                          "size_event_height": 0};
    }

}());
