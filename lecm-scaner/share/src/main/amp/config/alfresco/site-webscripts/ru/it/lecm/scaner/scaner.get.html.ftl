<script>

	function init(){
		var applet,
			parent,
			scanner;

		// applet = document.getElementById("scanner-applet");
		// parent = applet.parentNode;
		// parent.style.cssText = "width:100%; height:100%";

		scanner = new LogicECM.Scanner.Page("scanner-page");
		scanner.init();
	}

	YAHOO.util.Event.onDOMReady(init);

</script>

<iframe
  id="IFRMenu"
  src="javascript:false;"
  scrolling="no"
  frameborder="0"
  style="position:fixed; top:0px; left:0px; display:none;">
 </iframe>

 <iframe
  id="IFRHeader"
  src="javascript:false;"
  scrolling="no"
  frameborder="0"
  style="position:fixed; top:0px; left:0px; display:block;">
 </iframe>

<applet code="com.aplana.scanner.ScannerApplet" id="scanner-applet" 
	archive="${url.context}/scripts/lecm-scaner/scanner-applet.jar" 
	width="100%" height="1000px">
<param name="targetUrl" value="${url.server}${targetURL}"/>
 <param name="filename" value="${fileName}"/>
</applet>
<script type="text/javascript" src="${url.context}/scripts/lecm-scaner/scanner.js"></script> 