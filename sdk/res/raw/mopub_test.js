<html>
  <head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
    <meta name='viewport' content='user-scalable=no, width=device-width,initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0'/>
    <script type='text/javascript' src='jsfile://mraid_mopub.js'></script>
    <style type='text/css'>
      html, body {
        margin: 0;
        padding: 0;
      }

      .banner, .overlayBanner {
        display: block;
        position: absolute;
        top: 0px;
        left: 0px;
        z-index: 1000;
        overflow: hidden;
      }

      .banner {
        background-color: red;
        width: 300px;
        height: 50px;
      }

      .overlayBanner {
        background-color: green;
        width: 300px;
        height: 250px;
      } 
    </style>
    <script type='text/javascript'>
      // Set the expand properties
      mraid.setExpandProperties({width:300,height:250,useCustomClose:false,lockOrientation:true});
      // Listen for the state change events
      mraid.addEventListener('error', onError);
      mraid.addEventListener('stateChange', onStateChange);
      mraid.addEventListener('ready', onReady);

      function onError(error){
        alert('Error: '+error);
      }

      function onReady(){
        alert('ready event fired');
      }

      function onStateChange() {
	alert('MRAID State: '+mraid.getState());
        switch(mraid.getState()) {
        case "expanded":
          document.getElementById('banner').setAttribute('class', 'overlayBanner');
          document.getElementById('banner').setAttribute('onClick', 'mraid.close()');
          break;
        case "default":
          document.getElementById('banner').setAttribute('class', 'banner');
          document.getElementById('banner').setAttribute('onClick', 'mraid.expand()');
          break;
        }
      }
    </script>
  </head>

  <body>
    <div id='banner' onClick='mraid.expand()'><img src='http://profile.ak.fbcdn.net/hprofile-ak-ash4/203525_170232353033960_1305274426_q.jpg'> </img></div>
  </body>
</html>
