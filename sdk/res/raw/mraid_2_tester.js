    <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
    <meta name='viewport' content='user-scalable=no, width=device-width,initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0'/>
    <script src='mraid.js'></script>
    <script type='text/javascript'>
	Element.prototype.remove = function() {
	    this.parentElement.removeChild(this);
	}
	NodeList.prototype.remove = HTMLCollection.prototype.remove = function() {
	    for(var i = 0, len = this.length; i < len; i++) {
		if(this[i] && this[i].parentElement) {
		    this[i].parentElement.removeChild(this[i]);
		}
	    }
	}

    onReady = function(){
        alert("ON_READY");
    }

	document.playVideo = function(){
	    alert("PLAY_VIDEO");
	    mraid.playVideo("http://upload.wikimedia.org/wikipedia/commons/4/47/Cat_and_kittens.webm");
	}

	onChange = function(new_state){
		if(new_state==="default"){
			alert('DEFAULT');
			document.getElementById("banner").style.display='inline';
			if(document.getElementById("table1")!=null){
				document.getElementById("table1").remove();
			}

		}
		else if(new_state==="expanded"){
			alert('EXPANDED');
			document.getElementById("banner").style.display='none';

			var root_table = document.createElement("table");
            root_table.setAttribute("id", "table1");

            row1 = document.createElement("tr");

            td1 = document.createElement("td");

            button1 = document.createElement("button");

            button1.innerHTML = "Play Video";
            button1.onclick = 'javascript:document.playVideo();';

            td1.appendChild(button1);
            row1.appendChild(td1);
            root_table.appendChild(row1);

			document.getElementById("expanded").appendChild(root_table);

		}else if(new_state==="hidden"){
			alert('EXPANDED');
		}else if(new_state==="loading"){
			alert('LOADING');
		}
	}


	mraid.addEventListener('ready', onReady)
	mraid.addEventListener('stateChange', onChange);

    </script>
    <div id="banner" onclick='mraid.expand()'><img src='http://dummyimage.com/320x50/e67e17/fff.jpg&text=Click+to+Expand' /></div>
    <div id="expanded" />
