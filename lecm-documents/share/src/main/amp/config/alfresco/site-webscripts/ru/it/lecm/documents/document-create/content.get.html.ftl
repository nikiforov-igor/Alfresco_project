<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/documents/create/lecm-document-create.js" group="document-create"/>
</@>

<@markup id="widgets">
	<@createWidgets group="document-create"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div id="${el}-body" class="document-create"></div>
	</@>
</@>