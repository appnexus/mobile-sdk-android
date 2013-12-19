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

    document.secondToLastDayOfMonth = function(){
        mraid.createCalendarEvent({id:"2ndtolast",
                                   description:"2nd To Last Day of the Month",
                                   location:"Global",
                                   summary:"A short description goes here, normally",
                                   start:"2000-07-04T10:00:00+00:00",
                                   recurrence:{frequency:"monthly",
                                               daysInMonth:[-2]}
                                   });
    }

    document.newMonth = function(){
        mraid.createCalendarEvent({id:"newmonth",
                                   description:"New month",
                                   location:"Global",
                                   summary:"A short description goes here, normally",
                                   start:"2000-07-04T10:00:00+00:00",
                                   reminder:"-3600000",
                                   recurrence:{frequency:"monthly",
                                               daysInMonth:[1]}
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

            // Add a button to test mraid.getCurrentPosition
            row15 = document.createElement("tr");

            td15 = document.createElement("td");

            button15 = document.createElement("button");

            button15.innerHTML = "Lock orientation landscape";
            button15.setAttribute('onclick', '(function(){mraid.setOrientationProperties({"allowOrientationChange":false,"forceOrientation":"landscape"})})();');

            td15.appendChild(button15);
            row15.appendChild(td15);
            root_table.appendChild(row15);

            // Add a button to test mraid.storePicture with data
            row16 = document.createElement("tr");

            td16 = document.createElement("td");

            button16 = document.createElement("button");

            button16.innerHTML = "storePicture(DATA)";
            button16.setAttribute('onclick', '(function(){mraid.storePicture("data:image/jpeg;base64,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")})();');

            td16.appendChild(button16);
            row16.appendChild(td16);
            root_table.appendChild(row16);

            // Add a recurrence test

            row17 = document.createElement("tr");

            td17 = document.createElement("td");

            button17 = document.createElement("button");

            button17.innerHTML = "Add 2nd To Last Day of the Month";
            button17.setAttribute('onclick', 'document.secondToLastDayOfMonth()');

            td17.appendChild(button17);
            row17.appendChild(td17);
            root_table.appendChild(row17);

            // Add a reminder test

            row18 = document.createElement("tr");

            td18 = document.createElement("td");

            button18 = document.createElement("button");

            button18.innerHTML = "Add New Month Reminder";
            button18.setAttribute('onclick', 'document.newMonth()');

            td18.appendChild(button18);
            row18.appendChild(td18);
            root_table.appendChild(row18);

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
