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
    listeners['ready'] = [];
    listeners['error'] = [];
    listeners['stateChange'] = [];
    listeners['viewableChange'] = [];
    listeners['sizeChange'] = [];
    var state = 'loading'; //Can be loading, default, expanded, hidden, or resized
    var placement_type = 'inline';
    var is_viewable = false;
    var expand_properties = {
        width: -1,
        height: -1,
        useCustomClose: false,
        isModal: true
    };
    var orientation_properties = {
        allowOrientationChange: true,
        forceOrientation: "none"
    };
    var resize_properties = {
        customClosePosition: 'top-right',
        allowOffscreen: true
    };
    var screen_size = {};
    var max_size = {};
    var default_position = {};
    var current_position = {};
    var size_event_width = 0;
    var size_event_height = 0;
    var mraid_enable_called = false;
    var page_finished = false;
    var supports = [];
    supports['sms'] = false;
    supports['tel'] = false;
    supports['calendar'] = false;
    supports['storePicture'] = false;
    supports['inlineVideo'] = false;

    // constants for interaction with anjam.js
    var MRAID_STATE = "state";
    var MRAID_PLACEMENT_TYPE = "placementType";
    var MRAID_VIEWABLE = "viewable";
    var MRAID_EXPAND_PROPERTIES = "expandProperties";
    var MRAID_RESIZE_PROPERTIES = "resizeProperties";
    var MRAID_ORIENTATION_PROPERTIES = "orientationProperties";
    var MRAID_SCREEN_SIZE = "screenSize";
    var MRAID_MAX_SIZE = "maxSize";
    var MRAID_DEFAULT_POSITION = "defaultPosition";
    var MRAID_CURRENT_POSITION = "currentPosition";

    // ----- MRAID AD API FUNCTIONS -----

    // getVersion() returns string '2.0'
    mraid.getVersion = function() {
        return '2.0'
    };

    // getVendor() returns string 'appnexus'
    mraid.getVendor = function() {
        return 'appnexus'
    };

    /** Adds a listener to a specific event. For example, a function onReady might be defined, and then mraid.addEventListener('ready', onReady)
     * is called. When the ready event is fired, onReady will be called.
     * Events 'error', 'viewableChange', 'stateChange', have parameters.
     */
    mraid.addEventListener = function(event_name, method) {
        if (listeners[event_name].indexOf(method) > -1) return; // Listener is already registered
        listeners[event_name].push(method);
    };

    // Removes a listener from the registry
    mraid.removeEventListener = function(event_name, method) {
        //If no method name is given, remove all listeners from event
        if (method == null) {
            listeners[event_name].length = 0;
            return;
        }

        var method_index = listeners[event_name].indexOf(method);
        if (method_index > -1) { //Don't try to remove unregistered listeners
            listeners[event_name].splice(method_index, 1);
        } else {
            mraid.util.errorEvent("An unregistered listener was requested to be removed.", "mraid.removeEventListener()")
        }
    };

    //returns 'loading', 'default', 'expanded', or 'hidden'
    mraid.getState = function() {
        return state;
    };

    //returns 'inline' or 'interstitial'
    mraid.getPlacementType = function() {
        return placement_type;
    };

    //returns true or false
    mraid.isViewable = function() {
        return is_viewable;
    };

    // ----- MRAID JS TO NATIVE FUNCTIONS -----

    mraid.enable = function() {
        if (mraid_enable_called) {
            return;
        }
        mraid_enable_called = true;
        if (page_finished) {
            mraid.util.nativeCall("mraid://enable/");
        }
    };

    //Closes an expanded ad or hides an ad in default state
    mraid.close = function() {
        switch (mraid.getState()) {
            case 'loading':
                mraid.util.errorEvent("mraid.close() called while state is 'loading'.", "mraid.close()");
                break;
            case 'default':
                mraid.util.nativeCall("mraid://close");
                mraid.util.stateChangeEvent('hidden');
                break;
            case 'expanded':
                mraid.util.nativeCall("mraid://close");
                mraid.util.stateChangeEvent('default');
                break;
            case 'hidden':
                mraid.util.errorEvent("mraid.close() called while ad was already hidden", "mraid.close()");
                break;
            case 'resized':
                mraid.util.nativeCall("mraid://close/");
                mraid.util.stateChangeEvent('default');
        }
    };

    // Expands a default state ad, or unhides a hidden ad. Optionally takes a URL to load in the expanded view
    mraid.expand = function(url) {
        switch (mraid.getState()) {
            case 'loading':
                mraid.util.errorEvent("mraid.expand() called while state is 'loading'.", "mraid.expand()");
                break;
            case 'default':
            case 'resized':
                if (placement_type !== "inline") {
                    mraid.util.errorEvent("mraid.expand() cannot be called for the placement_type " + placement_type, "mraid.expand()");
                    return;
                }
                mraid.util.nativeCall("mraid://expand/"
                    + "?w=-1"
                    + "&h=-1"
                    + "&useCustomClose=" + mraid.getExpandProperties().useCustomClose
                    + (url != null ? "&url=" + url : "")
                    + "&allow_orientation_change=" + orientation_properties.allowOrientationChange
                    + "&force_orientation=" + orientation_properties.forceOrientation);
                break;
            case 'expanded':
                mraid.util.errorEvent("mraid.expand() called while state is 'expanded'.", "mraid.expand()");
                break;
            case 'hidden':
                mraid.util.errorEvent("mraid.expand() called while state is 'hidden'.", "mraid.expand()");
                break;
        }
    };

    // Takes an object... {width:300, height:250, useCustomClose:false, isModal:false};
    mraid.setExpandProperties = function(properties) {
        if (typeof properties === "undefined") {
            mraid.util.errorEvent("Invalid expandProperties. Retaining default values.", "mraid.setExpandProperties()");
            return;
        }
        if (typeof properties.width === "number") {
            expand_properties.width = properties.width;
        }
        if (typeof properties.height === "number") {
            expand_properties.height = properties.height;
        }
        if (typeof properties.useCustomClose === "boolean") {
            expand_properties.useCustomClose = properties.useCustomClose;
        }
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_EXPAND_PROPERTIES, expand_properties);
        }
    };

    //returns a json object... {width:300, height:250, useCustomClose:false, isModal:false};
    mraid.getExpandProperties = function() {
        return expand_properties;
    };

    // Loads a given URL
    mraid.open = function(url) {
        mraid.util.nativeCall("mraid://open/?uri=" + encodeURIComponent(url));
    };

    // MRAID 2.0 Stuff.
    mraid.resize = function() {
        if (!mraid.util.validateResizeProperties(resize_properties, "mraid.resize()")) {
            mraid.util.errorEvent("mraid.resize() called without properly setting setResizeProperties", "mraid.resize()");
            return;
        }
        switch (mraid.getState()) {
            case 'loading':
                mraid.util.errorEvent("mraid.resize() called while state is 'loading'.", "mraid.resize()");
                break;
            case 'expanded':
                mraid.util.errorEvent("mraid.resize() called while state is 'expanded'.", "mraid.resize()");
                break;
            case 'resized':
            case 'default':
                if (placement_type !== "inline") {
                    mraid.util.errorEvent("mraid.resize() cannot be called for the placement_type " + placement_type, "mraid.resize()");
                    return;
                }
                if (resize_properties) {
                    mraid.util.nativeCall("mraid://resize/?w="
                        + resize_properties.width
                        + "&h=" + resize_properties.height
                        + "&offset_x=" + resize_properties.offsetX
                        + "&offset_y=" + resize_properties.offsetY
                        + "&custom_close_position=" + resize_properties.customClosePosition
                        + "&allow_offscreen=" + resize_properties.allowOffscreen);
                } else {
                    mraid.util.errorEvent("mraid.resize() called with no resize_properties set", "mraid.resize()");
                }
                break;
            case 'hidden':
                mraid.util.errorEvent("mraid.resize() called while state is 'hidden'.", "mraid.resize()");
                break;

        }

    }

    mraid.setResizeProperties = function(props) {
        if (mraid.util.validateResizeProperties(props, "mraid.setResizeProperties()")) {
            if (typeof props.customClosePosition === "undefined") {
                props.customClosePosition = 'top-right';
            }
            if (typeof props.allowOffscreen === "undefined") {
                props.allowOffscreen = true;
            }
            resize_properties = props;
            if ((typeof window.sdkjs) !== "undefined") {
                window.sdkjs.mraidUpdateProperty(MRAID_RESIZE_PROPERTIES, resize_properties);
            }
        }
    }

    mraid.getResizeProperties = function() {
        return resize_properties;
    }


    //returns a json object... {allowOrientationChange:true, forceOrientation:"none"};
    mraid.getOrientationProperties = function() {
        return orientation_properties;
    }

    // Takes an object... {allowOrientationChange:true, forceOrientation:"none"};
    mraid.setOrientationProperties = function(properties) {
        if (typeof properties === "undefined") {
            mraid.util.errorEvent("Invalid orientationProperties.", "mraid.setOrientationProperties()");
            return;
        } else {

            if (properties.forceOrientation === 'portrait' || properties.forceOrientation === 'landscape' || properties.forceOrientation === 'none') {
                orientation_properties.forceOrientation = properties.forceOrientation;
            } else {
                mraid.util.errorEvent("Invalid orientationProperties forceOrientation property", "mraid.setOrientationProperties()");
            }

            if (typeof properties.allowOrientationChange === "boolean") {
                orientation_properties.allowOrientationChange = properties.allowOrientationChange;
            } else {
                mraid.util.errorEvent("Invalid orientationProperties allowOrientationChange property", "mraid.setOrientationProperties()");
            }
        }

        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_ORIENTATION_PROPERTIES, orientation_properties);
        }

        mraid.util.nativeCall("mraid://setOrientationProperties/?allow_orientation_change=" + orientation_properties.allowOrientationChange + "&force_orientation=" + orientation_properties.forceOrientation);
    }

    // Creates a calendar event when passed a W3C-formatted json object
    mraid.createCalendarEvent = function(event) {
        mraid.util.nativeCall("mraid://createCalendarEvent/?p=" + encodeURIComponent(JSON.stringify(event)));
    }

    // Plays a video in the native player
    mraid.playVideo = function(uri) {
        mraid.util.nativeCall("mraid://playVideo/?uri=" + encodeURIComponent(uri));
    }

    // Stores a picture on the device
    mraid.storePicture = function(uri) {
        mraid.util.nativeCall("mraid://storePicture/?uri=" + encodeURIComponent(uri));
    }

    // Convenience function to modify useCustomClose attribute of expandProperties
    mraid.useCustomClose = function(value) {
        if (value === true) {
            expand_properties.useCustomClose = true;
        } else {
            expand_properties.useCustomClose = false;
        }
    }

    // Checks if a feature is supported by this device
    mraid.supports = function(feature) {
        if (mraid.getState() == "loading") {
            mraid.util.errorEvent("Method 'mraid.supports()' called during loading state.", "mraid.supports()");
            return;
        }
        if ((typeof supports[feature]) !== "boolean") {
            mraid.util.errorEvent("Unknown feature to check for support: " + feature, "mraid.supports()");
            return false;
        }
        return supports[feature];
    }

    // Gets the screen size of the device
    mraid.getScreenSize = function() {
        if (mraid.getState() == "loading") {
            mraid.util.errorEvent("Method 'mraid.getScreenSize()' called during loading state.", "mraid.getScreenSize()");
            return;
        } else {
            return screen_size;
        }
    }

    // Gets the max size of the ad if expanded (so it won't obscure the app's title bar)
    mraid.getMaxSize = function() {
        if (mraid.getState() == "loading") {
            mraid.util.errorEvent("Method 'mraid.getMaxSize()' called during loading state.", "mraid.getMaxSize()");
            return;
        } else {
            return max_size;
        }
    }

    // Gets the default position of the ad view, in dips offset from top left.
    mraid.getDefaultPosition = function() {
        if (mraid.getState() == "loading") {
            mraid.util.errorEvent("Method 'mraid.getDefaultPosition()' called during loading state.", "mraid.getDefaultPosition()");
            return;
        } else {
            return default_position;
        }
    }


    // Gets the current position of the ad view, in dips offset from top left.
    mraid.getCurrentPosition = function() {
        if (mraid.getState() == "loading") {
            mraid.util.errorEvent("Method 'mraid.getCurrentPosition()' called during loading state.", "mraid.getCurrentPosition()");
            return;
        } else {
            return current_position;
        }
    }

    // ----- MRAID UTILITY FUNCTIONS -----
    // These functions are called by the native SDK to drive events and update information

    mraid.util.nativeCall = function(uri) {
        window.location = uri;
    }

    mraid.util.setPlacementType = function(type) {
        placement_type = type;
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_PLACEMENT_TYPE, placement_type);
        }
    };

    mraid.util.fireEvent = function(event) {
        if (!listeners[event]) {
            return;
        }

        var args = Array.prototype.slice.call(arguments);
        args.shift();
        var length = listeners[event].length;
        for (var i = 0; i < length; i++) {
            if (typeof listeners[event][i] === "function") {
                listeners[event][i].apply(null, args);
            }
        }
    }

    mraid.util.readyEvent = function() {
        mraid.util.fireEvent('ready');
    };

    mraid.util.errorEvent = function(message, what_doing) {
        mraid.util.fireEvent('error', message, what_doing);
    };

    mraid.util.viewableChangeEvent = function(is_viewable_now) {
        if (state === 'loading') return;
        is_viewable = is_viewable_now;
        mraid.util.fireEvent('viewableChange', is_viewable_now);
    };

    mraid.util.setIsViewable = function(is_it_viewable) {
        if (is_viewable === is_it_viewable) return;
        is_viewable = is_it_viewable;
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_VIEWABLE, is_viewable);
        }
        mraid.util.viewableChangeEvent(is_viewable);
    };

    mraid.util.stateChangeEvent = function(new_state) {
        if (state === new_state && state != 'resized') return;
        state = new_state;
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_STATE, state);
        }
        if (new_state === 'hidden') {
            mraid.util.setIsViewable(false);
        }
        mraid.util.fireEvent('stateChange', new_state);
    };

    mraid.util.sizeChangeEvent = function(width, height) {
        if (state === 'loading') {
            size_event_width = width;
            size_event_height = height;
            return;
        }
        if (width != size_event_width || height != size_event_height) {
            size_event_width = width;
            size_event_height = height;
            mraid.util.fireEvent('sizeChange', width, height);
        }
    }

    mraid.util.validateResizeProperties = function(properties, callingFunctionName) {
        if (typeof properties === "undefined") {
            mraid.util.errorEvent("Invalid resizeProperties", callingFunctionName);
            return false;
        }
        if (typeof properties.width !== "number" || typeof properties.height !== "number" || typeof properties.offsetX !== "number" || typeof properties.offsetY !== "number") {
            mraid.util.errorEvent("Incomplete resizeProperties. width, height, offsetX, offsetY required", callingFunctionName);
            return false;
        }
        if (properties.width < 50) {
            mraid.util.errorEvent("Resize properties width below the minimum 50 pixels", callingFunctionName);
            return false;
        }
        if (properties.height < 50) {
            mraid.util.errorEvent("Resize properties height below the minimum 50 pixels", callingFunctionName);
            return false;
        }
        return true;
    }

    mraid.util.pageFinished = function() {
        page_finished = true;
        if (mraid_enable_called) {
            mraid.util.nativeCall("mraid://enable/");
        }
    }

    mraid.util.setSupports = function(feature, value) {
        supports[feature] = value;
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateSupports(feature, value);
        }
    }

    mraid.util.setScreenSize = function(width, height) {
        screen_size = {
            "width": width,
            "height": height
        };
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_SCREEN_SIZE, screen_size);
        }
    }

    mraid.util.setMaxSize = function(width, height) {
        max_size = {
            "width": width,
            "height": height
        };
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_MAX_SIZE, max_size);
        }
    }

    mraid.util.setDefaultPosition = function(x, y, width, height) {
        default_position = {
            "x": x,
            "y": y,
            "width": width,
            "height": height
        };
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_DEFAULT_POSITION, default_position);
        }
    }

    mraid.util.setCurrentPosition = function(x, y, width, height) {
        current_position = {
            "x": x,
            "y": y,
            "width": width,
            "height": height
        };
        if ((typeof window.sdkjs) !== "undefined") {
            window.sdkjs.mraidUpdateProperty(MRAID_CURRENT_POSITION, current_position);
        }
    }

}());