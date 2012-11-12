<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<script type="text/javascript"> //<![CDATA[
	Alfresco.logger.info("${logMsg}");
	var obj = ${json};
	Alfresco.logger.info(obj);
	new Alfresco.widget.Resizer("DelegationResizer").setOptions({
		divLeft: "divWest",
		divRight: "divCenter",
		initialWidth: 300
	});
	YAHOO.util.Event.onDOMReady(function () {
		Alfresco.logger.info("Delegation module ready.");
	});
//]]>
</script>

<!-- Alfresco default scripts -->

<!-- Logic ECM scripts -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/ru/it/lecm/delegation/delegation-const.js"/>
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
	<@region id="title" scope="template"/>
</div>
<div id="bd">
	<@region id="delegation-actions" scope="template"/>
	<div class="yui-t1" id="alfresco-delegation">
		<div id="yui-main">
			<div class="yui-b" id="divCenter">
				<@region id="delegation-toolbar" scope="template"/>
				<@region id="center" scope="template"/>
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
