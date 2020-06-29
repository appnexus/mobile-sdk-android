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
(function () {

    if ((typeof window.anjam) !== "undefined") {
        return;
    }

    // constants
    var DEBUG = false;
    var VERSION_NO = "1.1";
    var ANJAM_PROTOCOL = "anjam:";
    var SDKJS_PROTOCOL = "sdkjs:";
    var WINDOW_NAME_PREFIX = "anjam_";
    var CALL_INIT = "init";
    var CALL_MAYDEEPLINK = "MayDeepLink";
    var CALL_DEEPLINK = "DeepLink";
    var CALL_EXTERNALBROWSER = "ExternalBrowser";
    var CALL_INTERNALBROWSER = "InternalBrowser";
    var CALL_RECORDEVENT = "RecordEvent";
    var CALL_DISPATCHAPPEVENT = "DispatchAppEvent";
    var CALL_GETVERSION = "GetVersion";
    var CALL_GETDEVICEID = "GetDeviceID";
    var CALL_GETCUSTOMKEYWORDS = "GetCustomKeywords";
    var CALL_SETMRAIDREFRESHFREQUENCY = "SetMRAIDRefreshFrequency";
    var CALL_READY = "ready";
    var CALL_RESULT = "result";
    var CALL_MRAID = "mraid";
    var MRAID_EVENT_PREFIX = "mraid_";

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
    var MRAID_CURRENT_APP_ORIENTATION = "currentAppOrientation";

    // public window variables
    var anjam = window.anjam = {};
    var mraid = null;
    // for ready event
    anjam.ready = false;

    // internal ANJAM variables
    var listeners = []; // event listeners
    var callbacks = []; // function callbacks
    var callbacksCounter = Math.ceil(Math.random() * 1e+10);
    var isIFrame = window !== window.top; // determine the context

    // mraid shell variables
    var mraidProperties = [];
    // supports is an array so handle it separately
    var mraidSupports = [];

    // -----
    // ----- HELPER FUNCTIONS -----
    // -----

    // Pair Object
    anjam.pair = function (first, second) {
        this.first = first;
        this.second = second;
    }

    // Convenient logging with flag
    anjam.anlog = function (message) {
        if (DEBUG) console.log(message);
    }

    // Cross-window communication helper
    anjam.postMessageToTop = function (message) {
        if (typeof message !== "string") {
            return;
        }
        window.top.postMessage(message, "*");
    };

    // Protocol definition
    anjam.constructMessage = function (command, paramsList) {
        var params = "";
        for (var i = 0; i < paramsList.length; i++) {
            var paramToAdd = paramsList[i].first + "=" + paramsList[i].second;
            if (i == 0) {
                params = paramToAdd;
            } else {
                params = params + "&" + paramToAdd;
            }
        }

        return ANJAM_PROTOCOL + command + "?" + params;
    }

    anjam.fireMessage = function (command, paramsList) {
        anjam.postMessageToTop(anjam.constructMessage(command, paramsList));
    };

    // -----
    // ----- INTERNAL SETUP -----
    // -----

    // window event listener to receive messages from SDKJS
    anjam.listener = function (event) {
        // accept all event.origin values, filter based on protocol

        // use anchor element for convenient parsing
        var a = document.createElement("a");
        a.href = event.data;

        if (a.protocol === SDKJS_PROTOCOL) {
            anjam.anlog("ANJAM received: " + event.data);

            // use pathname because host doesn't work
            var path = a.pathname;
            var search = a.search.substr(1); // drop the '?' at the front
            var query = search.split("&");
            var queryParameters = {};
            var length = query.length;
            for (var i = 0; i < length; i++) {
                var values = query[i].split("=");
                queryParameters[values[0]] = decodeURIComponent(values[1]);
            }

            if (path === CALL_READY) {
                anjam.onReadyEvent();
            } else if (path === CALL_RESULT) {
                anjam.onResult(queryParameters);
            } else if (path === CALL_MRAID) {
                anjam.onMraidCall(queryParameters);
            }

        }
    }

    anjam.fireInit = function () {
        anjam.fireMessage(CALL_INIT, [new anjam.pair("name", window.name)]);
    };

    // create mraid shell if necessary
    anjam.setupMraid = function () {
        if (!isIFrame) {
            return;
        }

        anjam.anlog("Creating Mraid shell");
        mraid = window.mraid = {};

        //set default values for properties
        mraidProperties[MRAID_STATE] = "loading";
        mraidProperties[MRAID_PLACEMENT_TYPE] = "inline";
        mraidProperties[MRAID_VIEWABLE] = false;
        mraidProperties[MRAID_EXPAND_PROPERTIES] = {
            width: -1,
            height: -1,
            useCustomClose: false,
            isModal: true
        };
        mraidProperties[MRAID_RESIZE_PROPERTIES] = {
            customClosePosition: 'top-right',
            allowOffscreen: true
        };
        mraidProperties[MRAID_ORIENTATION_PROPERTIES] = {
            allowOrientationChange: true,
            forceOrientation: "none"
        };
        mraidProperties[MRAID_SCREEN_SIZE] = {};
        mraidProperties[MRAID_MAX_SIZE] = {};
        mraidProperties[MRAID_DEFAULT_POSITION] = {};
        mraidProperties[MRAID_CURRENT_POSITION] = {};

        mraidProperties[MRAID_CURRENT_APP_ORIENTATION] = {
           orientation: "none",
           locked: false
        };
        // mraid event listeners

        mraid.addEventListener = function (eventName, method) {
            anjam.addEventListener(MRAID_EVENT_PREFIX + eventName, method);
        }

        mraid.removeEventListener = function (eventName, method) {
            anjam.removeEventListener(MRAID_EVENT_PREFIX + eventName,
                method);
        }

        // mraid methods with return values

        mraid.getVersion = function () {
            return "2.0";
        }

        mraid.getVendor = function () {
            return "appnexus";
        }

        mraid.getState = function () {
            return mraidProperties[MRAID_STATE];
        }

        mraid.getPlacementType = function () {
            return mraidProperties[MRAID_PLACEMENT_TYPE];
        }

        mraid.isViewable = function () {
            return mraidProperties[MRAID_VIEWABLE];
        }

        mraid.getExpandProperties = function () {
            return mraidProperties[MRAID_EXPAND_PROPERTIES];
        }

        mraid.getResizeProperties = function () {
            return mraidProperties[MRAID_RESIZE_PROPERTIES];
        }

        mraid.getOrientationProperties = function () {
            return mraidProperties[MRAID_ORIENTATION_PROPERTIES];
        }

        mraid.supports = function (feature) {
            if (typeof mraidSupports[feature] !== "boolean") {
                return false;
            }
            return mraidSupports[feature];
        }

        mraid.getScreenSize = function () {
            return mraidProperties[MRAID_SCREEN_SIZE];
        }

        mraid.getMaxSize = function () {
            return mraidProperties[MRAID_MAX_SIZE];
        }

        mraid.getDefaultPosition = function () {
            return mraidProperties[MRAID_DEFAULT_POSITION];
        }

        mraid.getCurrentPosition = function () {
            return mraidProperties[MRAID_CURRENT_POSITION];
        }

        mraid.getCurrentAppOrientation = function () {
            return mraidProperties[MRAID_CURRENT_APP_ORIENTATION];
        }

        // mraid methods that need to be forwarded to main

        mraid.forward = function (method, args) {
            var paramsList = [new anjam.pair("method", method)];
            var length = args.length;

            for (var i = 0; i < length; i++) {
                paramsList.push(new anjam.pair("p" + i, encodeURIComponent(
                    JSON.stringify(args[i]))));
            }

            anjam.fireMessage(CALL_MRAID, paramsList);
        }

        mraid.open = function () {
            mraid.forward("open", arguments);
        }

        mraid.close = function () {
            mraid.forward("close", arguments);
        }

        mraid.expand = function () {
            mraid.forward("expand", arguments);
        }

        mraid.setExpandProperties = function () {
            mraid.forward("setExpandProperties", arguments);
        }

        mraid.resize = function () {
            mraid.forward("resize", arguments);
        }

        mraid.setResizeProperties = function () {
            mraid.forward("setResizeProperties", arguments);
        }

        mraid.setOrientationProperties = function () {
            mraid.forward("setOrientationProperties", arguments);
        }

        mraid.createCalendarEvent = function (event) {
            mraid.forward("createCalendarEvent", arguments);
        }

        mraid.playVideo = function (uri) {
            mraid.forward("playVideo", arguments);
        }

        mraid.storePicture = function (uri) {
            mraid.forward("storePicture", arguments);
        }

        mraid.useCustomClose = function (value) {
            mraid.forward("useCustomClose", arguments);
        }

        mraid.enable = function () {
            mraid.forward("enable", arguments);
        }

        mraid.enable();
    }

    // Initial setup on first load on ANJAM.js
    anjam.setup = function () {
        anjam.setupMraid();

        // if the window doesn't have a name, give a unique name
        if (typeof window.name !== "string" || window.name === "") {
            var time = new Date().getTime();
            window.name = WINDOW_NAME_PREFIX + time;
        }

        // register a window listener
        if (window.addEventListener) {
            window.addEventListener("message", anjam.listener, false);
        } else {
            window.attachEvent("onmessage", anjam.listener);
        }

        anjam.anlog("ANJAM instance created: name(" + window.name +
            "), counter(" + callbacksCounter + "), IFrame(" + isIFrame +
            ")");

        // register this window in SDKJS
        anjam.fireInit();
    }

    // call setup()
    anjam.setup();

    // -----
    // ----- INTERNAL API UTILITY FUNCTIONS -----
    // -----

    anjam.fireEvent = function (eventName) {
        var eventListeners = listeners[eventName];
        if (eventListeners) {
            var args = Array.prototype.slice.call(arguments);
            args.shift();
            var length = eventListeners.length;
            for (var i = 0; i < length; i++) {
                if ((typeof eventListeners[i]) === "function") {
                    eventListeners[i].apply(null, args);
                }
            }
        }
    }

    anjam.onReadyEvent = function () {
        // only fire the ready event once
        if (!anjam.ready) {
            anjam.ready = true;
            anjam.fireEvent(CALL_READY);
        }
    }

    anjam.onResult = function (queryParameters) {
        var cb = -1;
        if (queryParameters.cb) {
            cb = parseInt(queryParameters.cb);
        }

        // remove the cb param
        queryParameters.cb = null;
        if ((cb > -1) && (typeof callbacks[cb] === "function")) {
            // invoke the callback, then release it
            callbacks[cb](queryParameters);
            callbacks[cb] = null;
        }
    }

    anjam.onMraidCall = function (params) {
        // forward all mraid events to anjam-mraid listeners
        if (params.event) {
            anjam.fireEvent(MRAID_EVENT_PREFIX + params.event, params.p0,
                params.p1);
            return;
        } else if (params.method) {
            if (params.method === "updateProperty") {
                mraidProperties[params.propertyName] = JSON.parse(
                    decodeURIComponent(params.value));
            } else if (params.method === "updateSupports") {
                mraidSupports[params.feature] = params.value;
            }
        }

    }

    anjam.addCallback = function (callback) {
        callbacksCounter++;
        callbacks[callbacksCounter] = callback;
    }

    anjam.validateString = function (s) {
        return (s && ((typeof s) === "string"));
    }

    anjam.validateHttpString = function (url) {
        return (((typeof url) === "string")
            && (url.lastIndexOf("http", 0) === 0));
    }

    // -----
    // ----- PUBLIC API FUNCTIONS -----
    // -----

    anjam.addEventListener = function (eventName, method) {
        anjam.anlog("adding listener on " + eventName + " for method: " +
            method)

        listeners[eventName] = listeners[eventName] || [];

        var length = listeners[eventName].length;
        for (var i = 0; i < length; i++) {
            if (listeners[eventName][i] == method) {
                anjam.anlog("Already added");
                return;
            }
        }

        listeners[eventName].push(method);
    }

    anjam.removeEventListener = function (eventName, method) {
        anjam.anlog("remove listener on " + eventName + " for method: " +
            method)
        var eventListeners = listeners[eventName];
        if (eventListeners) {
            var index = eventListeners.indexOf(method);
            if (index > -1) {
                eventListeners[index] = null;
            }
        }
    }

    // required: url, callback
    anjam.MayDeepLink = function (url, callback) {
        if (typeof callback !== "function") {
            anjam.anlog("MayDeepLink error: callback parameter should be a function");
            return;
        }
        anjam.addCallback(callback);

        if (!anjam.validateString(url)) {
            anjam.anlog("MayDeepLink error: url should be a string");
            // call the callback function for failure
            var failureResult = {};
            failureResult.caller = CALL_MAYDEEPLINK;
            failureResult.cb = callbacksCounter;
            failureResult.mayDeepLink = false;
            anjam.onResult(failureResult);
            return;
        }

        anjam.fireMessage(CALL_MAYDEEPLINK, [new anjam.pair(
            "cb", callbacksCounter), new anjam.pair("url",
            encodeURIComponent(url))]);
    }

    // required: url, optional: callback
    anjam.DeepLink = function (url, callback) {
        var index = -1;
        if (typeof callback === "function") {
            anjam.addCallback(callback);
            index = callbacksCounter;
        }

        if (!anjam.validateString(url)) {
            anjam.anlog("DeepLink error: url should be a string");
            // call the callback function for failure
            if (index > -1) {
                var failureResult = {};
                failureResult.caller = CALL_DEEPLINK;
                failureResult.cb = callbacksCounter;
                anjam.onResult(failureResult);
            }
            return;
        }

        anjam.fireMessage(CALL_DEEPLINK, [new anjam.pair(
            "cb", index), new anjam.pair("url",
            encodeURIComponent(url))]);
    }

    // required: url
    anjam.ExternalBrowser = function (url) {
        if (!anjam.validateHttpString(url)) {
            anjam.anlog("ExternalBrowser error: url should be a string");
            return;
        }

        anjam.fireMessage(CALL_EXTERNALBROWSER, [new anjam.pair("url",
            encodeURIComponent(url))]);
    }

    // required: url
    anjam.InternalBrowser = function (url) {
        if (!anjam.validateHttpString(url)) {
            anjam.anlog("InternalBrowser error: url should be a string");
            return;
        }

        anjam.fireMessage(CALL_INTERNALBROWSER, [new anjam.pair("url",
            encodeURIComponent(url))]);
    }

    // required: url
    anjam.RecordEvent = function (url) {
        if (!anjam.validateHttpString(url)) {
            anjam.anlog("RecordEvent error: url should be a string");
            return;
        }

        anjam.fireMessage(CALL_RECORDEVENT, [new anjam.pair("url",
            encodeURIComponent(url))]);
    }

    // required: at least one of event or data must be a non-empty string
    anjam.DispatchAppEvent = function (event, data) {
        // if not valid strings, set the parameters to empty string
        if (!anjam.validateString(event)) {
            event = "";
        }
        if (!anjam.validateString(data)) {
            data = "";
        }

        // validate that at least one is valid
        if (!event && !data) {
            anjam.anlog("DispatchAppEvent error: at least one of event or data must be a non-empty string");
            return;
        }

        anjam.fireMessage(CALL_DISPATCHAPPEVENT, [new anjam.pair(
            "event", event), new anjam.pair("data", data)]);
    }

    // required: callback
    anjam.GetVersion = function (callback) {
        if (typeof callback !== "function") {
            anjam.anlog("GetVersion error: callback parameter should be a function");
            return;
        }
        anjam.addCallback(callback);

        // call the callback function with version number
        var result = {};
        result.caller = CALL_GETVERSION;
        result.cb = callbacksCounter;
        result.version = VERSION_NO;
        anjam.onResult(result);
    }

    // required: callback
    anjam.GetDeviceID = function (callback) {
        if (typeof callback !== "function") {
            anjam.anlog("GetDeviceID error: callback parameter should be a function");
            return;
        }
        anjam.addCallback(callback);

        anjam.fireMessage(CALL_GETDEVICEID, [new anjam.pair(
            "cb", callbacksCounter)]);
    }
  
    // required: callback
    anjam.GetCustomKeywords = function (callback) {
        if (typeof callback !== "function") {
            anjam.anlog("GetCustomKeywords error: callback parameter should be a function");
            return;
        }
        anjam.addCallback(callback);

        anjam.fireMessage(CALL_GETCUSTOMKEYWORDS, [new anjam.pair(
            "cb", callbacksCounter)]);
    }

    anjam.SetMRAIDRefreshFrequency = function(milliseconds) {
        if (isNaN(milliseconds)) {
            return;
        }

        anjam.fireMessage(CALL_SETMRAIDREFRESHFREQUENCY, [new anjam.pair(
            "ms", milliseconds)]);
    }

}());
