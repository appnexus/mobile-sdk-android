<html>
  <head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
    <meta name='viewport' content='user-scalable=no, width=device-width,initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0'/>
    <script type='text/javascript' src='mraid.js'></script>
    <script type='text/javascript'>
      // Set the expand properties
      mraid.setExpandProperties({width:300,height:250,useCustomClose:false,lockOrientation:true});
      // Listen for the state change events
      mraid.addEventListener('error', onError);
      mraid.addEventListener('stateChange', onStateChange);
      mraid.addEventListener('ready', onReady);
      
      alert('MRAID Version: '+mraid.getVersion());
      function onError(error){
        alert('Error: '+error);
      }

      function onReady(){
        alert('ready event fired');
      }

      function onStateChange(new_state){
	alert('onStateChange: '+new_state);
      }

      function poll(){
	alert('Placement type: '+mraid.getPlacementType());
	alert('State: '+mraid.getState());
	alert('isViewable: '+mraid.isViewable());
      }
    </script>
  </head>

  <body>
    <div onClick='mraid.expand()'><button>Expand</button></div>
    <div onClick='mraid.close()' ><button>Close</button></div>
    <div onClick='poll()' ><button>Poll</button></div>
  </body>
</html>
