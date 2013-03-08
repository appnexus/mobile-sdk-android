(function() {

	// Set up some variables
	var mraid = window.mraid = {};
	var listeners = [];
	var listeners['ready']=[];
	var listeners['error']=[];
	var listeners['stateChange']=[];
	var listeners['viewableChange']=[];
	var state='loading'; //Can be loading, default, expanded, or hidden
	var placement_type='inline'; // TODO set this to 'interstitial' from the java
	var is_viewable=false; // TODO set this from the java in onViewabilityChange
	var expand_properties={width:300, height:250, useCustomClose:false, isModal:true};

	// ----- MRAID AD API FUNCTIONS -----
	
	// getVersion() returns string '1.0'
	mraid.getVersion=function(){ return '1.0'};

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
		var method_index = listeners[event_name].indexOf(method);
		if(method_index > -1) //Don't try to remove unregistered listeners
			listeners[event_name].splice(method_index,1);
	};

	//returns 'loading', 'default', 'expanded', or 'hidden'
	mraid.getState=function(){
		return state;
	};

	//returns 'inline' or 'interstitial'
	mraid.getPlacementType(){
		return placement_type;
	};

	//returns true or false
	mraid.isViewable(){
		return is_viewable;
	};

	//returns a json object... {width:300, height:250, useCustomClose:false, isModal:false};
	mraid.getExpandProperties(){
		return expand_properties;
	};


	// ----- MRAID JS TO NATIVE FUNCTIONS -----

	//Closes an expanded ad or hides an ad in default state
	mraid.close=function(){
		//TODO Check the state to make sure we can close
		//TODO Call native close function
		//TODO Broadcast stateChange/update state variable
	};

	// Expands a default state ad, or unhides a hidden ad. Optionally takes a URL to load in the expanded view
	mraid.expand=function(url){
		//TODO Check the state to make sure we can expand
		//TODO Call native expand function
		//TODO Broadcase stateChange
		//TODO if url is set, open the url
	};

	// Takes an object... {width:300, height:250, useCustomClose:false, isModal:false};
	mraid.setExpandProperties(properties){
		properties.isModal=true; // Read only property.
		expand_properties=properties;
	};

	// Takes a boolean
	mraid.useCustomClose=function(well_is_it){
		ep = mraid.getExpandProperties();
		ep.useCustomClose = well_is_it;
		mraid.setExpandProperties(ep);
	};

	// Loads a given URL
	mraid.open=function(url){
		window.location=url;
	};
}());
