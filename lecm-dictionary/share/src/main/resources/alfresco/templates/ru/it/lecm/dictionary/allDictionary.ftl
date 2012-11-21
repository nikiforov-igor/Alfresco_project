<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<@documentLibraryJS />

	<@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
	<@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
	<#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>

	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary-all.js"></@script>
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
	<@region id="title" scope="template"/>
</div>
<div id="bd">
	<div class="yui-t1">
		<div id="yui-main">
			<div class="yui-b" style="margin-left: 0;" id="alf-content">
				<@region id="toolbar" scope="template" />
				<@region id="datagrid" scope="template" />
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