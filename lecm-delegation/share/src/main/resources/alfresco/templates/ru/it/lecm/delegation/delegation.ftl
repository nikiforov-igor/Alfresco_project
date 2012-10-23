<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<script type="text/javascript"> //<![CDATA[
	new Alfresco.widget.Resizer("Calendar").setOptions({
		divLeft: "divWest",
		divRight: "divCenter",
		initialWidth: 215
	});
	YAHOO.util.Event.onDOMReady(function () {
		var loggerConfig = {
			width: "50%",
			height: "300px",
			footerEnabled: true,
			newestOnTop: false,
			verboseOutput: false
		};
		new YAHOO.widget.LogReader("delegationLogger", loggerConfig);
		YAHOO.log ("delegationLogger successfully initiated!", "info");
	});
//]]>
</script>
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
	<@region id="title" scope="template"/>
</div>
<div id="bd">
	<div class="yui-t1" id="alfresco-delegation">
		<div id="yui-main">
			<div class="yui-b" id="divCenter">
				<@region id="center" scope="template"/>
			</div>
			<div id="loggerContainer" class="yui-skin-sam">
				<div id="delegationLogger">
				</div>
			</div>
		</div>
		<div class="yui-b" id="divWest">
			<@region id="west" scope="template"/>
		</div>
	</div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>
