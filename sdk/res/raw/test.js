    
<!--
   Copyright 2013 APPNEXUS INC
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
    <meta name='viewport' content='user-scalable=no, width=device-width,initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0'/>
    <script type='text/javascript' src='mraid.js'></script>
    <script type='text/javascript'>
      function assert(condition, description){
        if(!condition){
          alert("FAIL: Assertion failed: "+description);
          return;
        }
        alert("PASS: Assertion passed: "+description);
      }
      assert(mraid.getVersion()==="1.0", "Get Version");
      assert(mraid.getState()==="loading", "Loading state");
      // Set the expand properties
      var expand_props = {width:250,height:250,useCustomClose:false,isModal:true};
      mraid.setExpandProperties(expand_props);
      assert(mraid.getExpandProperties()==expand_props, "Set expand properties");
      // Listen for the state change events
      mraid.addEventListener('error', onError);
      mraid.addEventListener('stateChange', onStateChange);
      mraid.addEventListener('ready', onReady);
      mraid.addEventListener('stateChange', onlyOnce);
      var j=0;
      function onlyOnce(){
        if(j<1){
          mraid.removeEventListener('stateChange', onlyOnce);
        }else{
          alert("FAIL: removeEventListener did not unregister a callback");
        }
        j++;
      }        
      
      function onError(error){
        alert('Error: '+error);
      }

      var i=0;
      function onReady(){
        if(i<1){
         alert('PASS: onReady() called');
         assert(mraid.getState()==="default", "Default state in onReady");
         assert(mraid.isViewable()===true, "Viewable in onReady");
        }else{
          alert('FAIL: onReady() called multiple times');
        }
        i++;
      }

      function onStateChange(new_state){
        if(new_state==="hidden"){
          assert(mraid.isViewable()===false, "Not viewable when state is hidden");
        }else if(new_state==="default"){
          assert(mraid.isViewable()===true, "Viewable in default state");
        }else if(new_state==="expanded"){
          assert(mraid.isViewable()===true, "Viewable in expanded state");
        }
      }

      function poll(){
        alert('Placement type: '+mraid.getPlacementType());
        alert('State: '+mraid.getState());
        alert('isViewable: '+mraid.isViewable());
      }
    </script>
    <button onClick='mraid.expand()'>Expand</button>
    <button onClick='mraid.close()'>Close</button>
    <button onClick='poll()'>Poll</button>
