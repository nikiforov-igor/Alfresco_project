<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />

<@script type="text/javascript" src="${url.context}/res/components/model-editor/model-list2.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/model-editor/model-editor.css" />

<#assign id = args.htmlid>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
	function init() {
		new LogicECM.module.ModelEditor.ModelList2("${id}").setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
//]]></script>
<div id="${id}-body" class="datagrid models">
   
</div>
<div id="${id}-button">${msg("lecm.meditor.lbl.add")}</div>