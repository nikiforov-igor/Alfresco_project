<br/>
<div id="toolbar"></div>

<script type="text/javascript">//<![CDATA[

(function() {


	function initToolbar() {

		var toolbar = new Toolbar.module.Dictionary("toolbar");
		toolbar.setMessages(${messages});
		toolbar.draw();
	}

	//once the DOM has loaded, we can go ahead and set up our tree:
	YAHOO.util.Event.onDOMReady(initToolbar);

})();
//]]></script>
