<#include "/org/alfresco/components/form/controls/common/editorparams.inc.ftl" />
<#include "/org/alfresco/components/component.head.inc">

<style type="text/css" media="screen">
      @import "/share/res/components/model-editor/model-editor.css";
</style>

<@script type="text/javascript" src="${url.context}/res/components/model-editor/controls/dialog.js" />
<@script type="text/javascript" src="${url.context}/res/components/model-editor/controls/input.js" />
<@script type="text/javascript" src="${url.context}/res/components/model-editor/controls/select.js" />
<@script type="text/javascript" src="${url.context}/res/components/model-editor/model-editor.js" />

<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=8></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<#if form.capabilities?? && form.capabilities.javascript?? && form.capabilities.javascript == false><#assign jsDisabled=true><#else><#assign jsDisabled=false></#if>

<#if form.mode != "view">
	<div id="${fieldHtmlId}_categoryDlg"></div>
	<div id="${fieldHtmlId}_attributesDlg"></div>
	<div id="${fieldHtmlId}_associationsDlg"></div>
	<div id="${fieldHtmlId}_tablesDlg"></div>

	<script type="text/javascript">//<![CDATA[
	(function()
	{
		new IT.component.ModelEditor("${fieldHtmlId}",["datatable","storage","container"]).setOptions(
		{
			<#if context.properties.nodeRef??>
        	nodeRef: "${context.properties.nodeRef?js_string}",
        	<#elseif form.mode == "edit" && args.itemId??>
        	nodeRef: "${args.itemId?js_string}",
        	<#else>
        	nodeRef: "",
        	</#if>
			currentUser: "${user.name?js_string}"
		}
		);
	})();
	//]]></script>

	<div id="${fieldHtmlId}_base" class="models">
		<!-- Field data -->
		<input id="${fieldHtmlId}" name="${field.name}" type="hidden" />
		<!-- Controls -->
		<div id="${fieldHtmlId}_loading">Загрузка данных...</div>
		<div id="${fieldHtmlId}_props" class="hidden1">
			<div id="${fieldHtmlId}_title"></div>
			<label><b>Категории вложений</b></label>
			<div id="${fieldHtmlId}_categories" class="form-field"></div>
			<label><b>Атрибуты</b></label>
			<div id="${fieldHtmlId}_attributes" class="form-container form-field"></div>
			<label><b>Ассоциации<b/></label>
			<div id="${fieldHtmlId}_associations" class="form-container form-field"></div>
			<label><b>Таблицы<b/></label>
			<div id="${fieldHtmlId}_tables" class="form-container form-field"></div>
		</div>
		<!-- Debug -->	
		<pre id="${fieldHtmlId}_console"></pre>
		<div id="${fieldHtmlId}_btn"></div>
		<!-- Debug end -->
	</div>
</#if>
<!-- //////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->
<#if form.mode == "view">

</#if>