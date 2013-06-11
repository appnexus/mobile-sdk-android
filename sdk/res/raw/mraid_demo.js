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
	var expand_props = {width:300,height:300,useCustomClose:false,isModal:true};
	mraid.setExpandProperties(expand_props);
	mraid.addEventListener('stateChange', onChange);
			alert('MAIN');
	mraid.addEventListener('viewableChange', onVis);
			alert('MAIN');
	function onChange(new_state){
		if(new_state==="default"){
			alert('DEFAULT');
			document.getElementById("image").src="http://www.katiediaries.com/images/eatplay.gif";
			document.getElementById("a").setAttribute("onclick",'mraid.expand(); alert("CLICK");');
			document.getElementById("words").innerHTML="TAP FOR A KITTEN";
			document.getElementById("words").style.color='red';
		}else if(new_state==="expanded"){
			alert('EXPANDED');
			document.getElementById("image").src='http://placekitten.com/g/300/300';
			document.getElementById("a").setAttribute("onclick", 'mraid.open("http://en.wikipedia.org/wiki/Munchkin_cat")');
			document.getElementById("words").innerHTML="Tap for info on what Andy calls a 'Corgi Cat'."; 
			document.getElementById("words").style.color='green';
		}else if(new_state==="hidden"){
			alert('EXPANDED');
		}else if(new_state==="loading"){
			alert('LOADING');
		}
	}

	function onVis(visibility){
		alert("Visibility change: "+visibility);
	}

    </script>
<div id="a">
<image id="image" />
</div>
<div style="position:absolute; top:30px; left:75px;width:150px;height:20px"><center><div id='words'></div></center></div>

