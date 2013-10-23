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

	document.addJuly4th = function(){
	    alert("ADD_JULY_4TH");
	    mraid.createCalendarEvent({id:"july4",
	                               description:"Independence Day",
	                               location:"USA",
	                               summary:"The day the declaration of independence was signed.",
	                               end:"2014-07-04T12:00:00+00:00"
	                               });
	}

	onChange = function(new_state){
		if(new_state==="default"){
			alert('DEFAULT');
			document.getElementById("banner").style.display='inline';
			if(document.getElementById("table1")!=null){
				document.getElementById("table1").remove();
			}

		}else if(new_state==="expanded"){
			alert('EXPANDED');
			document.getElementById("banner").style.display='none';

			var root_table = document.createElement("table");
            root_table.setAttribute("id", "table1");

            // Add a playVideo tester

            row1 = document.createElement("tr");

            td1 = document.createElement("td");

            button1 = document.createElement("button");

            button1.innerHTML = "Play Video";
            button1.setAttribute('onclick', 'document.playVideo()');

            td1.appendChild(button1);
            row1.appendChild(td1);
            root_table.appendChild(row1);

            // Add a addCalendarEvent row

            row2 = document.createElement("tr");

            td2 = document.createElement("td");

            button2 = document.createElement("button");

            button2.innerHTML = "Add July 4th to Cal";
            button2.setAttribute('onclick', 'document.addJuly4th()');

            td2.appendChild(button2);
            row2.appendChild(td2);
            root_table.appendChild(row2);

			// Add a button to print the result of mraid.getVersion()
            row3 = document.createElement("tr");

            td3 = document.createElement("td");

            button3 = document.createElement("button");

            button3.innerHTML = "Print result of mraid.getVersion()";
            button3.setAttribute('onclick', '(function(){alert(mraid.getVersion())})();');

            td3.appendChild(button3);
            row3.appendChild(td3);
            root_table.appendChild(row3);

			// Add a button to print the result of mraid.getVendor()
            row4 = document.createElement("tr");

            td4 = document.createElement("td");

            button4 = document.createElement("button");

            button4.innerHTML = "Print result of mraid.getVendor()";
            button4.setAttribute('onclick', '(function(){alert(mraid.getVendor())})();');

            td4.appendChild(button4);
            row4.appendChild(td4);
            root_table.appendChild(row4);

			// Add a button to print the result of mraid.getPlacementType()
            row5 = document.createElement("tr");

            td5 = document.createElement("td");

            button5 = document.createElement("button");

            button5.innerHTML = "Print result of mraid.getPlacementType()";
            button5.setAttribute('onclick', '(function(){alert(mraid.getPlacementType())})();');

            td5.appendChild(button5);
            row5.appendChild(td5);
            root_table.appendChild(row5);
			// Add a button to test mraid.close()
            row6 = document.createElement("tr");

            td6 = document.createElement("td");

            button6 = document.createElement("button");

            button6.innerHTML = "Call mraid.close()";
            button6.setAttribute('onclick', '(function(){mraid.close()})();');

            td6.appendChild(button6);
            row6.appendChild(td6);
            root_table.appendChild(row6);
			// Add a button to test mraid.open()
            row7 = document.createElement("tr");

            td7 = document.createElement("td");

            button7 = document.createElement("button");

            button7.innerHTML = "Call mraid.open(aww.reddit.com)";
            button7.setAttribute('onclick', '(function(){mraid.open("http://aww.reddit.com/")})();');

            td7.appendChild(button7);
            row7.appendChild(td7);
            root_table.appendChild(row7);
			// Add a button to test mraid.resize()
			// Add a button to test mraid.setOrientationProperties()
			// Add a button to test mraid.storePicture()
            row8 = document.createElement("tr");

            td8 = document.createElement("td");

            button8 = document.createElement("button");

            button8.innerHTML = "Call mraid.storePicture(url)";
            button8.setAttribute('onclick', '(function(){mraid.storePicture("http://i.imgur.com/8dVigHB.jpg")})();');

            td8.appendChild(button8);
            row8.appendChild(td8);
            root_table.appendChild(row8);
			// Add a button to test mraid.supports()
			// Add a button to test mraid.getScreenSize()
			// Add a button to test mraid.getMaxSize()
			// Add a button to test mraid.getCurrentPosition
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
