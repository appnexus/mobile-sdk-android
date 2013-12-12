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
	    mraid.playVideo("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
	}

	document.addJuly4th = function(){
	    alert("ADD_JULY_4TH");
	    mraid.createCalendarEvent({id:"july4",
	                               description:"Independence Day",
	                               location:"USA",
	                               summary:"The day the declaration of independence was signed.",
	                               start:"2014-07-04T10:00:00+00:00",
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
            // Add a button to test mraid.open(tel)
            row9 = document.createElement("tr");

            td9 = document.createElement("td");

            button9 = document.createElement("button");

            button9.innerHTML = "Call me maybe?";
            button9.setAttribute('onclick', '(function(){mraid.open("tel:2034513528")})();');

            td9.appendChild(button9);
            row9.appendChild(td9);
            root_table.appendChild(row9);
            // Add a button to test mraid.open(sms:)
            row10 = document.createElement("tr");

            td10 = document.createElement("td");

            button10 = document.createElement("button");

            button10.innerHTML = "Text me maybe?";
            button10.setAttribute('onclick', '(function(){mraid.open("sms:2034513528")})();');

            td10.appendChild(button10);
            row10.appendChild(td10);
            root_table.appendChild(row10);

			// Add a button to test mraid.supports()
            row11 = document.createElement("tr");

            td11 = document.createElement("td");

            button11 = document.createElement("button");

            button11.innerHTML = "Print result of supports() to logs";
            button11.setAttribute('onclick', '(function(){ \
                alert("mraid.supports(sms) = "+mraid.supports(\"sms\"));\
                alert("mraid.supports(tel) = "+mraid.supports(\"tel\"));\
                alert("mraid.supports(cal) = "+mraid.supports(\"calendar\"));\
                alert("mraid.supports(storePicture) = "+mraid.supports(\"storePicture\"));\
                alert("mraid.supports(inlineVideo) = "+mraid.supports(\"inlineVideo\"));\
                })();');

            td11.appendChild(button11);
            row11.appendChild(td11);
            root_table.appendChild(row11);
			// Add a button to test mraid.getScreenSize()
            row12 = document.createElement("tr");

            td12 = document.createElement("td");

            button12 = document.createElement("button");

            button12.innerHTML = "Write screensize to logs";
            button12.setAttribute('onclick', '(function(){alert("Screen size: "+mraid.getScreenSize().width+", "+mraid.getScreenSize().height)})();');

            td12.appendChild(button12);
            row12.appendChild(td12);
            root_table.appendChild(row12);
			// Add a button to test mraid.getMaxSize()
            row13 = document.createElement("tr");

            td13 = document.createElement("td");

            button13 = document.createElement("button");

            button13.innerHTML = "Write maxsize to logs";
            button13.setAttribute('onclick', '(function(){alert("Max size: "+mraid.getMaxSize().width+", "+mraid.getMaxSize().height)})();');

            td13.appendChild(button13);
            row13.appendChild(td13);
            root_table.appendChild(row13);
			// Add a button to test mraid.getCurrentPosition
            row14 = document.createElement("tr");

            td14 = document.createElement("td");

            button14 = document.createElement("button");

            button14.innerHTML = "Write currentposition to logs";
            button14.setAttribute('onclick', '(function(){alert("Current position: "+mraid.getCurrentPosition().x+", "+mraid.getCurrentPosition().y)})();');

            td14.appendChild(button14);
            row14.appendChild(td14);
            root_table.appendChild(row14);
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
