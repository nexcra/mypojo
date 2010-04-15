package erwins.util.lib{
	import flash.external.ExternalInterface;

	/**
	 * Browser detect
	 * @author Yongho Ji
	 * @since 2009.03.24
	 * @see http://www.quirksmode.org/js/detect.html
	 */
	public class BrowserDetect
	{
		/**
		 * Here we define javascript functions which will be inserted into the DOM
		 */
		private static const DATA_OS:String =
				"var dataOS = [" +
					"{" +
						"string: navigator.platform," +
						"subString: \"Win\"," +
						"identity: \"Windows\"" +
					"}," +
					"{" +
						"string: navigator.platform," +
						"subString: \"Mac\"," +
						"identity: \"Mac\"" +
					"}," +
					"{" +
						"string: navigator.userAgent," +
						"subString: \"iPhone\"," +
						"identity: \"iPhone/iPod\"" +
					"}," +
					"{" +
						"string: navigator.platform," +
						"subString: \"Linux\"," +
						"identity: \"Linux\"" +
					"}" +
				"];";

		private static const DATA_BROWSER:String =
				"var dataBrowser = [" +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"Chrome\"," +
							"identity: \"Chrome\"" +
						"}," +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"OmniWeb\"," +
							"versionSearch: \"OmniWeb/\"," +
							"identity: \"OmniWeb\"" +
						"}," +
						"{" +
							"string: navigator.vendor," +
							"subString: \"Apple\"," +
							"identity: \"Safari\"," +
							"versionSearch: \"Version\"" +
						"}," +
						"{" +
							"prop: window.opera," +
							"identity: \"Opera\"" +
						"}," +
						"{" +
							"string: navigator.vendor," +
							"subString: \"iCab\"," +
							"identity: \"iCab\"" +
						"}," +
						"{" +
							"string: navigator.vendor," +
							"subString: \"KDE\"," +
							"identity: \"Konqueror\"" +
						"}," +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"Firefox\"," +
							"identity: \"Firefox\"" +
						"}," +
						"{" +
							"string: navigator.vendor," +
							"subString: \"Camino\"," +
							"identity: \"Camino\"" +
						"}," +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"Netscape\"," +
							"identity: \"Netscape\"" +
						"}," +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"MSIE\"," +
							"identity: \"Explorer\"," +
							"versionSearch: \"MSIE\"" +
						"}," +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"Gecko\"," +
							"identity: \"Mozilla\"," +
							"versionSearch: \"rv\"" +
						"}," +
						"{" +
							"string: navigator.userAgent," +
							"subString: \"Mozilla\"," +
							"identity: \"Netscape\"," +
							"versionSearch: \"Mozilla\"" +
						"}" +
					"];";		

		private static const FUNCTION_SEARCH_BROWSER:String =
			"document.insertScript = function ()" +
			"{"	+
				"if (document.searchBrowser==null)" +
				"{" +
					"searchBrowser = function ()" +
					"{" +
						DATA_BROWSER +
						"var browser = null;" +
						"for (var i=0;i<dataBrowser.length;i++)	" +
						"{" +
							"var dataString = dataBrowser[i].string;" +
							"var dataProp = dataBrowser[i].prop;" +
							"if (dataString) " +
							"{" +
								"if (dataString.indexOf(dataBrowser[i].subString) != -1)" +
								"{" +
									"browser = dataBrowser[i].identity;" +
									"break;" +
								"}" +
							"}" +
							"else if (dataProp)" +
							"{" +
								"browser = dataBrowser[i].identity;" +
								"break;" +
							"}" +
						"}" +
						"if( browser == null )" +
						"{" +
							"browser = \"An unknown browser\";" +
						"}" +
						"return browser;" +
					"}" +
				"}" +
			"}";

		private static const FUNCTION_SEARCH_BROWSER_VERSION:String =
			"document.insertScript = function ()" +
			"{"	+
				"if (document.searchBrowserVersion==null)" +
				"{" +
					"searchBrowserVersion = function () " +
					"{" +
						DATA_BROWSER +
						"var browser;" +
						"for (var i=0;i<dataBrowser.length;i++)	" +
						"{" +
							"var browserString = dataBrowser[i].string;" +
							"var browserProp = dataBrowser[i].prop;" +
							"if (browserString) " +
							"{" +
								"if (browserString.indexOf(dataBrowser[i].subString) != -1)" +
								"{" +
									"browser = dataBrowser[i];" +
									"break;" +
								"}" +
							"}" +
							"else if (browserProp)" +
							"{" +
								"browser = dataBrowser[i];" +
								"break;" +
							"}" +
						"}" +
						"var versionSearchString = browser.versionSearch || browser.identity;" +
						"var index;" +
						"var version;" +
						"version = navigator.appVersion;" +
						"index = version.indexOf(versionSearchString);" +
						"if (index != -1) " +
						"{" +
							"return parseFloat(version.substring(index+versionSearchString.length+1));" +
						"}" +
						"version = navigator.userAgent;" +
						"index = version.indexOf(versionSearchString);" +
						"if (index != -1) " +
						"{" +
							"return parseFloat(version.substring(index+versionSearchString.length+1));" +
						"}" +
						"return \"an unknown version\";" +
					"}"+
				"}"+
			"}";	

		private static const FUNCTION_SEARCH_OS:String =
			"document.insertScript = function ()" +
			"{" +
				"if (document.searchOS==null)" +
				"{" +
					"searchOS = function ()" +
					"{" +
						DATA_OS +
						"var os = null;" +
						"for (var i=0;i<dataOS.length;i++)	" +
						"{" +
							"var dataString = dataOS[i].string;" +
							"var dataProp = dataOS[i].prop;" +
							"if (dataString) " +
							"{" +
								"if (dataString.indexOf(dataOS[i].subString) != -1)" +
								"{" +
									"os = dataOS[i].identity;" +
									"break;" +
								"}" +
							"}" +
							"else if (dataProp)" +
							"{" +
								"os = dataOS[i].identity;" +
								"break;" +
							"}" +
						"}" +
						"if( os == null )" +
						"{" +
							"os = \"An unknown OS\";" +
						"}" +
						"return os;" +
					"}"+
				"}"+
			"}";	

		private static var initFlag:Boolean = false;
		private static var _browserVersion:String = null;
		private static var _browser:String = null;
		private static var _os:String = null;

		private static function insertScript():void
		{
			if( initFlag ) return;
			if (! ExternalInterface.available)
			{
			    throw new Error("ExternalInterface is not available in this container. Internet Explorer ActiveX, Firefox, Mozilla 1.7.5 and greater, or other browsers that support NPRuntime are required.");
			}

			initFlag = true;
			ExternalInterface.call( FUNCTION_SEARCH_BROWSER );
			ExternalInterface.call( FUNCTION_SEARCH_BROWSER_VERSION );
			ExternalInterface.call( FUNCTION_SEARCH_OS );
		}

		/**
		 * browser name
		 */
		public static function get browser():String
		{
			if( _browser ) return _browser;
			insertScript();
			_browser = ExternalInterface.call( "searchBrowser" );
			return _browser;
		}
		
		public static function isFirefox():Boolean{
			return BrowserDetect.browser =='Firefox';
		}

		/**
		 * browser version
		 */
		public static function get browserVersion():String
		{
			if( _browserVersion ) return _browserVersion;
			insertScript();
			_browserVersion = ExternalInterface.call( "searchBrowserVersion" );
			return _browserVersion;
		}

		/**
		 * OS name
		 */
		public static function get OS():String
		{
			if( _os ) return _os;
			insertScript();
			_os = ExternalInterface.call( "searchOS" );
			return _os;
		}

	}
}