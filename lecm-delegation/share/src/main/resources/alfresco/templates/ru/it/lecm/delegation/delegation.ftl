<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<script type="text/javascript"> //<![CDATA[
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
<@script type="text/javascript" src="${page.url.context}/res/scripts/ru/it/lecm/delegation/delegation-const.js"></@script>
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
