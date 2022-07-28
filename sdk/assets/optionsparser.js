/*
 *    Copyright 2019-2020 APPNEXUS INC
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

var options = {
    "autoInitialSize"  : true,
    
    "controlBarPosition"  : "below",                //important to set the control bar below else it will float
    
    "data" : {
        "adIcons"               : null,
        "durationMsec"          : null,
        "isVastVideoSkippable"  : false,
        "skipOffset"            : "",
        "skipOffsetMsec"        : null,
        "vastDurationMsec"      : null,
        "vastProgressEvent"     : {}
    },
    "delayExpandUntilVPAIDImpression"  : false,
    "delayExpandUntilVPAIDInit"        : true,
    //"disableTopBar"                    : true,      // Disable information bar at top of video playback view.
    
    
    "enableNativeInline"  : true,                   //this option is required if we are using HTML 5 player
    
    "fitInContainer"       : false,
    "fixedSizePlayer"      : true,
    "forceAdInFullscreen"  : false,
    
    "initialPlayback"  : "click",
    //this needs to be click for the load & play option else the player will load the ad & play automatically
    
    "learnMore" : {
        "clickToPause"  : false,
        "enabled"       : true
    },
    
    "mobileSDK"  :  true,
    
    "overlayPlayer" : false,
    
    "playerSkin" : {
        /*
         "controlBarHeight"  : 1,
         "controlBarColor"   : "#00000000",
         // Use controlBar* to remove diagnostics information bar at bottom of video playback view.
         //   Mutually exclusive with dividerColor option.
         */
        
        "dividerColor"  : "black"         // Mutually exclusive with controlBar* options.
    },
    "preloadInlineAudioForIos"  :  true,
    //this is set to toggle the audio play...
    //  ...if false then there will be no audio until the user clicks on the volume button for IOS player...
    //Not required for HTML player
    
    "shouldResizeVideoToFillMobileWebview"  : true,
    
    "showBigPlayButton"                     : false,        //hide the play/pause big button
    "showPlayToggle"                        : false,        //Hide the play/pause small button
    "showProgressBar"                       : true,
    
    "terminateUnresponsiveVPAIDCreative"  : false,
     "showVolume": false,

    "vpaidEnvironmentVars": {
        "rhythmone"  : true
    },
    "vpaidTimeout"  : 20000,
    
    "waterfallTimeout"  : 15000,
    "waterfallSteps"    : -1
};


var instreamOptions = {
    "allowFullscreen"  : false,         //show the fullscreen control
    "showFullScreenButton"                  : false,
    "aspectRatio"      : "16:9",        // Aspect ratio we expect the video to be played in

    "disableCollapse"  : false,         // This option is used in outstream. To do freeze on last frame. Always false for instream case.
    
    "initialAudio"     : "on",          // Setting this option to true turns on initial Audio
    
    "skippable": {                      // This option enables SKIP and configures it.
        "skipButtonText"  : "SKIP",
        "skipLocation"    : "top-right",
        "skipText"        : "Video can be skipped in %%TIME%% seconds",
        "enabled"         : true,
        "videoOffset"     : 5,
        "videoThreshold"  : 15
    }
};

var outstreamOptions = {
    "allowFullscreen"  : true,          // This option shows/hides the fill screen button
    "aspectRatio"      : "auto",        // Aspect ratio we expect the video to be played in
    "showFullScreenButton"                  : true,
    
    "disableCollapse": {
        "enabled"  : true,              // this option is used for freeze on last frame
        "replay"   : true               // This shows the replay button
    },
    
    "initialAudio" : "off",             // Setting this option to false turns off initial Audio
    
    "skippable": {                      // This option disables SKIP
        "enabled" : false
    }
};


// Default Partner. This will later be overriden by call from native layer to setOMIDPartner.
var partner = {
    "name"  : "Appnexus",            // This option name is to get OMID partner name
    "version"  : "Default-version",          // This option name is to get OMID partner version
};


var getOMIDPartner = function(){
    return partner;
}

//
// VAST player lifecycle.
//

// Default OMIDVideoEvent object. This will later be re-constructed in createVastPlayerWithContent.
var OMIDVideoEvent = {
    "isSkippable"  : true,
    "isAutoPlay"  : true,
    "position"  : "standalone"
};

var OMIDVideoEvents = function(){
    return OMIDVideoEvent;
}

function constructVideoPlayerOptions (publisherOptions){
    try {
        debugger
        var playerOptionsObject = JSON.parse(publisherOptions);
        
        var partnerOptions = playerOptionsObject.partner
        
        if(partnerOptions){
            partner.name = partnerOptions.name;
            partner.version = partnerOptions.version;
        }
        
        var entryPointType = playerOptionsObject.entryPoint
        
        if(entryPointType){
            if (entryPointType == "BANNER") {
                options = combineObjectsAndSubobjects(options, outstreamOptions);
                OMIDVideoEvent.position = "standalone";
                OMIDVideoEvent.isSkippable = outstreamOptions.skippable;
            } else if (entryPointType == "INSTREAM_VIDEO") {
                options = combineObjectsAndSubobjects(options, instreamOptions);
                OMIDVideoEvent.position = "preroll";
                OMIDVideoEvent.isSkippable = true;
            }
            OMIDVideoEvent.isAutoPlay = true;
        }
        var videoSettings = playerOptionsObject.videoOptions
        options = combineObjectsAndSubobjects(options, videoSettings);
    }catch(err) {
        console.log('Issue loading video into document :' + err.message);
    }
    return options
}

function combineObjectsAndSubobjects(target, source) {
    for (var key in source) {
        if (source.hasOwnProperty(key)) {
            if (typeof target[key] === "object") {
                target[key] = combineObjectsAndSubobjects(target[key], source[key]);
            } else {
                target[key] = source[key];
            }
        }
    }
    return target;
}
