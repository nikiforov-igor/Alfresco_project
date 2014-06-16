<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/edit/lecm-document-edit.js" group="document-edit"/>
</@>

<@markup id="widgets">
	<@createWidgets group="document-edit"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div id="${el}-body" class="document-edit"></div>
	</@>
</@>