<#include "/org/alfresco/include/alfresco-template.ftl"/>
<@templateHeader>
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
	<@region id="title" scope="template"/>
</div>
<div id="bd">
	<div class="yui-t1" id="alfresco-delegation">
		<div id="yui-main">
			<div id="divDelegationContent">
				<@region id="view" scope="template" class="view"/>
			</div>
		</div>
	</div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>
