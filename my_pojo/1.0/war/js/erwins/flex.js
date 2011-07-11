var requiredMajorVersion = 9;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 28;

var Flex = {
		/**
		 * swf를 초기화 한다.
		 */
		init : function(name,height,width){
			this.name = name;	
			this.height = height == null ? '100%' : height;
			this.width = width == null ? '100%' : width;
			// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
			var hasProductInstall = DetectFlashVer(6, 0, 65);

			// Version check based upon the values defined in globals
			var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

			if (hasProductInstall && !hasRequestedVersion) {
				// DO NOT MODIFY THE FOLLOWING FOUR LINES
				// Location visited after installation is complete if installation is
				// required
				var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
				var MMredirectURL = window.location;
				document.title = document.title.slice(0, 47) + " - Flash Player Installation";
				var MMdoctitle = document.title;

				AC_FL_RunContent("src", "/Flex/playerProductInstall", "FlashVars", "MMredirectURL=" + MMredirectURL
						+ '&MMplayerType=' + MMPlayerType + '&MMdoctitle=' + MMdoctitle + "", "width", "100%", "height",
						"100%", "align", "middle", "id", "Sheet", "quality", "high", "bgcolor", "#869ca7", "name", "Sheet",
						"allowScriptAccess", "sameDomain", "type", "application/x-shockwave-flash", "pluginspage",
						"http://www.adobe.com/go/getflashplayer");//,"wmode", "opaque"
			} else if (hasRequestedVersion) {
				// if we've detected an acceptable version
				// embed the Flash Content SWF when all tests are passed
				this.display();

			} else { // flash is too old or we can't detect the plugin
				var alternateContent = 'Alternate HTML content should be placed here. ' + 'This content requires the Adobe Flash Player. ' + '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
				document.write(alternateContent); // insert non-flash content
			}
		},
		/**
		 * dispaly한다. div를 설정해줘야 나오는듯
		 * "WMode", "opaque" 옵션으로 플렉스 보다 HTML을 우선한다. => 하지만 파폭에서는 오류난다.
		 */
		display : function(){
			document.write('<div style="width:'+this.width+';height:'+this.height+'px;position:relative;">'); //추가~  이거 없으면 안됨
			var path = "/Flex/" + this.name ;
			/*
			AC_FL_RunContent("WMode", "opaque","src", path, "width", "100%", "height", "100%", "align", "middle", "id", this.name,
					"quality", "high", "bgcolor", "#869ca7", "name", this.name, "allowScriptAccess", "sameDomain", "type",
					"application/x-shockwave-flash", "pluginspage", "http://www.adobe.com/go/getflashplayer");*/
			AC_FL_RunContent("WMode", "window","src", path, "width", "100%", "height", "100%", "align", "middle", "id", this.name,
					"quality", "high", "bgcolor", "#869ca7", "name", this.name, "allowScriptAccess", "sameDomain", "type",
					"application/x-shockwave-flash", "pluginspage", "http://www.adobe.com/go/getflashplayer");
			
			document.write('</div>');
		}
}
